package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IFileModifier {
    void replaceUsages(IObfuscatedFile src2, List<String> oldTypeNames, List<String> newTypeNames);
}
