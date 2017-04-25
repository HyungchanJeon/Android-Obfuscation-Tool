package JavaObfuscator.FileReader;

import com.netflix.rewrite.ast.Tr;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class ObfuscatedType implements IObfuscatedFile {
    private File _baseFile;

    private Tr.CompilationUnit _compilationUnit;

    public ObfuscatedType(File baseFile){
        _baseFile = baseFile;

    }

    public Path path() {
        return _baseFile.toPath();
    }


    @Override
    public List<String> getTypeNames() {
        throw new NotImplementedException();
        //return returnList;
    }

    @Override
    public void setCompilationUnit(Tr.CompilationUnit compilationUnit) {
        _compilationUnit = compilationUnit;
    }

    @Override
    public Tr.CompilationUnit getCompilationUnit() {
        return _compilationUnit;
    }
}
