package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscator {
    List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> javaSources);
}
