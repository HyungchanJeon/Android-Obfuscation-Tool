package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.PrimitiveType;
import jdk.nashorn.internal.ir.Block;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class WhileReplacer implements IFileModifier {
    INameGenerator _nameGenerator;
    IStatementGenerator _statementGenerator;

    StatementReplacer _statementReplacer;

    private Object List;

    public WhileReplacer(INameGenerator nameGenerator, IStatementGenerator statementGenerator){
        _nameGenerator = nameGenerator;
        _statementGenerator = statementGenerator;
        _statementReplacer = new StatementReplacer(_nameGenerator);
    }


    @Override
    public void applyChanges(IObfuscatedFile file) {
        ArrayList<Node> whileNodes = new ArrayList<Node>();
        recurseAllNodes(file.getCompilationUnit(), whileNodes);
        whileNodes.forEach(node -> changeWhileToSwitch(node));
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
    private void changeWhileToSwitch(Node node) {
        _statementReplacer.resetName();

        Node parent = node.getParentNode().get();
        BlockStmt parentBlock = (BlockStmt)parent;
        int statementInsertLocation = parentBlock.getStatements().indexOf(node);

        List<Statement> originalStatements = parentBlock.getStatements();

        parentBlock.setStatements(new NodeList<>());
        originalStatements.subList(0, statementInsertLocation).forEach(stmt -> parentBlock.addAndGetStatement(stmt));
        //add "int swVar = 1"
        Node swVarNode = _statementReplacer.generateSwVarInitialiser();
        parentBlock.addAndGetStatement((Expression)swVarNode);

        BinaryExpr outerWhileExpression = new BinaryExpr(new NameExpr(_statementReplacer.getVariableName()), new UnaryExpr(new
                IntegerLiteralExpr("1"), UnaryExpr
                .Operator.MINUS),
                BinaryExpr
                .Operator.NOT_EQUALS);

        WhileStmt outerWhile = new WhileStmt(outerWhileExpression, _statementReplacer.generateSwitchInWhile(node));

        parentBlock.addAndGetStatement(outerWhile);

        //Add rest of the function
        int size = originalStatements.size();
        originalStatements.subList(statementInsertLocation + 1, size).forEach(stmt -> parentBlock.addAndGetStatement(stmt));
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