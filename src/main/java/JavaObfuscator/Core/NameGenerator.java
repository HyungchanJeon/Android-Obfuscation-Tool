package JavaObfuscator.Core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Name generator used for generating unique random strings
 *
 * Created by Jack Barker on 5/04/2017.
 */
public class NameGenerator implements INameGenerator {

    private HashMap<String, String> _classNames = new HashMap<>();
    private HashMap<String, String> _methodNames = new HashMap<>();
    private HashMap<String, String> _variablesNames = new HashMap<>();
    private List<String> _originalClassList;
    private List<String> _distinctNames = new ArrayList<>();
    public String generateDistinct(){
        String name = generateName();
        _distinctNames.add(name);
        return name;
    }

    /**
     * Renames a class name, either creating a new random value or if the sting has already been renamed,
     * retrieving the renamed value
     *
     * @param oldName String to be renamed
     * @return New unique random string
     */
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

    /**
     * Renames a method name, either creating a new random value or if the sting has already been renamed,
     * retrieving the renamed value
     *
     * @param oldName String to be renamed
     * @return New unique random string
     */
    public String getMethodName(String oldName){

        if(_classNames.containsKey(oldName)){
            return _classNames.get(oldName);
        }

        if(_methodNames.containsKey(oldName)){
            return _methodNames.get(oldName);
        }

        String newName = generateName();
        _methodNames.put(oldName, newName);

        return newName;
    }

    public void setMethodName(String oldName){

        if(_methodNames.containsKey(oldName)){
            return;
        }

        _methodNames.put(oldName, oldName);
    }

    /**
     * Renames a variable name, either creating a new random value or if the sting has already been renamed,
     * retrieving the renamed value
     *
     * @param oldName String to be renamed
     * @return New unique random string
     */
    public String getVariableName(String oldName){

        if(oldName.equals("menu")){
            return oldName;
        }

        if(_variablesNames.containsKey(oldName)){
            return _variablesNames.get(oldName);
        }

        String newName = generateName();
        _variablesNames.put(oldName, newName);

        return newName;
    }

    /**
     * Generates a unique random string
     *
     * @return A random string
     */
    private String generateName(){
        Random random = new Random();
        boolean nameFound = false;
        String newName = "";

        while(!nameFound) {
            int length = 4 + random.nextInt(20);
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
