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

    public Obfuscator(INameGenerator nameGenerator, IFileModifier renameTypes, IFileModifier renameVariables){
        _nameGenerator = nameGenerator;
        _renameTypes = renameTypes;
        _renameVariables = renameVariables;
    }

    @Override
    public List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameTypes.rename(obfuscatedFiles.get(i), _nameGenerator);
        }

        return obfuscatedFiles;
    }

    @Override
    public List<IObfuscatedFile> randomiseVariableNames(List<IObfuscatedFile> obfuscatedFiles) {

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            _renameVariables.rename(obfuscatedFiles.get(i), _nameGenerator);
        }

        return obfuscatedFiles;
    }
}
