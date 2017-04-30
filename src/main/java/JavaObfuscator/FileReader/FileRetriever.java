package JavaObfuscator.FileReader;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileRetriever implements IFileRetriever {
    public List<File> getFiles(String path) {
        File dir = new File(path);
        ArrayList<File> files = new ArrayList<>();
        listf(path, files);

        return files;
    }

    public void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if(file.getPath().toLowerCase().endsWith(".java"))
                    files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }
}
