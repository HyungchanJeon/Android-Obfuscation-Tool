package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.NodeList;
import com.sun.javadoc.AnnotationDesc;
import com.sun.tools.classfile.Annotation;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.TypeAnnotations;
import com.sun.xml.internal.xsom.parser.AnnotationContext;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameMethods implements IFileModifier {

    INameGenerator _nameGenerator;

    @Override
    public void rename(IObfuscatedFile file, INameGenerator nameGenerator) {
        _nameGenerator = nameGenerator;
        recurseAllNodes(file.getCompilationUnit(), file);
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file){
        replaceMethodUsages(n);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file));
    }

    private void replaceMethodUsages(Node n){
        Class c = n.getClass();

        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration methodName = (MethodDeclaration) (n);
            NodeList<AnnotationExpr> annotations = methodName.getAnnotations();
            String newMethodName = _nameGenerator.getMethodName(methodName.getNameAsString());
            if (!methodName.isAnnotationPresent("Override")){
                methodName.setName(newMethodName);
            } else {
                _nameGenerator.setMethodName(newMethodName);
            }
        }

        if(c.getSimpleName().equals("MethodCallExpr")){
            MethodCallExpr methodName = (MethodCallExpr) (n);
            String newMethodName = _nameGenerator.getMethodName(methodName.getNameAsString());
            methodName.setName(newMethodName);
        }
    }
}
