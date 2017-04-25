package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.netflix.rewrite.ast.Tr;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Stream<Tr.ClassDecl> classes = obfuscatedFiles.stream().map(f -> f.getCompilationUnit()).map(c -> c
                .getClasses()).flatMap(Collection::stream);

        List<String> classNames = classes.map(c -> c.getSimpleName()).collect(Collectors.toList());
        _nameGenerator.setClassNames(classNames);

        List<String> newClassNames = _nameGenerator.getClassNames();

        ArrayList<Tr.CompilationUnit> refactored = new ArrayList<>();

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            for(int j = 0; j < classNames.size(); j++){
                _fileModifier.replaceUsages(obfuscatedFiles.get(i), classNames.get(j), newClassNames.get(j));
            }
        }

        return obfuscatedFiles;
    }
}
