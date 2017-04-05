package Core;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.util.List;

public interface ISourceReader{
    List<JavaSource> ParseSourceDirectory(String dir);
}
