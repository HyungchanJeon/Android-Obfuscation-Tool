package JavaObfuscator.Core;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Random;

/**
 * Created by Lenovo on 5/2/2017.
 */
public class OpaquePredicateGenerator {

    private DummyCodeGenerator _dummyCodeGenerator;
    private INameGenerator _nameGenerator;

    OpaquePredicateGenerator(INameGenerator nameGenerator, DummyCodeGenerator dummyCodeGenerator){
        _dummyCodeGenerator = dummyCodeGenerator;
        _nameGenerator = nameGenerator;
    }

    public Statement generateOpaqueStatement(BlockStmt blockToBeExecuted){
        IfStmt ifStmt = new IfStmt();

        //Generate random boolean value to determine which block in the if statement the true code will go
        Random random = new Random();
        Boolean result = random.nextBoolean();

        //Generate random predicate that results the boolean we genereated
        ifStmt.setCondition(generatePredicate(result));

        //Put dummy code in the if blocks that will never be executed
        if (result){
            ifStmt.setThenStmt(_dummyCodeGenerator.generateDummyCodeBlock());
            ifStmt.setElseStmt(blockToBeExecuted);
        } else {
            ifStmt.setThenStmt(blockToBeExecuted);
            ifStmt.setElseStmt(_dummyCodeGenerator.generateDummyCodeBlock());
        }

        return ifStmt;
    }

    public BinaryExpr generatePredicate(boolean result) {
        String RandomString = _nameGenerator.generateDistinct();
        if (result) {
            return new BinaryExpr(new UnaryExpr(new StringLiteralExpr(RandomString), UnaryExpr.Operator.MINUS), new UnaryExpr(new StringLiteralExpr(RandomString), UnaryExpr.Operator.MINUS), BinaryExpr.Operator.EQUALS);
        } else {
            return new BinaryExpr(new UnaryExpr(new StringLiteralExpr(RandomString), UnaryExpr.Operator.MINUS), new UnaryExpr(new StringLiteralExpr(RandomString), UnaryExpr.Operator.MINUS), BinaryExpr.Operator.NOT_EQUALS);
        }
    }
}
