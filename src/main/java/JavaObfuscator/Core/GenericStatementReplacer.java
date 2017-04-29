package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import jdk.nashorn.internal.ir.Block;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class GenericStatementReplacer implements IFileModifier {
    INameGenerator _nameGenerator;
    IStatementGenerator _statementGenerator;

    StatementReplacer _statementReplacer;

    private Object List;

    public GenericStatementReplacer(INameGenerator nameGenerator, IStatementGenerator statementGenerator){
        _nameGenerator = nameGenerator;
        _statementGenerator = statementGenerator;
        _statementReplacer = new StatementReplacer(_nameGenerator);
    }


    @Override
    public void applyChanges(IObfuscatedFile file) {
        ArrayList<Node> blockStatements = new ArrayList<Node>();
        recurseAllNodes(file.getCompilationUnit(), blockStatements);
        blockStatements.forEach(node -> flatten(node));
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
    private void flatten(Node node) {
        _statementReplacer.resetName();

        BlockStmt parentBlock = (BlockStmt)node;

        BlockStmt newNode = _statementReplacer.generateSwitchInWhile(node);
        parentBlock.setStatements(newNode.getStatements());
    }

    private void recurseAllNodes(Node n, ArrayList<Node> blockStatements){
        Class c = n.getClass();
        if(c.getSimpleName().equals("BlockStmt")){
            blockStatements.add(n);
        }
        //generateSwitch(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, blockStatements));
    }
}