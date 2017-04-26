package JavaObfuscator.Core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Jack Barker on 5/04/2017.
 */
public class NameGenerator implements INameGenerator {

    private HashMap<String, String> _classNames = new HashMap<>();


    public String getClassName(String oldName){
        if(_classNames.containsKey(oldName)){
            return _classNames.get(oldName);
        }

        String newName = generateName();
        _classNames.put(oldName, newName);

        return newName;
    }

    private String generateName(){
        Random random = new Random();
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
            if(!_classNames.containsKey(newName) && !_classNames.containsValue(newName)){
                nameFound = true;
            }
        }

        return newName;
    }
}
