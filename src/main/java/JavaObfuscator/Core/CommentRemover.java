package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Removes comments from the java source AST
 *
 * Created by Jack Barker on 5/04/2017.
 */
public class CommentRemover implements IFileModifier {
    INameGenerator _nameGenerator;

    @Override
    public void applyChanges(IObfuscatedFile file) {
        recurseAllNodes(file.getCompilationUnit(), file);
    }

    /**
     * Recursively remove all comments in the AST
     *
     * @param n Node to traverse recursively
     * @param file File containing java source code to remove comments in
     */
    private void recurseAllNodes(Node n, IObfuscatedFile file){
        removeComments(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file));
    }


    /**
     * Remove comments from the specified node
     *
     * @param n Node to remove comments from
     */
    private void removeComments(Node n) {
        System.out.println(n.getComment());
        n.setComment(null);
    }
}