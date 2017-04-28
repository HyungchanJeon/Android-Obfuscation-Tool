package JavaObfuscator.Core;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.PrimitiveType;

import java.util.List;

/**
 * Created by Jack Barker on 28/04/2017.
 */
public class StatementReplacer {
    INameGenerator _nameGenerator;

    private String _variableName;

    public StatementReplacer(INameGenerator nameGenerator){
        _nameGenerator = nameGenerator;
        _variableName = nameGenerator.generateDistinct();
    }

    public Node generateSwVarInitialiser(){
        return new VariableDeclarationExpr(new VariableDeclarator(new PrimitiveType().setType(PrimitiveType.Primitive
                .INT), _variableName, new IntegerLiteralExpr("1")));
    }

    public BlockStmt generateSwitchInWhile(Node node){
        //Any declaration statements that are in the while loop
        NodeList<VariableDeclarationExpr> declarationStatements = new NodeList<>();


        Statement switchStatement = generateSwith(node, declarationStatements);

        //Inner while expression
        BinaryExpr left = new BinaryExpr(new NameExpr(_variableName), new UnaryExpr(new IntegerLiteralExpr("1"), UnaryExpr.Operator.MINUS), BinaryExpr
                .Operator.NOT_EQUALS);
        BinaryExpr right = new BinaryExpr(new NameExpr(_variableName), new IntegerLiteralExpr("0"), BinaryExpr.Operator.NOT_EQUALS);
        BinaryExpr combined = new BinaryExpr(left, right, BinaryExpr.Operator.AND);

        WhileStmt innerWhile = new WhileStmt(combined, switchStatement);

        //Switch statement
        NodeList<Expression> stmts = new NodeList<>();

        declarationStatements.forEach(st -> stmts.add(st));
        BlockStmt block = new BlockStmt();

        for(Expression n : stmts){
            block.addAndGetStatement(n);
        }

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr("1"),
                AssignExpr.Operator.ASSIGN));

        block.addAndGetStatement(swVarChange);

        block.addAndGetStatement(innerWhile);

        return block;
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

        SwitchStmt switchStmt = new SwitchStmt((Expression)(new NameExpr(_variableName)), statements);

        return switchStmt;
    }

    private SwitchEntryStmt generateStatement(Statement stmt, Integer index, List<VariableDeclarationExpr> declarationStatements,
                                                     Node
            node, int max) {
        NodeList<Statement> statements = new NodeList<Statement>();

        statements.add(removeDeclaration(stmt, declarationStatements));

        if(index == 1){
            //Exit condition handling
            BinaryExpr condition = (BinaryExpr)node.getChildNodes().get(0);

            ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr("-1"),
                    AssignExpr.Operator.ASSIGN));

            int nextSwVar = index + 1;

            if(index == max){
                nextSwVar = 0;
            }

            ExpressionStmt swVarChange2 = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr(nextSwVar + ""),
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

        ExpressionStmt swVarChange = new ExpressionStmt(new AssignExpr(new NameExpr(_variableName), new IntegerLiteralExpr(nextSwVar + ""),
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


    public String getVariableName() {
        return _variableName;
    }

    public void resetName() {
        _variableName = _nameGenerator.generateDistinct();
    }
}
