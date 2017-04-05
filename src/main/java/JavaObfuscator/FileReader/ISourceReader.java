package JavaObfuscator.FileReader;

import java.util.List;

public interface ISourceReader{
    List<IObfuscatedFile> ParseSourceDirectory(String dir);
}
