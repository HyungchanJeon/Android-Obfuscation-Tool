package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This manages obfuscation of source
 */
public class Obfuscator implements IObfuscator {

    private IFileModifier _renameTypes;
    private IFileModifier _renameVariables;
    private IFileModifier _methodInliner;
    private IFileModifier _removeComments;
    private IFileModifier _genericStatementReplacer;
    private IFileModifier _stringSplitter;


    /**
     * Constructor
     * @param combinedTypeSolver
     * @param nameGenerator
     * @param symbolSolver
     * @param renameTypes
     * @param renameVariables
     * @param methodInliner
     * @param genericStatementReplacer
     * @param stringSplitter
     * @param removeComments
     */
    public Obfuscator(CombinedTypeSolver combinedTypeSolver, INameGenerator nameGenerator, SymbolSolver symbolSolver,
                      IFileModifier renameTypes, IFileModifier renameVariables, IFileModifier methodInliner,
                      IFileModifier genericStatementReplacer, IFileModifier stringSplitter, IFileModifier removeComments){
        _removeComments = removeComments;
        _renameTypes = renameTypes;
        _renameVariables = renameVariables;
        _methodInliner = methodInliner;
        _genericStatementReplacer = genericStatementReplacer;
        _stringSplitter = stringSplitter;
    }

    /**
     * Removes comments from each file
     * @param obfuscatedFiles
     * @return
     */
    @Override
    public List<IObfuscatedFile> removeComments(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _removeComments.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    /**
     * Randomises class names in files
     * @param obfuscatedFiles
     * @return
     */
    @Override
    public List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameTypes.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    /**
     * Randomises all variable names in files
     * @param obfuscatedFiles
     * @return
     */
    @Override
    public List<IObfuscatedFile> randomiseVariableNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameVariables.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    /**
     * Replaces all messages inline
     * @param obfuscatedFiles
     * @return
     */
    @Override
    public List<IObfuscatedFile> inlineMethods(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _methodInliner.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    /**
     * Splits all strings into characters and rebuilds
     * @param obfuscatedFiles
     * @return
     */
    @Override
    public List<IObfuscatedFile> splitStrings(List<IObfuscatedFile> obfuscatedFiles) {
        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _stringSplitter.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    /**
     * Control flattens files
     * @param obfuscatedFiles
     * @return
     */
    public List<IObfuscatedFile> flattenEntireProject(List<IObfuscatedFile> obfuscatedFiles) {
        final Integer s;

        for(int j = 0; j < 1; j++) {
            System.out.println(j);
            for (int i = 0; i < obfuscatedFiles.size(); i++) {
                _genericStatementReplacer.applyChanges(obfuscatedFiles.get(i));
            }
        }

        return obfuscatedFiles;
    }
}
