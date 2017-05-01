package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscator {
    List<IObfuscatedFile> randomiseClassNames(List<IObfuscatedFile> javaSources);
    List<IObfuscatedFile> randomiseVariableNames(List<IObfuscatedFile> javaSources);
    List<IObfuscatedFile> randomiseMethodNames(List<IObfuscatedFile> javaSources);
    List<IObfuscatedFile> inlineMethods(List<IObfuscatedFile> obfuscatedFiles);

    List<IObfuscatedFile> splitStrings(List<IObfuscatedFile> obfuscatedFiles);
}
