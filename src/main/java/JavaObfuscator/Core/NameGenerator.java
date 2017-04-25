package JavaObfuscator.Core;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class NameGenerator implements INameGenerator {

    private List<String> _originalClassNames;
    private List<String> _newClassNames;


    public void setClassNames(List<String> originalClassNames){
        _originalClassNames = originalClassNames;
        _newClassNames = getNames(originalClassNames);
    }



    /**(
     * @param oldNames is a list of names that need to be replaced
     * @return a list of strings to replace the old names with
     */
    @Override
    public List<String> getNames(List<String> oldNames) {
        //Need to ensure none of the old names are used in the new names
        //Names contain only letters
        List<String> newNames = new ArrayList<String>();
        Random random = new Random();
        for(String name : oldNames){
            boolean nameFound = false;
            String newName = "";

            while(!nameFound) {
                int length = 4 + random.nextInt(10);
                System.out.println(length);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < length; i++) {
                    int letterIndex = random.nextInt(26);
                    char letter = (char) ('A' + letterIndex);
                    sb.append(letter);
                }
                newName = sb.toString();
                if(!oldNames.contains(newName) && !newNames.contains(newName)){
                    nameFound = true;
                }
            }

            newNames.add(newName);
        }

        return newNames;
    }

    @Override
    public List<String> getClassNames() {
        return _newClassNames;
    }
}
