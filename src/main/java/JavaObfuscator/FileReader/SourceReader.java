package JavaObfuscator.FileReader;

import com.netflix.rewrite.ast.Tr;
import com.netflix.rewrite.parse.OracleJdkParser;
import com.netflix.rewrite.parse.Parser;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jack Barker on 4/04/2017.
 */
public class SourceReader implements ISourceReader {

    private IFileRetriever _fileRetriever;

    public SourceReader(IFileRetriever fileRetriever){
        _fileRetriever = fileRetriever;
    }

    public List<IObfuscatedFile> ParseSourceDirectory(String path) {

        List<File> javaFiles = _fileRetriever.getFiles(path);

        List<IObfuscatedFile> obfuscatedFiles =  javaFiles.stream().map(i -> {
            IObfuscatedFile file = new ObfuscatedType(i);
            return file;
        }).collect(Collectors.toList());

        Parser parser = new OracleJdkParser();
        List<Path> paths = obfuscatedFiles.stream().map(file -> file.path()).collect(Collectors.toList());
        List<Tr.CompilationUnit>  compilationUnits = parser.parse(paths);

        for(int i = 0; i < obfuscatedFiles.size(); i++){
            obfuscatedFiles.get(i).setCompilationUnit(compilationUnits.get(i));
        }

        return obfuscatedFiles.stream().collect(Collectors.toList());
    }
}
