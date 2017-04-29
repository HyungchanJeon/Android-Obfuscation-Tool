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
    private IFileModifier _whileReplacer;

    public Obfuscator(INameGenerator nameGenerator, IFileModifier renameTypes, IFileModifier renameVariables, IFileModifier whileReplacer){
        _nameGenerator = nameGenerator;
        _renameTypes = renameTypes;
        _renameVariables = renameVariables;
        _whileReplacer = whileReplacer;
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
    public List<IObfuscatedFile> replaceWhilesWithSwitches(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _whileReplacer.applyChanges(obfuscatedFiles.get(i));
        }

        return obfuscatedFiles;
    }
}
