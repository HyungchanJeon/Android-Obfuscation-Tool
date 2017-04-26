package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.stream.Collectors;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileModifier implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        changeInterfaceAndClassTypes(file, oldTypeName, newTypeName);
        changeMethodReturnTypes(file, oldTypeName, newTypeName);
        changeImplementedInterfaceTypes(file, oldTypeName, newTypeName);
        changeExtendedClassTypes(file, oldTypeName, newTypeName);
    }

    private void changeExtendedClassTypes(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        file.getCompilationUnit().getNodesByType(ClassOrInterfaceDeclaration.class)
                .stream().forEach(node -> changeTypeOfExtendedClass(node, oldTypeName, newTypeName));
    }


    private void changeImplementedInterfaceTypes(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        file.getCompilationUnit().getNodesByType(ClassOrInterfaceDeclaration.class)
                .stream().forEach(node -> changeTypeOfInterface(node, oldTypeName, newTypeName));
    }

    private void changeMethodReturnTypes(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        NodeList<TypeDeclaration<?>> types = file.getCompilationUnit().getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    if(method.getType().toString().equals(oldTypeName))
                        method.setType(newTypeName);
                }
            }
        }
    }

    private void changeInterfaceAndClassTypes(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        file.getCompilationUnit().getNodesByType(ClassOrInterfaceDeclaration.class).stream()
                .filter(c -> c.getNameAsString().equals(oldTypeName)).forEach(c -> c.getName().setIdentifier(newTypeName));
    }

    private void changeTypeOfInterface(ClassOrInterfaceDeclaration node, String oldTypeName, String newTypeName) {
        for(ClassOrInterfaceType type : node.getImplementedTypes()){
            if(type.getName().toString().equals(oldTypeName)){
                type.getName().setIdentifier(newTypeName);
            }
        }
    }

    private void changeTypeOfExtendedClass(ClassOrInterfaceDeclaration node, String oldTypeName, String newTypeName) {
        for(ClassOrInterfaceType type : node.getExtendedTypes()){
            if(type.getName().toString().equals(oldTypeName)){
                type.getName().setIdentifier(newTypeName);
            }
        }
    }
}
