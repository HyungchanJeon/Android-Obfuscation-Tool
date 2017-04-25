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
        NameGenerator generator = new NameGenerator();
        List<String> newClassNames = generator.getNames(classNames);

        ArrayList<Tr.CompilationUnit> refactored = new ArrayList<>();

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            for(int j = 0; j < classNames.size(); j++){
                obfuscatedFiles.get(i).setCompilationUnit(obfuscatedFiles.get(i).getCompilationUnit().refactor().changeType(classNames.get(j),
                        newClassNames.get(j))
                        .fix());
            }
        }

        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            System.out.println(obfuscatedFile.getCompilationUnit().print());
        }

        return obfuscatedFiles;
    }
}
