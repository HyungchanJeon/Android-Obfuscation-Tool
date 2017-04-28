package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.NodeList;
import com.sun.tools.classfile.Annotation;
import com.sun.tools.javac.code.Attribute;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameVariables implements IFileModifier {

    INameGenerator _nameGenerator;

    @Override
    public void rename(IObfuscatedFile file, INameGenerator nameGenerator) {
        _nameGenerator = nameGenerator;
        recurseAllNodes(file.getCompilationUnit(), file);
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file){
        replaceVariableUsages(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file));
    }

    private void replaceVariableUsages(Node n){

        Class c = n.getClass();
        if(c.getSimpleName().equals("NameExpr")){
            NameExpr variableUsage = (NameExpr) (n);
            String newVariableName = _nameGenerator.getVariableName(variableUsage.getNameAsString());
            variableUsage.setName(newVariableName);
        }

        if(c.getSimpleName().equals("VariableDeclarator")){
            VariableDeclarator variableUsage = (VariableDeclarator) (n);
            String newVariableName = _nameGenerator.getVariableName(variableUsage.getNameAsString());
            variableUsage.setName(newVariableName);
        }

        if(c.getSimpleName().equals("Parameter")){
            Parameter variableUsage = (Parameter) (n);
            String newVariableName = _nameGenerator.getVariableName(variableUsage.getNameAsString());
            variableUsage.setName(newVariableName);
        }
    }
}
