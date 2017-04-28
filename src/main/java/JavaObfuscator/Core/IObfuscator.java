package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscator {
    List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> javaSources);
    List<IObfuscatedFile> randomiseVariableNames(List<IObfuscatedFile> javaSources);
    List<IObfuscatedFile> replaceWhilesWithSwitches(List<IObfuscatedFile> javaSources);
}
