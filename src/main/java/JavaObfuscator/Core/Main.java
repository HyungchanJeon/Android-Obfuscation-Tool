/**
 * Created by Jack Barker on 4/04/2017.
 */
package JavaObfuscator.Core;

import JavaObfuscator.FileReader.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory("C:\\tmp");

        NameGenerator nameGenerator = new NameGenerator();
        Obfuscator obfuscator = new Obfuscator(nameGenerator, new FileModifier());
        obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);

        //Raw obfuscator is used when the AST is no longer in use, used for editing the raw text
        RawObfuscator rawObfuscator = new RawObfuscator(nameGenerator);
        obfuscatedFiles = rawObfuscator.changeTypeNames(obfuscatedFiles);

        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }
    }
}



