package Core;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Barker on 4/04/2017.
 */
public class SourceReader implements ISourceReader {

    private IFileRetriever _fileRetriever;

    public SourceReader(IFileRetriever fileRetriever){
        _fileRetriever = fileRetriever;
    }
    public List<JavaSource> ParseSourceDirectory(String path) {

        IObfuscatedFile[] javaFiles = _fileRetriever.getFiles(path);

        List<JavaSource> returnList = new ArrayList<JavaSource>();
        for(IObfuscatedFile file : javaFiles){
            returnList.add(parseSource(file));
        }

        return returnList;
    }

    private JavaSource parseSource(IObfuscatedFile file) {
        return Roaster.parse(JavaSource.class, file.source());
    }
}
