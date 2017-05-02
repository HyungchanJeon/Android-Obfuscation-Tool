/**
 * Created by Jack Barker on 4/04/2017.
 */
package JavaObfuscator.Core;

import JavaObfuscator.FileReader.*;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void runMethod(int module) throws IOException{
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory(
                "C:\\Users\\Jack Barker\\Documents\\702A11\\app\\src\\main\\java\\com\\example\\a702app\\passworddiary"
                //"C:\\tmp"
                , ".java");

        NameGenerator nameGenerator = new NameGenerator();

        Stream<TypeDeclaration<?>> classes = obfuscatedFiles.stream().map(f -> f.getCompilationUnit()).map(c -> c.getTypes()).flatMap
                (Collection::stream);

        List<String> classNames = classes.map(c -> c.getName().toString()).collect(Collectors.toList());
        nameGenerator.setClassNames(classNames);


        ClassOrInterfaceModifier modifier = new ClassOrInterfaceModifier(nameGenerator);
        obfuscatedFiles.forEach(f -> modifier.remove(f.getCompilationUnit()));


        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        combinedTypeSolver.add(new MemoryTypeSolver());
        combinedTypeSolver.add(new ReflectionTypeSolver());

        ReflectionTypeSolver rfs = new ReflectionTypeSolver();

        obfuscatedFiles.forEach(file ->  combinedTypeSolver.add(new JavaParserTypeSolver(file.getBaseFile().getParentFile())));

        SymbolSolver symbolSolver = new SymbolSolver(combinedTypeSolver);

        Obfuscator obfuscator = new Obfuscator(
                combinedTypeSolver,
                nameGenerator,
                symbolSolver,
                new RenameTypes(nameGenerator),
                new RenameMethods(nameGenerator, symbolSolver),
                new RenameVariables(nameGenerator, combinedTypeSolver, symbolSolver),
                new MethodInliner(nameGenerator),
                new GenericStatementReplacer(nameGenerator, new StatementGenerator()),
                new StringSplitter(nameGenerator));


        if(module == 1){
            obfuscatedFiles = obfuscator.randomiseMethodNames(obfuscatedFiles);
        }
        if(module == 2){
            obfuscatedFiles = obfuscator.randomiseVariableNames(obfuscatedFiles);
        }
        if(module == 3){
            obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);
        }
        if(module == 4){
            obfuscatedFiles = obfuscator.splitStrings(obfuscatedFiles);
        }
        if(module == 5){
            obfuscator.inlineMethods(obfuscatedFiles);
        }
        if(module == 6){
            obfuscatedFiles = obfuscator.flattenEntireProject(obfuscatedFiles);
        }

        modifier.replace();

        //Modify xml files
        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }
    }
    public static void main(String[] args) throws IOException {
        runMethod(2);
        runMethod(3);
        runMethod(4);
        runMethod(5);
        runMethod(6);
    }
}

