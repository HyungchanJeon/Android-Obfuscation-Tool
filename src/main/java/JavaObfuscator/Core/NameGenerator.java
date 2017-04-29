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
    private List<String> _originalClassList;
    private List<String> _distinctNames = new ArrayList<>();
    public String generateDistinct(){
        String name = generateName();
        _distinctNames.add(name);
        return name;
    }

    public String getClassName(String oldName){
        if (!_originalClassList.contains(oldName)) {
            return oldName;
        }

        if(_classNames.containsKey(oldName)){
            return _classNames.get(oldName);
        }

        if(_classNames.containsValue(oldName)){
            return oldName;
        }

        String newName = generateName();
        _classNames.put(oldName, newName);

        return newName;
    }

    @Override
    public void setClassNames(List<String> classNames) {
        _originalClassList = classNames;
    }

    private String generateName(){
        Random random = new Random();
        boolean nameFound = false;
        String newName = "";

        while(!nameFound) {
            int length = 4 + random.nextInt(10);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                int letterIndex = random.nextInt(26);
                char letter = (char) ('A' + letterIndex);
                sb.append(letter);
            }
            newName = sb.toString();
            if(!_classNames.containsKey(newName) && !_classNames.containsValue(newName) && !_distinctNames.contains(newName)){
                nameFound = true;
            }
        }

        return newName;
    }
}
