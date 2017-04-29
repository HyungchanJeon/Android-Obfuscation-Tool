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
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory("C:\\tmp");

        NameGenerator nameGenerator = new NameGenerator();

        Obfuscator obfuscator = new Obfuscator(
                nameGenerator,
                new RenameTypes(nameGenerator),
                new RenameMethods(nameGenerator),
                new RenameVariables(nameGenerator),
                new GenericStatementReplacer(nameGenerator, new StatementGenerator()));

        obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);
        obfuscatedFiles = obfuscator.randomiseMethodNames(obfuscatedFiles);
        obfuscatedFiles = obfuscator.randomiseVariableNames(obfuscatedFiles);
        //obfuscatedFiles = obfuscator.replaceWhilesWithSwitches(obfuscatedFiles);

        obfuscatedFiles = obfuscator.flattenEntireProject(obfuscatedFiles);

        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }
    }
}



