package JavaObfuscator.FileReader;

import com.netflix.rewrite.ast.Tr;
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class ObfuscatedType implements IObfuscatedFile {
    private File _baseFile;

    private String _fileName;
    private Tr.CompilationUnit _compilationUnit;

    public ObfuscatedType(File baseFile){

        _baseFile = baseFile;
        _fileName = baseFile.getName();
    }

    public Path path() {
        return _baseFile.toPath();
    }


    @Override
    public void setCompilationUnit(Tr.CompilationUnit compilationUnit) {
        _compilationUnit = compilationUnit;
    }

    @Override
    public Tr.CompilationUnit getCompilationUnit() {
        return _compilationUnit;
    }

    public void applyChanges(){
        Path path = Paths.get(path().getParent() + "\\out\\");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(  PrintWriter out = new PrintWriter( path.toString() +  "\\" + _fileName)  ){
            out.println(  _compilationUnit.print() );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        try {
            return new String(Files.readAllBytes(Paths.get(_baseFile.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  "";
    }
}
