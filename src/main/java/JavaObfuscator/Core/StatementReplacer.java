package JavaObfuscator.Core;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import javax.swing.plaf.nimbus.State;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jack Barker on 28/04/2017.
 */
public class StatementReplacer {
    INameGenerator _nameGenerator;
    NextSwitchGenerator _nextSwitchGenerator = new NextSwitchGenerator();

    Integer _nextSwitchInt = 0;
    private String _variableName;

    public StatementReplacer(INameGenerator nameGenerator){
        _nextSwitchInt = _nextSwitchGenerator.getRandomInteger();
        _nameGenerator = nameGenerator;
        _variableName = nameGenerator.generateDistinct();
    }


    public BinaryExpr generateWhileCondition(){
        BinaryExpr condition = new BinaryExpr(new NameExpr(_variableName), new UnaryExpr(new IntegerLiteralExpr("1"), UnaryExpr.Operator
                .MINUS), BinaryExpr
                .Operator.NOT_EQUALS);

        return condition;
    }


    /**
     * Generates a switch statement in a while loop, given a node who's children should be in their own case
     * @param node
     * @return
     */
    public BlockStmt generateSwitchInWhile(Node node){
        _nextSwitchInt = _nextSwitchGenerator.getRandomInteger();
        //Pull out declarations
        NodeList<Expression> declarationStatements = new NodeList<>();
        NodeList<Statement> returnStatement = new NodeList<>();
        NodeList<Statement> superStatements = new NodeList<>();

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr(_nextSwitchInt
                + ""),
                AssignExpr.Operator.ASSIGN));

        Statement switchStatement = generateSwith(node, declarationStatements,  superStatements, returnStatement);

        Statement innerWhile = new WhileStmt(generateWhileCondition(), switchStatement);

        //Switch statement
        NodeList<Expression> stmts = new NodeList<>();

        declarationStatements.forEach(st -> stmts.add(st));
        BlockStmt block = new BlockStmt();

        superStatements.forEach(st -> block.addAndGetStatement(st));

        for(Expression n : stmts){
            block.addAndGetStatement(n);
        }

        ExpressionStmt swVarDeclaration = new ExpressionStmt(new VariableDeclarationExpr(PrimitiveType.intType(), _variableName));



        block.addAndGetStatement(swVarDeclaration);
        block.addAndGetStatement(swVarChange);

        if(!switchStatement.getClass().getSimpleName().equals("BlockStmt")){
            block.addAndGetStatement(innerWhile);
        }


        returnStatement.forEach(st -> block.addAndGetStatement(st));

        return block;
    }


    private NodeList<Statement> removeSuperCallsAndAddToList(NodeList<Statement> statements, NodeList<Statement> superStatements) {

        if(statements.size() > 0 && statements.get(0).getClass().getSimpleName().equals("ExplicitConstructorInvocationStmt")){
            superStatements.add(statements.get(0));
            statements.remove(0);
        }

        return statements;
    }


    /**
     * If it is a variable declaration statement, it will be replaced with a variable assignment
     * A variable declaration will be added to declarationStatements
     * @param stmt
     * @param declarationStatements
     * @return
     */
    private NodeList<Statement> removeDeclarationAndAddToList(Statement stmt, List<Expression> declarationStatements) {

        NodeList<Statement> assignments = new NodeList<Statement>();

        List<Node> releventChildren = stmt.getChildNodes().stream().filter(child -> child.getClass().getSimpleName().equals
                ("VariableDeclarationExpr")).collect(Collectors.toList());

        releventChildren.forEach(child -> {
            VariableDeclarator declarator = (VariableDeclarator) child.getChildNodes().get(0);
            declarationStatements.add(getObjectDeclaration(declarator));

            declarationStatements.add(new AssignExpr(new NameExpr(declarator.getName().toString()), new NullLiteralExpr(),
                    AssignExpr.Operator.ASSIGN));

            try{
                Expression expression = declarator.getInitializer().get();
                AssignExpr assignExpr = new AssignExpr(new NameExpr(declarator.getName().toString()), expression,
                        AssignExpr.Operator.ASSIGN);
                assignments.add(new ExpressionStmt(assignExpr));

            }catch(NoSuchElementException e){
            }

        });

        if(releventChildren.size() == 0){
            assignments.add(stmt);
        }

        return assignments;
    }

    private Expression getObjectDeclaration(VariableDeclarator declarator) {
        String name = declarator.getType().toString();

        Type actualType = declarator.getType();

        if(name.equals("boolean")){
            actualType = new ClassOrInterfaceType("Boolean");
        }

        if(name.equals("byte")){
            actualType = new ClassOrInterfaceType("Byte");
        }

        if(name.equals("char")){
            actualType = new ClassOrInterfaceType("Character");
        }

        if(name.equals("short")){
            actualType = new ClassOrInterfaceType("Short");
        }

        if(name.equals("int")){
            actualType = new ClassOrInterfaceType("Integer");
        }

        if(name.equals("long")){
            actualType = new ClassOrInterfaceType("Long");
        }

        if(name.equals("double")){
            actualType = new ClassOrInterfaceType("Double");
        }

        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(actualType, declarator.getName().toString());

        return variableDeclarationExpr;
    }


    /**
     * Generates a switch statement from a node, who's children should be different cases
     * @param node
     * @param declarationStatements
     * @return
     */
    private Statement generateSwith(Node node, List<Expression> declarationStatements,  NodeList<Statement> superStatements,
                                    List<Statement> lastStatement) {
        NodeList<SwitchEntryStmt> statements = new NodeList<>();

        BlockStmt block = (BlockStmt) node;

        NodeList<Statement> blockStatements = block.getStatements();

        if(blockStatements.size() > 0 && blockStatements.get(blockStatements.size() - 1).getClass().getSimpleName().equals("ReturnStmt")){
            lastStatement.add(blockStatements.get(blockStatements.size() - 1));
            blockStatements.remove(blockStatements.size() - 1);
        }

        if(blockStatements.size() == 0){
            return new BlockStmt(blockStatements);
        }

        int index = 1;
        String switchName = _nameGenerator.generateDistinct();

        for(Statement stmt : blockStatements) {
            statements.add(generateCaseStatement(stmt, index, declarationStatements, superStatements, node, block.getStatements().size(),
                    switchName));
            index++;
        }

        Collections.shuffle(statements);
        
        SwitchStmt switchStmt = new SwitchStmt((Expression)(new NameExpr(_variableName)), statements);

        LabeledStmt lbl = new LabeledStmt(switchName, switchStmt);

        return lbl;
    }

    private SwitchEntryStmt generateCaseStatement(Statement stmt, Integer index, List<Expression> declarationStatements,
                                                  NodeList<Statement> superStatements,
                                                     Node
            node, int max, String switchName) {
        NodeList<Statement> statements = new NodeList<Statement>();



        statements.addAll(removeSuperCallsAndAddToList(removeDeclarationAndAddToList(stmt, declarationStatements), superStatements));


        int nextSwVar = _nextSwitchGenerator.getRandomInteger();

        if(index == max){
            nextSwVar = -1;
        }

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr(nextSwVar + ""),
                AssignExpr.Operator.ASSIGN));

        if(!statements.stream().anyMatch(st -> st.getClass().getSimpleName().equals("ReturnStmt") || st.getClass().getSimpleName().equals
                ("BreakStmt"))){
            statements.add(swVarChange);
            statements.add(new BreakStmt(switchName));
        }

        NodeList<Statement> bs = new NodeList<>();

        bs.add(new BlockStmt(statements));

        SwitchEntryStmt switchEntryStmt = new SwitchEntryStmt(new IntegerLiteralExpr(_nextSwitchInt.toString()), bs);

        _nextSwitchInt = nextSwVar;

        return switchEntryStmt;
    }


    public String getVariableName() {
        return _variableName;
    }

    public void resetName() {
        _variableName = _nameGenerator.generateDistinct();
    }
}
