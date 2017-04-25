package JavaObfuscator.FileReader;

import org.jboss.forge.roaster.model.source.JavaSource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class ObfuscatedType implements IObfuscatedFile {
    private File _baseFile;

    public ObfuscatedType(File baseFile){
        _baseFile = baseFile;

    }

    public String path() {
        return _baseFile.getPath();
    }


    @Override
    public List<String> getTypeNames() {
        List<String> returnList = new ArrayList<String>();
        returnList.add(_javaSource.getName());
        return returnList;
    }
    
}
