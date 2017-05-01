package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.symbolsolver.model.declarations.ValueDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import jdk.nashorn.internal.ir.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameMethods implements IFileModifier {

    INameGenerator _nameGenerator;
    SymbolSolver _symbolSolver;

    public RenameMethods(INameGenerator nameGenerator, SymbolSolver symbolSolver){
        _symbolSolver = symbolSolver;
        _nameGenerator = nameGenerator;
    }
    
    @Override
    public void applyChanges(IObfuscatedFile file) {
        List<Node> nodesToReplace = new ArrayList<>();
        recurseAllNodes(file.getCompilationUnit(), nodesToReplace);
        nodesToReplace.forEach(nd -> replaceMethodUsages(nd));
    }

    private void recurseAllNodes(Node n, List<Node> nodesToReplace){
        checkNode(n, nodesToReplace);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, nodesToReplace));
    }

    private void checkNode(Node n, List<Node> nodesToReplace){
        Class c = n.getClass();

        if(c.getSimpleName().equals("MethodDeclaration")){
            MethodDeclaration methodName = (MethodDeclaration) (n);
            nodesToReplace.add(n);
        }


        if(c.getSimpleName().equals("MethodCallExpr")){
            MethodCallExpr methodName = (MethodCallExpr) (n);


            List<Type> types = new ArrayList<>();
            try{
                methodName.getTypeArguments().get().forEach(t -> types.add((Type) t));
            }catch (NoSuchElementException e){}

            try {
                MethodUsage methodUsage = _symbolSolver.solveMethod(
                        methodName.getNameAsString(),
                        types,
                        methodName);

                    if (methodUsage.getDeclaration() != null) {
                        nodesToReplace.add(n);
                    }
            }
            catch (Exception e) {
                int deleteThis = 1;
            }
        }
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