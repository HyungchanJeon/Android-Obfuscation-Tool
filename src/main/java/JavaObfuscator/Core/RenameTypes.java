package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameTypes implements IFileModifier {
    INameGenerator _nameGenerator;
    @Override
    public void rename(IObfuscatedFile file, INameGenerator nameGenerator) {
        _nameGenerator = nameGenerator;
        recurseAllNodes(file.getCompilationUnit(), file);
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file){
        replaceClassDefinitions(n, file);
        replaceClassOrInterfaces(n);
        replaceMethodDeclarations(n);
        replaceVariableDeclarations(n);

        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file));
    }

    private void replaceVariableDeclarations(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator variable = (VariableDeclarator)(n);
            String newTypeName = _nameGenerator.getClassName(variable.getName().toString());
            variable.setType(newTypeName);
        }
    }

    private void replaceMethodDeclarations(Node n) {
        Class c = n.getClass();
        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration method = (MethodDeclaration)(n);
            String newTypeName = _nameGenerator.getClassName(method.getName().toString());
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
