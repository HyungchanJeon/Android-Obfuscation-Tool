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
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameTypes implements IFileModifier {
    INameGenerator _nameGenerator;

    public RenameTypes(INameGenerator nameGenerator){
        _nameGenerator = nameGenerator;
    }

    @Override
    public void applyChanges(IObfuscatedFile file) {
        recurseAllNodes(file.getCompilationUnit(), file);
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file){
        replaceClassDefinitions(n, file);
        replaceClassOrInterfaces(n);
        replaceMethodDeclarations(n);
        replaceVariableDeclarations(n);
        replaceTypeArguments(n);
        replaceConstructors(n);
        replaceNameExpressions(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file));
    }

    private void replaceNameExpressions(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("NameExpr")){
            NameExpr variable = (NameExpr)(n);
            variable.setName(_nameGenerator.getClassName(variable.getName().toString()));

        }
    }

    private void replaceConstructors(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("ConstructorDeclaration")){
            ConstructorDeclaration variable = (ConstructorDeclaration)(n);
            variable.setName(_nameGenerator.getClassName(variable.getName().toString()));

        }
    }

    private void replaceTypeArguments(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("FieldDeclaration")){
            try {
                FieldDeclaration variable = (FieldDeclaration)(n);
                String variableTypeName = variable.getVariable(0).getType().toString();
                if(variableTypeName.contains("<")){
                    String innerType = variableTypeName.substring(variableTypeName.indexOf("<") + 1, variableTypeName.indexOf(">"));
                    String newInnerType = _nameGenerator.getClassName(innerType);
                    variable.getVariable(0).setType(new ClassOrInterfaceType(variableTypeName.substring(0, variableTypeName.indexOf("<"))
                            + "<" + newInnerType + ">"
                    ));
                }
            }catch (NoSuchElementException e){}
        }
    }

    private void replaceVariableDeclarations(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator variable = (VariableDeclarator)(n);
            String newTypeName = _nameGenerator.getClassName(variable.getType().toString());
            variable.setType(newTypeName);
        }
    }

    private void replaceMethodDeclarations(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration method = (MethodDeclaration)(n);
            String newTypeName = _nameGenerator.getClassName(method.getType().toString());
            method.setType(newTypeName);

        }
    }

    private void replaceClassOrInterfaces(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("ClassOrInterfaceType")){
            ClassOrInterfaceType type = (ClassOrInterfaceType)(n);
            String newTypeName = _nameGenerator.getClassName(type.getName().toString());
            type.getName().setIdentifier(newTypeName);

        }
    }

    private void replaceClassDefinitions(Node n, IObfuscatedFile file) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("ClassOrInterfaceDeclaration")){
            ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration)(n);
            String newTypeName = _nameGenerator.getClassName(type.getName().toString());

            if(file.getFileName().equals(type.getName().toString() + ".java")){
                file.setFileName(newTypeName + ".java");
            }

            type.getName().setIdentifier(newTypeName);
        }
    }
}