package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileModifier implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        file.setCompilationUnit(file.getCompilationUnit().refactor().changeType(oldTypeName, newTypeName).fix());
    }

    public void changeTypeNamesRaw(IObfuscatedFile file, String oldTypeName, String newTypeName){

    }
}
