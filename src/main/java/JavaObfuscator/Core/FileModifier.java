package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileModifier implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile src2, List<String> oldTypeNames, List<String> newTypeNames) {
        throw new NotImplementedException();
    }
}
