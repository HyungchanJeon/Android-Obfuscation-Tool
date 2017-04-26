package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;

import java.util.List;

/**
 * Created by Jack Barker on 26/04/2017.
 * RawObfuscator is used when the AST is no longer in use, used for editing the raw text
 */
public class RawObfuscator implements IRawObfuscator {
    NameGenerator _nameGenerator;

    public RawObfuscator(NameGenerator nameGenerator) {
        _nameGenerator = nameGenerator;
    }

    public List<IObfuscatedFile> changeTypeNames(List<IObfuscatedFile> obfuscatedFiles) {

        return obfuscatedFiles;
    }
}
