package JavaObfuscator.FileReader;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.io.File;
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

    public List<IObfuscatedFile> ParseSourceDirectory(String path) {
        List<File> javaFiles = _fileRetriever.getFiles(path);

        List<IObfuscatedFile> obfuscatedFiles =  javaFiles.stream().map(i -> {
            IObfuscatedFile file = new ObfuscatedType(i);
            file.setJavaSource(parseSource(file));
            return file;
        }).collect(Collectors.toList());

        return obfuscatedFiles.stream().collect(Collectors.toList());
    }

    private JavaSource parseSource(IObfuscatedFile file) {
        return Roaster.parse(JavaSource.class, file.stringSource());
    }
}
