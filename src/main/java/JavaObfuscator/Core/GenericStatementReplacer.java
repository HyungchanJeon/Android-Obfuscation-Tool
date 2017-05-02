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
 * The file modifer for statement replacer
 */
public class GenericStatementReplacer implements IFileModifier {
    INameGenerator _nameGenerator;
    IStatementGenerator _statementGenerator;

    StatementReplacer _statementReplacer;

    private Object List;

    /**
     * Constructor
     * @param nameGenerator
     * @param statementGenerator
     */
    public GenericStatementReplacer(INameGenerator nameGenerator, IStatementGenerator statementGenerator){
        _nameGenerator = nameGenerator;
        _statementGenerator = statementGenerator;
        _statementReplacer = new StatementReplacer(_nameGenerator);
    }

    /**
     * Applies the changes
     * @param file
     */
    @Override
    public void applyChanges(IObfuscatedFile file) {
        ArrayList<Node> blockStatements = new ArrayList<Node>();
        recurseAllNodes(file.getCompilationUnit(), blockStatements);
        blockStatements.forEach(node -> flatten(node));
    }

    /**
     * This method changes any statements to switches:
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
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, blockStatements));
    }
}