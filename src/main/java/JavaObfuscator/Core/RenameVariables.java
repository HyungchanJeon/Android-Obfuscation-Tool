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
 *
 *
 * Created by Jack Barker on 5/04/2017.
 */
public class RenameVariables implements IFileModifier {

    private CombinedTypeSolver _combinedTypeSolver;
    private INameGenerator _nameGenerator;
    private SymbolSolver _symbolSolver;

    /**
     * Sets symbol solver instance and name generator instance for renaming variables
     *
     * @param nameGenerator Randomly generates unique names
     * @param combinedTypeSolver Combines type solvers from symbol solver
     * @param symbolSolver Solves symbols from nodes
     */
    public RenameVariables(INameGenerator nameGenerator, CombinedTypeSolver combinedTypeSolver, SymbolSolver symbolSolver){
        _symbolSolver = symbolSolver;
        _combinedTypeSolver = combinedTypeSolver;
        _nameGenerator = nameGenerator;
    }

    /**
     * Find all varibles that need renaming, then recursively search the java AST and rename the variables
     *
     * @param file File containing java source code to rename variables in
     */
    @Override
    public void applyChanges(IObfuscatedFile file) {
        ClassOrInterfaceModifier modifier = new ClassOrInterfaceModifier(_nameGenerator);
        modifier.remove(file.getCompilationUnit());
        List<Node> nodesToReplace = new ArrayList<>();
        recurseAllNodes(file.getCompilationUnit(), file, nodesToReplace);
        nodesToReplace.forEach(nd -> replaceVariableUsages(nd));
        modifier.replace();
    }

    /**
     * Recursively rename all variables in the AST
     *
     * @param n Node to traverse recursively
     * @param file File containing java source code to rename variables in
     * @param nodesToReplace List of nodes that contain variables that need renaming
     */
    private void recurseAllNodes(Node n, IObfuscatedFile file, List<Node> nodesToReplace){
        populateNodesToReplace(n, nodesToReplace);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file, nodesToReplace));
    }

    /**
     * Finds the variables that need to be renamed.
     *
     * @param n Node to traverse recursively
     * @param nodesToReplace List of nodes that contain variables to rename
     */
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

        if(c.getSimpleName().equals("FieldAccessExpr")){
            FieldAccessExpr variableUsage = (FieldAccessExpr) (n);

            SymbolReference<? extends ValueDeclaration> z = _symbolSolver.solveSymbol(variableUsage.getChildNodes().get(1).toString(),
                    n);
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

    /**
     * Recursively rename all variable names used in expressions to new random names sourced from the name generator
     *
     * @param n Node to traverse recursively
     */
    private void replaceVariableUsages(Node n){

        Class c = n.getClass();
        if(c.getSimpleName().equals("NameExpr")){

            NameExpr variableUsage = (NameExpr) (n);

            String newVariableName = _nameGenerator.getVariableName(variableUsage.getNameAsString());
            variableUsage.setName(newVariableName);
        }

        if(c.getSimpleName().equals("FieldAccessExpr")){
            FieldAccessExpr variableUsage = (FieldAccessExpr) (n);

            SimpleName name = (SimpleName)variableUsage.getChildNodes().get(1);
            name.setIdentifier(_nameGenerator.getVariableName(name.getIdentifier()));
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