/**
 * Created by Jack Barker on 4/04/2017.
 */
package JavaObfuscator.Core;

import JavaObfuscator.FileReader.*;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory("C:\\workspace\\702A1-master\\app\\src\\main\\java\\com\\example\\a702app\\passworddiary");

        NameGenerator nameGenerator = new NameGenerator();
        Obfuscator obfuscator = new Obfuscator(nameGenerator, new RenameTypes(), new RenameVariables(), new RenameMethods());
        obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);
        obfuscatedFiles = obfuscator.randomiseMethodNames(obfuscatedFiles);
        obfuscatedFiles = obfuscator.randomiseVariableNames(obfuscatedFiles);

        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }
    }
}



