package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.netflix.rewrite.parse.OracleJdkParser;
import com.netflix.rewrite.parse.Parser;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileModifier implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile file, List<String> oldTypeNames, List<String> newTypeNames) {


    }
}
