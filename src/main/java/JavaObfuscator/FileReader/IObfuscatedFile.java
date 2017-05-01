package JavaObfuscator.FileReader;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscatedFile {
    public Path path();

    void setCompilationUnit(CompilationUnit compilationUnit);

    CompilationUnit getCompilationUnit();

    void applyChanges();

    String getFileName();

    void setFileName(String s);

    File getBaseFile();
}
