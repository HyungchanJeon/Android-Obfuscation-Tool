package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import jdk.nashorn.internal.ir.Block;
import sun.tools.tree.CaseStatement;
import sun.tools.tree.DeclarationStatement;
import sun.tools.tree.UnaryExpression;
import sun.tools.tree.WhileStatement;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class WhileReplacer implements IFileModifier {
    INameGenerator _nameGenerator;
    IStatementGenerator _statementGenerator;
    private Object List;

    public WhileReplacer(INameGenerator nameGenerator, IStatementGenerator statementGenerator){
        _nameGenerator = _nameGenerator;
        _statementGenerator = statementGenerator;
    }


    @Override
    public void applyChanges(IObfuscatedFile file) {
        ArrayList<Node> whileNodes = new ArrayList<Node>();
        recurseAllNodes(file.getCompilationUnit(), whileNodes);
        whileNodes.forEach(node -> changeNodeToSwitch(node));
    }

    /**
     * This method changes any whiles loops to switches:
     *
     * Eg:
     *  int c = 10;
        while(c != 0) {
            System.out.println(c);
            c--;
        }

        is converted to:

         int c = 10;
         int swVar = 1;
         while (swVar != -1) {
             swVar = 1;
             while (swVar != -1 && swVar != 0)
                switch(swVar) {
                     case 1:
                         if (c != 0) {
                            System.out.println(c);
                            swVar = 2;
                            break  ;
                         } else {
                            swVar = -1;
                            break  ;
                        }
                     case 2:
                        c--;
                        swVar = 0;
                        break  ;
             }
         }
     * @param node
     */
    private void changeNodeToSwitch(Node node) {
        Node parent = node.getParentNode().get();

        BlockStmt parentBlock = (BlockStmt)parent;
        int statementInsertLocation = parentBlock.getStatements().indexOf(node);

        List<Statement> originalStatements = parentBlock.getStatements();

        parentBlock.setStatements(new NodeList<>());
        originalStatements.subList(0, statementInsertLocation).forEach(stmt -> parentBlock.addAndGetStatement(stmt));

        //add "int swVar = 1"
        Node swVarNode = generateSwVar();
        parentBlock.addAndGetStatement((Expression)swVarNode);

        //Any declaration statements that are in the while loop
        NodeList<VariableDeclarationExpr> declarationStatements = new NodeList<>();


        Statement switchStatement = generateSwith(node, declarationStatements);

        //Inner while expression
        BinaryExpr left = new BinaryExpr(new NameExpr("swVar"), new UnaryExpr(new IntegerLiteralExpr("1"), UnaryExpr.Operator.MINUS), BinaryExpr
                .Operator.NOT_EQUALS);
        BinaryExpr right = new BinaryExpr(new NameExpr("swVar"), new IntegerLiteralExpr("0"), BinaryExpr.Operator.NOT_EQUALS);
        BinaryExpr combined = new BinaryExpr(left, right, BinaryExpr.Operator.AND);

        WhileStmt innerWhile = new WhileStmt(combined, switchStatement);

        //Switch statement
        NodeList<Expression> stmts = new NodeList<>();

        declarationStatements.forEach(st -> stmts.add(st));
        BlockStmt block = new BlockStmt();

        for(Expression n : stmts){
            block.addAndGetStatement(n);
        }

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr("swVar"), new IntegerLiteralExpr("1"),
                AssignExpr.Operator.ASSIGN));

        block.addAndGetStatement(swVarChange);

        block.addAndGetStatement(innerWhile);

        WhileStmt outerWhile = new WhileStmt(left, block);

        parentBlock.addAndGetStatement(outerWhile);

        //Add rest of the function
        int size = originalStatements.size();
        originalStatements.subList(statementInsertLocation + 1, size).forEach(stmt -> parentBlock.addAndGetStatement(stmt));
    }

    private Statement generateSwith(Node node, List<VariableDeclarationExpr> declarationStatements) {
        NodeList<SwitchEntryStmt> statements = new NodeList<>();

        for(Node child : node.getChildNodes()){
            if(child.getClass().getSimpleName().equals("BlockStmt")){
                BlockStmt block = (BlockStmt) child;
                int index = 1;
                for(Statement stmt : block.getStatements()){
                    statements.add(generateStatement(stmt, index, declarationStatements, node, block.getStatements().size()));
                    index++;
                }
            }
        }

        SwitchStmt switchStmt = new SwitchStmt((Expression)(new NameExpr("swVar")), statements);

        return switchStmt;
    }

    private SwitchEntryStmt generateStatement(Statement stmt, Integer index, List<VariableDeclarationExpr> declarationStatements, Node
            node, int max) {
        NodeList<Statement> statements = new NodeList<Statement>();

        statements.add(removeDeclaration(stmt, declarationStatements));

        if(index == 1){
            //Exit condition handling
            BinaryExpr condition = (BinaryExpr)node.getChildNodes().get(0);

            ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr("swVar"), new IntegerLiteralExpr("-1"),
                    AssignExpr.Operator.ASSIGN));

            int nextSwVar = index + 1;

            if(index == max){
                nextSwVar = 0;
            }

            ExpressionStmt swVarChange2 = new ExpressionStmt(new AssignExpr(new NameExpr("swVar"), new IntegerLiteralExpr(nextSwVar + ""),
                    AssignExpr.Operator.ASSIGN));

            statements.add(swVarChange2);


            statements.add(new BreakStmt(" "));


            NodeList<Statement> exitStatements = new NodeList<Statement>();
            exitStatements.add(swVarChange);
            exitStatements.add(new BreakStmt(" "));

            IfStmt condMetStmt = new IfStmt(condition, new BlockStmt(statements), new BlockStmt(exitStatements));

            NodeList<Statement> s = new NodeList<>();
            s.add(condMetStmt);
            SwitchEntryStmt switchEntryStmt = new SwitchEntryStmt(new IntegerLiteralExpr(index.toString()), s);

            return switchEntryStmt;
        }

        int nextSwVar = index + 1;

        if(index == max){
            nextSwVar = 0;
        }

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr("swVar"), new IntegerLiteralExpr(nextSwVar + ""),
                AssignExpr.Operator.ASSIGN));

        statements.add(swVarChange);
        statements.add(new BreakStmt(" "));

        SwitchEntryStmt switchEntryStmt = new SwitchEntryStmt(new IntegerLiteralExpr(index.toString()), statements);

        return switchEntryStmt;
    }

    private Statement removeDeclaration(Node node, List<VariableDeclarationExpr> declarationStatements) {
        if(node.getChildNodes().get(0).getClass().getSimpleName().equals("VariableDeclarationExpr")){

            VariableDeclarator declarator = (VariableDeclarator) node.getChildNodes().get(0).getChildNodes().get(0);


            declarationStatements.add(new VariableDeclarationExpr(declarator.getType(), declarator.getName().toString()));

            return new ExpressionStmt(new AssignExpr(new NameExpr(declarator.getName().toString()), declarator.getInitializer().get(),
                    AssignExpr.Operator.ASSIGN));
        }
        return (Statement) node;
    }

    private Node generateSwVar(){
        return new VariableDeclarationExpr(new VariableDeclarator(new PrimitiveType().setType(PrimitiveType.Primitive
                .INT), "swVar", new IntegerLiteralExpr("1")));
    }

    private void recurseAllNodes(Node n, ArrayList<Node> whileNodes){

        Class c = n.getClass();
        if(c.getSimpleName().equals("WhileStmt")){
            whileNodes.add(n);
        }
        //generateSwitch(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, whileNodes));
    }

}