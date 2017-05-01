package JavaObfuscator.Core;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jack Barker on 1/05/2017.
 */
public class ClassOrInterfaceModifier {

    private INameGenerator _nameGenerator;
    HashMap<ClassOrInterfaceDeclaration, ArrayList<ClassOrInterfaceType>> iterfacesAndSubClasses = new HashMap<>();

    public ClassOrInterfaceModifier(INameGenerator nameGenerator){
        _nameGenerator = nameGenerator;
    }

    public void remove(Node node) {
        if(node.getClass().getSimpleName().equals("ClassOrInterfaceDeclaration")){
            ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration)node;
            List<ClassOrInterfaceType> extendedTypes = declaration.getExtendedTypes();
            List<ClassOrInterfaceType> toRemove = new ArrayList<>();

            extendedTypes.forEach(type -> {
                if(_nameGenerator.getClassName(type.getNameAsString()).equals(type.getNameAsString())){
                    if(iterfacesAndSubClasses.containsKey(declaration)){
                        iterfacesAndSubClasses.get(declaration).add(type);
                    }else{
                        ArrayList<ClassOrInterfaceType> list = new ArrayList<>();
                        list.add(type);
                        iterfacesAndSubClasses.put(declaration, list);
                    }

                    toRemove.add(type);
                }
            });

            toRemove.forEach(type -> type.remove());
        }
        node.getChildNodes().forEach(n -> remove(n));
    }

    public void replace(){

        iterfacesAndSubClasses.entrySet().forEach(key -> {
            if(key.getKey().getExtendedTypes() == null){
                key.getKey().setExtendedTypes(new NodeList<>());
            }
            key.getValue().forEach(type -> {
                key.getKey().addExtendedType(type);
            });
        });
    }


}
