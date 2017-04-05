/**
 * Created by Jack Barker on 4/04/2017.
 */
package JavaObfuscator.Core;

import JavaObfuscator.FileReader.*;

import java.util.List;

public class Main {
    public static void main(String[] args){
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory("C:\\tmp");

        IObfuscator obfuscator = new Obfuscator(new NameGenerator(), new FileModifier());

        obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);

        for(IObfuscatedFile src : obfuscatedFiles){
            System.out.println(src);
        }
    }
}



