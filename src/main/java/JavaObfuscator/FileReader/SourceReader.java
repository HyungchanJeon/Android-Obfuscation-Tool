package JavaObfuscator.FileReader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 4/04/2017.
 */
public class SourceReader implements ISourceReader {

    private IFileRetriever _fileRetriever;

    public SourceReader(IFileRetriever fileRetriever){
        _fileRetriever = fileRetriever;
    }

    public List<IObfuscatedFile> ParseSourceDirectory(String path, String extension) throws IOException {

        List<File> javaFiles = _fileRetriever.getFiles(path, extension);

        List<IObfuscatedFile> obfuscatedFiles =  javaFiles.stream().map(i -> {
            IObfuscatedFile file = new ObfuscatedType(i);
            return file;
        }).collect(Collectors.toList());


        List<Path> paths = obfuscatedFiles.stream().map(file -> file.path()).collect(Collectors.toList());

        List<CompilationUnit> compilationUnits = paths.stream().map(tmpPath -> {
            try {
                return JavaParser.parse(tmpPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            obfuscatedFiles.get(i).setCompilationUnit(compilationUnits.get(i));
        }

        return obfuscatedFiles.stream().collect(Collectors.toList());
    }
}
