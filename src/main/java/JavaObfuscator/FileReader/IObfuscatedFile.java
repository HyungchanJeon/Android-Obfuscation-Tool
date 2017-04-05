package JavaObfuscator.FileReader;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscatedFile {
    public String path();
    public String source();

    void setSource(String source);

    List<String> getTypeNames();
}
