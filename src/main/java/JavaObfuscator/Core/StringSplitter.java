package JavaObfuscator.Core;

import JavaObfuscator.FileReader.IObfuscatedFile;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.model.declarations.ValueDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.ExpressionStatement;
import jdk.nashorn.internal.ir.Statement;
import sun.tools.tree.BinaryExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Jack Barker on 2/05/2017.
 */
public class StringSplitter implements IFileModifier {

    INameGenerator _nameGenerator;

    public StringSplitter(INameGenerator nameGenerator){
        _nameGenerator = nameGenerator;
    }
    @Override
    public void applyChanges(IObfuscatedFile file) {
        List<Node> stringNodes = new ArrayList<>();
        recurseAllNodes(file.getCompilationUnit(), file, stringNodes);

        replaceStrings(stringNodes);
    }

    private void replaceStrings(List<Node> stringNodes) {
        stringNodes.forEach(node -> {
            Class c = node.getClass();
            if(c.getSimpleName().equals("StringLiteralExpr")){



                StringLiteralExpr stringLiteral = (StringLiteralExpr) (node);
                BlockStmt outerBlock = null;
                Node notQuiteParent = node;
                try {
                    Node parent = node.getParentNode().get();


                    while (outerBlock == null) {

                        if (parent == null) {
                            break;
                        }
                        if (parent.getClass().getSimpleName().equals("BlockStmt")) {
                            outerBlock = (BlockStmt) parent;
                        } else {
                            notQuiteParent = parent;
                            parent = parent.getParentNode().get();
                        }
                    }

                    List<ExpressionStmt> letters = new ArrayList<>();
                    List<ExpressionStmt> builders = new ArrayList<>();
                    String string = stringLiteral.getValue();
                    String finalStringName = _nameGenerator.generateDistinct();
                    ExpressionStmt baseString = new ExpressionStmt(new VariableDeclarationExpr(new ClassOrInterfaceType("String"),
                            finalStringName));
                    letters.add(baseString);

                    ExpressionStmt baseStringAssign = new ExpressionStmt(new AssignExpr(new NameExpr(finalStringName), new StringLiteralExpr
                            (""), AssignExpr
                            .Operator.ASSIGN));

                    letters.add(baseStringAssign);

                    for (Character ch : string.toCharArray()) {
                        String variableName = _nameGenerator.generateDistinct();
                        ExpressionStmt tmp = new ExpressionStmt(new VariableDeclarationExpr(new ClassOrInterfaceType("String"),
                                variableName));
                        letters.add(tmp);

                        ExpressionStmt assign = new ExpressionStmt(new AssignExpr(new NameExpr(variableName), new StringLiteralExpr
                                (ch + ""), AssignExpr
                                .Operator.ASSIGN));

                        letters.add(assign);

                        ExpressionStmt builderTmp = new ExpressionStmt(new AssignExpr(new NameExpr(finalStringName), new NameExpr
                                (variableName), AssignExpr.Operator.PLUS));
                        builders.add(builderTmp);
                    }

                    NodeList<com.github.javaparser.ast.stmt.Statement> statements = outerBlock.getStatements();

                    int insertLocation = statements.indexOf(notQuiteParent);

                    NodeList<com.github.javaparser.ast.stmt.Statement> newStatements = new NodeList<com.github.javaparser.ast.stmt.Statement>();

                    newStatements.addAll(statements.subList(0, insertLocation));
                    letters.forEach(l -> newStatements.add(l));
                    builders.forEach(b -> newStatements.add(b));

                    if(node.getParentNode().get().getClass().getSimpleName().equals("VariableDeclarator")){
                        VariableDeclarator assignment = (VariableDeclarator) node.getParentNode().get();
                        assignment.setInitializer(new NameExpr(finalStringName));
                    }else if(node.getParentNode().get().getClass().getSimpleName().equals("BinaryExpr")){
                        BinaryExpr usage = (BinaryExpr) node.getParentNode().get();
                        if(usage.getLeft().equals(node)){
                            usage.setLeft(new NameExpr(finalStringName));
                        }

                        if(usage.getRight().equals(node)){
                            usage.setRight(new NameExpr(finalStringName));
                        }

                    }
                    else if(node.getParentNode().get().getClass().getSimpleName().equals("MethodCallExpr")){
                        MethodCallExpr usage = (MethodCallExpr) node.getParentNode().get();
                        int i = usage.getArguments().indexOf(node);
                        if(i >= 0)
                            usage.setArgument(i, new NameExpr(finalStringName));

                    }
                    else if(node.getParentNode().get().getClass().getSimpleName().equals("SwitchEntryStmt")){
                        SwitchEntryStmt usage = (SwitchEntryStmt) node.getParentNode().get();
                        usage.setLabel(new NameExpr(finalStringName));

                    }else{
                        throw new NoSuchElementException();
                    }

                    newStatements.addAll(statements.subList(insertLocation, statements.size()));
                    outerBlock.setStatements(newStatements);
                }catch(NoSuchElementException ex){}


            }
        });
    }

    private void recurseAllNodes(Node n, IObfuscatedFile file, List<Node> stringNodes){
        populateNodesToReplace(n, stringNodes);
        n.getChildNodes().stream().forEach(node -> recurseAllNodes(node, file, stringNodes));
    }

    private void populateNodesToReplace(Node n, List<Node> stringNodes){
        Class c = n.getClass();
        if(c.getSimpleName().equals("StringLiteralExpr")){
            StringLiteralExpr stringLiteral = (StringLiteralExpr) (n);
            stringNodes.add(stringLiteral);
        }

    }
}
