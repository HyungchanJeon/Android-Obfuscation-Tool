package JavaObfuscator.FileReader;

import com.netflix.rewrite.ast.Tr;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface IObfuscatedFile {
    public Path path();

    List<String> getTypeNames();

    void setCompilationUnit(Tr.CompilationUnit compilationUnit);

    Tr.CompilationUnit getCompilationUnit();
}
