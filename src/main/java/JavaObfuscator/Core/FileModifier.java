package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.netflix.rewrite.ast.NameTree;
import com.netflix.rewrite.ast.Tr;
import com.netflix.rewrite.ast.Tr.Ident;
import com.netflix.rewrite.ast.Type;
import com.netflix.rewrite.ast.visitor.AstVisitor;
import com.sun.tools.javac.code.TypeTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileModifier implements IFileModifier {
    @Override
    public void replaceUsages(IObfuscatedFile file, String oldTypeName, String newTypeName) {
        file.setCompilationUnit(file.getCompilationUnit().refactor().changeType(oldTypeName, newTypeName).fix());

        List<String> things = new ArrayList<>();

        new AllStrings().visit(file.getCompilationUnit());

    }

    class AllStrings extends AstVisitor<List<String>> {
        public AllStrings() { super(Collections.emptyList()); }

        @Override
        public List<String> visitLiteral(Tr.Literal literal) {
            Object value = literal.getValue();
            return new ArrayList<String>();
        }
    }

    private void testVisit(){

    }

    public void changeTypeNamesRaw(IObfuscatedFile file, String oldTypeName, String newTypeName){

    }
}
