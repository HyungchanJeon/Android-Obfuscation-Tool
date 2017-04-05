package Core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileRetriever implements IFileRetriever {
    public List<IObfuscatedFile> getFiles(String path) {
        File dir = new File(path);

        List<File> fileList = Arrays.asList(dir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String fileName) {
                if(fileName.endsWith(".java")){
                    return true;
                }
                return false;
            }
        }));

        return fileList.stream().map(i -> new ObfuscatedFile(i)).collect(Collectors.toList());
    }
}
