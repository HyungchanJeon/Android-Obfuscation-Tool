package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.sun.glass.ui.EventLoop;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.tools.internal.ws.processor.model.Block;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Lenovo on 4/29/2017.
 */
public class MethodInliner implements IFileModifier {
    INameGenerator _nameGenerator;
    private HashMap<String, MethodDeclaration> _privateMethodBodyNodes = new HashMap<String, MethodDeclaration>();
    private ArrayList<MethodCallExpr> _methodCallExpressions = new ArrayList<MethodCallExpr>();

    public MethodInliner(INameGenerator nameGenerator){
        _nameGenerator = nameGenerator;
    }

    @Override
    public void applyChanges(IObfuscatedFile file) {
        inlineMethods(file.getCompilationUnit());
    }

    private void inlineMethods(Node n){
        findPrivateMethods(n);
        inlineMethodCalls();
    }

    private void inlineMethodCalls(){

        //Iterate through all method calls we found, and check if they can be inlined.
        for (MethodCallExpr methodCall : _methodCallExpressions){
            loopIteration:
            if (_privateMethodBodyNodes.containsKey(methodCall.getNameAsString())){

                //Find top level block statement where the method call is used and the statement where the method is called.
                Node topStmtNode = methodCall;
                Node methodCallNode = methodCall;
                if (topStmtNode.getParentNode().isPresent()){
                    while (!topStmtNode.getClass().getSimpleName().equals("BlockStmt")){
                        methodCallNode = topStmtNode;
                        if (topStmtNode.getParentNode().isPresent()){
                            topStmtNode = topStmtNode.getParentNode().get();
                        } else {
                            break loopIteration;
                        }
                    }
                }
                BlockStmt oldBlock = (BlockStmt) topStmtNode;

                //Get a copy of the body from the method that is being called.
                BlockStmt methodBlock = (BlockStmt) _privateMethodBodyNodes.get(methodCall.getNameAsString()).getBody().get().clone();

                //Rename the variables in the block to the names passed as arguments in the method call
                // Return a list of variables to declare and assign to non-named/scoped arguments
                ArrayList<Statement> literalAssignments = renameParamVariables(methodCall, methodBlock);

                //Find the index of the statement where the method is called
                int methodCallIndex = 0;
                NodeList<Statement> oldBlockStatements = oldBlock.getStatements();
                for (int i = 0; i < oldBlockStatements.size(); i++){
                    if (oldBlockStatements.get(i).equals(methodCallNode)) methodCallIndex = i;
                }

                //Generate label for the while loop which will encase the method contents.
                String LABEL = _nameGenerator.generateDistinct();

                //Find the expression (variable) the value returned by the method is assigned too.
                Expression returnExpression = null;
                if (methodCallNode.getClass().getSimpleName().equals("ReturnStmt")){

                    ReturnStmt methodCallReturnStatement = (ReturnStmt) methodCallNode;
                    Expression methodCallExpression = methodCallReturnStatement.getExpression().get();
                    returnExpression = methodCallExpression;

                } else {

                    ExpressionStmt methodCallExprStatement = (ExpressionStmt) methodCallNode;
                    Expression methodCallExpression = methodCallExprStatement.getExpression();
                    if ((methodCallExpression.getClass().getSimpleName().equals("AssignExpr"))){

                        AssignExpr expr = (AssignExpr) methodCallExpression;
                        returnExpression = expr.getTarget();

                    } else if ((methodCallExpression.getClass().getSimpleName().equals("VariableDeclarationExpr"))){

                        VariableDeclarationExpr dec = (VariableDeclarationExpr) methodCallExpression;
                        returnExpression =  new NameExpr(dec.getVariable(0).getNameAsString());
                    }
                }

                //Get top level statements of method body, and add break at the end to simulate returned from a method when the end is reached.
                NodeList<Statement> methodBlockStatements = methodBlock.getStatements();

                //Final block that will contain while-wrapped method and any declared variables the method originally returned.
                BlockStmt finalBlock = new BlockStmt();

                //If the method callNode isn't a return statement, then find all return statements and replace with variable assignments and break statements to simulate returns.
                if (!methodCallNode.getClass().getSimpleName().equals("ReturnStmt")) {

                    ArrayList<ReturnStmt> returnStmts = new ArrayList<ReturnStmt>();
                    convertReturnStatements(methodBlock, LABEL, returnExpression, returnStmts);
                    if (returnStmts.size() == 0) methodBlockStatements.add(methodBlockStatements.size(), new BreakStmt(LABEL));

                    Expression methodCallExpression = ((ExpressionStmt) methodCallNode).getExpression();

                    //If the method callNode is a variable declaration as well, declare the same variable before the method inlining (outside the while-wrapper)
                    if (methodCallExpression.getClass().getSimpleName().equals("VariableDeclarationExpr")) {

                        VariableDeclarationExpr variableDec = (VariableDeclarationExpr) methodCallExpression;
                        finalBlock.addStatement(0, new AssignExpr(new VariableDeclarationExpr(variableDec.getVariable(0).getType(), variableDec.getVariable(0).getNameAsString()),new NullLiteralExpr(), AssignExpr.Operator.ASSIGN));
                    }
                }

                //Add literal assignments at the top of the method body
                for (Statement literalAssignment: literalAssignments){
                    finalBlock.addStatement(0, literalAssignment);
                }

                //Build labelled infinite while loop to wrap the method contents
                BinaryExpr condition = new BinaryExpr(new UnaryExpr(new IntegerLiteralExpr("1"), UnaryExpr.Operator.MINUS), new UnaryExpr(new IntegerLiteralExpr("1"), UnaryExpr.Operator.MINUS), BinaryExpr.Operator.EQUALS);
                WhileStmt whileWrapper = new WhileStmt(condition, new BlockStmt(methodBlockStatements));
                LabeledStmt whileWrapperWithLabel = new LabeledStmt(LABEL, whileWrapper);

                //Populate final block with method statements
                finalBlock.addStatement(whileWrapperWithLabel);

                //Remove old method call and replace it with while-wrapped method body.
                oldBlockStatements.remove(methodCallIndex);
                oldBlockStatements.addAll(methodCallIndex, finalBlock.getStatements());
                oldBlock.setStatements(oldBlockStatements);
            }
        }
    }

    private void convertReturnStatements(Node node, String breakLabel, Expression returnVariableName, ArrayList<ReturnStmt> returnStmts){

        //Check if the node passed is a BlockStmt - if so we can check its statements.
        if (node.getClass().getSimpleName().equals("BlockStmt")){

            //Iterate through the blocks statements checking for return statements.
            BlockStmt body = (BlockStmt) node;
            NodeList<Statement> methodBlockStatements = body.getStatements();
            for (int i = 0; i < methodBlockStatements.size(); i++){
                Statement methodBlockStatement = methodBlockStatements.get(i);

                //If we find a return statement replace it with an assign expression
                if (methodBlockStatement.getClass().getSimpleName().equals("ReturnStmt")){
                    ReturnStmt returnStmt = (ReturnStmt) methodBlockStatement;
                    returnStmts.add(returnStmt);
                    Expression expressionBeingReturned = returnStmt.getExpression().get();

                    AssignExpr assignExpr = new AssignExpr(returnVariableName, expressionBeingReturned, AssignExpr.Operator.ASSIGN);
                    methodBlockStatements.set(i, new ExpressionStmt(assignExpr));
                    methodBlockStatements.add(i + 1, new BreakStmt(breakLabel));
                }
            }
            body.setStatements(methodBlockStatements);
        }

        //Recursively find all other Block statements
        node.getChildNodes().stream().forEach(n -> convertReturnStatements(n, breakLabel, returnVariableName, returnStmts));
    }

    private ArrayList<Statement> renameParamVariables(MethodCallExpr methodCall, BlockStmt body){

        HashMap<String, String> _scopedNameExpressions = new HashMap<String, String>();
        ArrayList<Statement> literalAssignments = new ArrayList<Statement>();
        HashSet<String> paramNames = new HashSet<String>();
        MethodDeclaration methodDec = _privateMethodBodyNodes.get(methodCall.getNameAsString());
        NodeList<Parameter> methodParameters = methodDec.getParameters();
        NodeList<Expression> methodArguments = methodCall.getArguments();

        //Build mapping of expressions/names used in the method call arguments and names used as parameters in the method declaration
        for (int i = 0; i < methodParameters.size(); i++){
            if (methodArguments.get(i).getClass().getSimpleName().equals("NameExpr")){
                _scopedNameExpressions.put(methodParameters.get(i).getNameAsString(), ((NameExpr)(methodArguments.get(i))).getNameAsString());
                paramNames.add(methodParameters.get(i).getNameAsString());
            } else {
                literalAssignments.add(new ExpressionStmt(assignLiteralArgument(methodArguments.get(i), methodParameters.get(i))));
            }
        }

        //Build list of all variables scoped to this method body.
        HashSet<String> methodDecVariables = new HashSet<String>();
        getLocallyScopedVariables(body, methodDecVariables);

        //Using mapping recursively rename all variables in the method.
        renameNamedVariablesRecursively(body, _scopedNameExpressions, paramNames, methodDecVariables);

        return literalAssignments;
    }

    private AssignExpr assignLiteralArgument(Expression argument, Parameter param) {
        return new AssignExpr(new VariableDeclarationExpr(param.getType(), param.getNameAsString()), argument,
                AssignExpr.Operator.ASSIGN);
    }

    private void renameNamedVariablesRecursively(Node n, HashMap<String, String> _scopedNameExpressions, HashSet<String> paramNames, HashSet<String> methodDecVariables){

        Class c = n.getClass();

        if(c.getSimpleName().equals("NameExpr")){
            NameExpr expr = (NameExpr) (n);
            if (_scopedNameExpressions.containsKey(expr.getNameAsString())){
                expr.setName(_scopedNameExpressions.get(expr.getNameAsString()));
            } else if (!paramNames.contains(expr.getNameAsString()) && methodDecVariables.contains(expr.getNameAsString())){
                String newVariableName = _nameGenerator.getVariableName(expr.getNameAsString());
                expr.setName(newVariableName);
            }
        }
        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator expr = (VariableDeclarator) (n);
            if (_scopedNameExpressions.containsKey(expr.getNameAsString())){
                expr.setName(_scopedNameExpressions.get(expr.getNameAsString()));
            } else if (!paramNames.contains(expr.getNameAsString()) && methodDecVariables.contains(expr.getNameAsString())){
                String newVariableName = _nameGenerator.getVariableName(expr.getNameAsString());
                expr.setName(newVariableName);
            }
        }
        if(c.getSimpleName().equals("Parameter")){
            Parameter expr = (Parameter) (n);
            if (_scopedNameExpressions.containsKey(expr.getNameAsString())){
                expr.setName(_scopedNameExpressions.get(expr.getNameAsString()));
            } else if (!paramNames.contains(expr.getNameAsString())&& methodDecVariables.contains(expr.getNameAsString())){
                String newVariableName = _nameGenerator.getVariableName(expr.getNameAsString());
                expr.setName(newVariableName);
            }
        }
        n.getChildNodes().stream().forEach(node -> renameNamedVariablesRecursively(node, _scopedNameExpressions, paramNames, methodDecVariables));
    }

    private void getLocallyScopedVariables(Node n, HashSet<String> methodDecVariables){

        Class c = n.getClass();
        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator expr = (VariableDeclarator) (n);
            methodDecVariables.add(expr.getNameAsString());
        }
        n.getChildNodes().stream().forEach(node -> getLocallyScopedVariables(node, methodDecVariables));
    }

    private void findPrivateMethods(Node n){
        Class c = n.getClass();

        //If we find a method declaration build a mapping between its name and its method body.
        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration methodDec = (MethodDeclaration) (n);

            //Only add methods that are private (As non-private methods cannot be inlined due to scoping issues)
            if (methodDec.getModifiers().contains(Modifier.PRIVATE)){
                if (methodDec.getBody().isPresent()){
                    System.out.println("found declaration: " + methodDec.getNameAsString());
                    _privateMethodBodyNodes.put(methodDec.getNameAsString(), methodDec);
                }
            }
        }

        //If we find a method call expression add it to a list of method calls to replace with inlined method contents
        if(c.getSimpleName().equals("MethodCallExpr")){
            MethodCallExpr methodCall = (MethodCallExpr) (n);
            _methodCallExpressions.add(methodCall);
        }

        //Recursively search for more method declarations and calls.
        n.getChildNodes().stream().forEach(node -> findPrivateMethods(node));
    }
}
