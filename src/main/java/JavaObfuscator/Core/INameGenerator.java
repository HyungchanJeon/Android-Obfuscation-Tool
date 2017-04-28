package JavaObfuscator.Core;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface INameGenerator {
    String getClassName(String oldTypeName);
    String getVariableName(String oldVariableName);
    String getMethodName(String oldVariableName);
    void setMethodName(String oldVariableName);


    void setClassNames(List<String> classNames);
}
