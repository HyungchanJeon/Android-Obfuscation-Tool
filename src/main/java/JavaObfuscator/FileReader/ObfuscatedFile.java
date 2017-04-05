package JavaObfuscator.FileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class ObfuscatedFile implements IObfuscatedFile {
    private File _baseFile;
    private String _source;
    public ObfuscatedFile(File baseFile){
        _baseFile = baseFile;
        try {
            _source = Files.lines(_baseFile.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String path() {
        return _baseFile.getPath();
    }

    public String source() {
        return _source;
    }

    @Override
    public void setSource(String source) {
        _source = source;
    }

    @Override
    public String toString(){
        return _source;
    }
}
