package JavaObfuscator.FileReader;

import java.io.IOException;
import java.util.List;

public interface ISourceReader{
    List<IObfuscatedFile> ParseSourceDirectory(String dir) throws IOException;
}
