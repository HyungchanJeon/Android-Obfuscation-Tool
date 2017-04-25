package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;

/**
 * Created by Jack Barker on 26/04/2017.
 */
public interface IRawObfuscator {
    List<IObfuscatedFile> changeTypeNames(List<IObfuscatedFile> obfuscatedFiles);
}
