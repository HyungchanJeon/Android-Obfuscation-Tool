/**
 * Created by Jack Barker on 4/04/2017.
 */
package Core;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public class Obfuscator {
    public static void main(String[] args){
        ISourceReader reader = new SourceReader();
        JavaClassSource javaClass =
                Roaster.parse(JavaClassSource.class, "public class SomeClass {}");
        javaClass.addMethod()
                .setPublic()
                .setStatic(true)
                .setName("main")
                .setReturnTypeVoid()
                .setBody("System.out.println(\"Hello World\");")
                .addParameter("java.lang.String[]", "args");
        System.out.println(javaClass);
    }
}



