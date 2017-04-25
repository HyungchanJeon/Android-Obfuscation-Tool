package JavaObfuscator.Core;

import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public interface INameGenerator {
    List<String> getNames(List<String> oldNames);

    List<String> getClassNames();

    void setClassNames(List<String> originalClassNames);
}
