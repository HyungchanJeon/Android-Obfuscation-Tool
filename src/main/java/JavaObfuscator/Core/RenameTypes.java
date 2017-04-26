package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.sun.org.apache.xpath.internal.operations.Variable;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameTypes implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        recurseAllNodes(file.getCompilationUnit(), oldTypeName, newTypeName);
    }

    private void recurseAllNodes(Node n, String oldTypeName, String newTypeName){
        replaceClassDefinitions(n, oldTypeName, newTypeName);
        replaceClassOrInterfaces(n, oldTypeName, newTypeName);
        replaceMethodDeclarations(n, oldTypeName, newTypeName);
        replaceVariableDeclarations(n, oldTypeName, newTypeName);

        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, oldTypeName, newTypeName));
    }

    private void replaceVariableDeclarations(Node n, String oldTypeName, String newTypeName) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator variable = (VariableDeclarator)(n);
            if(variable.getType().toString().equals(oldTypeName)) {
                variable.setType(newTypeName);
            }
        }
    }

    private void replaceMethodDeclarations(Node n, String oldTypeName, String newTypeName) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration method = (MethodDeclaration)(n);
            if(method.getType().toString().equals(oldTypeName)) {
                method.setType(newTypeName);
            }
        }
    }

    private void replaceClassOrInterfaces(Node n, String oldTypeName, String newTypeName) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("ClassOrInterfaceType")){
            ClassOrInterfaceType type = (ClassOrInterfaceType)(n);
            if(type.getName().toString().equals(oldTypeName)){
                type.getName().setIdentifier(newTypeName);
            }
        }
    }

    private void replaceClassDefinitions(Node n, String oldTypeName, String newTypeName) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("ClassOrInterfaceDeclaration")){
            ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration)(n);
            if(type.getName().toString().equals(oldTypeName)){
                type.getName().setIdentifier(newTypeName);
            }
        }
    }

}
