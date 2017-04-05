package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class Obfuscator implements IObfuscator {

    private INameGenerator _nameGenerator;
    private IFileModifier _fileModifier;

    public Obfuscator(INameGenerator nameGenerator, IFileModifier fileModifier){

        _nameGenerator = nameGenerator;
        _fileModifier = fileModifier;
    }
    @Override
    public List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> obfuscatedFiles) {

        //Get all type names
        List<String> oldTypeNames = obfuscatedFiles.stream().map(i -> i.getTypeNames()).flatMap(List::stream).collect(Collectors.toList());

        List<String> newTypeNames = _nameGenerator.getNames(oldTypeNames);

        //Replace types over all files
        for(IObfuscatedFile src : obfuscatedFiles){
            _fileModifier.replaceUsages(src, oldTypeNames, newTypeNames);
        }

        return obfuscatedFiles;
    }
}
