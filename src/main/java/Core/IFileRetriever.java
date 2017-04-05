package Core;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IFileRetriever {
    public List<IObfuscatedFile> getFiles(String path);
}
