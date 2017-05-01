package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class Obfuscator implements IObfuscator {

    private INameGenerator _nameGenerator;
    private IFileModifier _renameTypes;
    private IFileModifier _renameVariables;
    private IFileModifier _renameMethods;
    private  IFileModifier _methodInliner;
    private IFileModifier _genericStatementReplacer;

    public Obfuscator(INameGenerator nameGenerator, IFileModifier renameTypes, IFileModifier renameMethods, IFileModifier renameVariables,IFileModifier methodInliner, IFileModifier genericStatementReplacer){
        _nameGenerator = nameGenerator;
        _renameTypes = renameTypes;
        _renameMethods = renameMethods;
        _renameVariables = renameVariables;
        _methodInliner = methodInliner;
        _genericStatementReplacer = genericStatementReplacer;
    }

    @Override
    public List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> obfuscatedFiles) {
        Stream<TypeDeclaration<?>> classes = obfuscatedFiles.stream().map(f -> f.getCompilationUnit()).map(c -> c.getTypes()).flatMap
                (Collection::stream);

        List<String> classNames = classes.map(c -> c.getName().toString()).collect(Collectors.toList());
        _nameGenerator.setClassNames(classNames);

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameTypes.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    @Override
    public List<IObfuscatedFile> randomiseVariableNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameVariables.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }

    @Override
    public List<IObfuscatedFile> randomiseMethodNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameMethods.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }


    @Override
    public List<IObfuscatedFile> inlineMethods(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _methodInliner.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }


    public List<IObfuscatedFile> flattenEntireProject(List<IObfuscatedFile> obfuscatedFiles) {
        final Integer s;

        for(int j = 0; j < 2; j++) {
            System.out.println(j);
            for (int i = 0; i < obfuscatedFiles.size(); i++) {
                _genericStatementReplacer.applyChanges(obfuscatedFiles.get(i));
            }
        }

        return obfuscatedFiles;
    }
}
