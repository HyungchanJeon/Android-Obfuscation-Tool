package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.model.declarations.*;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameVariables implements IFileModifier {

    private CombinedTypeSolver _combinedTypeSolver;
    private INameGenerator _nameGenerator;
    private SymbolSolver _symbolSolver;

    public RenameVariables(INameGenerator nameGenerator, CombinedTypeSolver combinedTypeSolver, SymbolSolver symbolSolver){
        _symbolSolver = symbolSolver;
        _combinedTypeSolver = combinedTypeSolver;
        _nameGenerator = nameGenerator;
    }
    private void removeUnknown(Node node, HashMap<ClassOrInterfaceDeclaration, ArrayList<ClassOrInterfaceType>> nodes) {
        if(node.getClass().getSimpleName().equals("ClassOrInterfaceDeclaration")){
            ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration)node;
            List<ClassOrInterfaceType> extendedTypes = declaration.getExtendedTypes();
            List<ClassOrInterfaceType> toRemove = new ArrayList<>();

            extendedTypes.forEach(type -> {
                if(_nameGenerator.getClassName(type.getNameAsString()).equals(type.getNameAsString())){
                    if(nodes.containsKey(declaration)){
                        nodes.get(declaration).add(type);
                    }else{
                        ArrayList<ClassOrInterfaceType> list = new ArrayList<>();
                        list.add(type);
                        nodes.put(declaration, list);
                    }

                    toRemove.add(type);
                }
            });

            toRemove.forEach(type -> type.remove());
        }
        node.getChildNodes().forEach(n -> removeUnknown(n, nodes));
    }

    @Override
    public void applyChanges(IObfuscatedFile file) {
        CompilationUnit modifiedCu = file.getCompilationUnit();
        HashMap<ClassOrInterfaceDeclaration, ArrayList<ClassOrInterfaceType>> iterfacesAndSubClasses = new HashMap<>();
        removeUnknown(modifiedCu, iterfacesAndSubClasses);

        List<Node> nodesToReplace = new ArrayList<>();
        recurseAllNodes(modifiedCu, file, nodesToReplace);
        nodesToReplace.forEach(nd -> replaceVariableUsages(nd));

        iterfacesAndSubClasses.keySet().forEach(key -> {
            if(key.getExtendedTypes() == null){
                key.setExtendedTypes(new NodeList<>());
            }
            iterfacesAndSubClasses.values().forEach(type -> {
                type.forEach(t -> key.addExtendedType(t));
            });
        });
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file, List<Node> nodesToReplace){
        populateNodesToReplace(n, nodesToReplace);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file, nodesToReplace));
    }

    private void populateNodesToReplace(Node n, List<Node> nodesToReplace){
        Class c = n.getClass();
        if(c.getSimpleName().equals("NameExpr")){
            NameExpr variableUsage = (NameExpr) (n);
            SymbolReference<? extends ValueDeclaration> z = _symbolSolver.solveSymbol(variableUsage.getNameAsString(), n);
            try{
                if(z.getCorrespondingDeclaration() != null){
                    nodesToReplace.add(n);
                }
            }catch (UnsupportedOperationException e){
            }
        }

        if(c.getSimpleName().equals("VariableDeclarator")){
            nodesToReplace.add(n);
        }

        if(c.getSimpleName().equals("Parameter")){
            nodesToReplace.add(n);
        }
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