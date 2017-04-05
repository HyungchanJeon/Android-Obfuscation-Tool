package Core;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class ObfuscatedFile implements IObfuscatedFile {
    private File _baseFile;

    public ObfuscatedFile(File baseFile){
        _baseFile = baseFile;
    }

    public String path() {
        return _baseFile.getPath();
    }

    public String source() {
        throw new NotImplementedException();
    }
}
