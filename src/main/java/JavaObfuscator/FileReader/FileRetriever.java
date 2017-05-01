package JavaObfuscator.FileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class FileRetriever implements IFileRetriever {
    public List<File> getFiles(String path, String extension) {
        File dir = new File(path);
        ArrayList<File> files = new ArrayList<>();
        listf(path, files, extension);

        return files;
    }

    public void listf(String directoryName, ArrayList<File> files, String extension) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if(file.getPath().toLowerCase().endsWith(extension))
                    files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files, extension);
            }
        }
    }
}
