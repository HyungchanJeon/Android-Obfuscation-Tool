package JavaObfuscator.Core;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface INameGenerator {
    String getClassName(String oldTypeName);

    void setClassNames(List<String> classNames);

    String generateDistinct();
}
