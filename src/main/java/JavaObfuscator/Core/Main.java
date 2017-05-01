/**
 * Created by Jack Barker on 4/04/2017.
 */
package JavaObfuscator.Core;

import JavaObfuscator.FileReader.*;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        IFileRetriever fileRetriever = new FileRetriever();
        ISourceReader reader = new SourceReader(fileRetriever);
        List<IObfuscatedFile> obfuscatedFiles = reader.ParseSourceDirectory("C:\\tmp", ".java");

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
                new GenericStatementReplacer(nameGenerator, new StatementGenerator()));



        //obfuscatedFiles = obfuscator.randomiseMethodNames(obfuscatedFiles);
        obfuscatedFiles = obfuscator.randomiseVariableNames(obfuscatedFiles);
        //obfuscatedFiles = obfuscator.randomiseClassNames(obfuscatedFiles);
        //obfuscatedFiles = obfuscator.flattenEntireProject(obfuscatedFiles);

        modifier.replace();

        for(IObfuscatedFile obfuscatedFile : obfuscatedFiles){
            obfuscatedFile.applyChanges();
        }
    }
}



