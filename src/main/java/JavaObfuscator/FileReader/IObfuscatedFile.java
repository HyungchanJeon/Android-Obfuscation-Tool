package JavaObfuscator.FileReader;

import org.jboss.forge.roaster.model.source.JavaSource;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscatedFile {
    public String path();

    List<String> getTypeNames();
}
