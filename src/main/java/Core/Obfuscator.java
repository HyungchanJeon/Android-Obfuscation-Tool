/**
 * Created by Jack Barker on 4/04/2017.
 */
package Core;
public class Obfuscator {
    public static void main(String[] args){
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        reader.ParseSourceDirectory("C:\\tmp");
    }
}



