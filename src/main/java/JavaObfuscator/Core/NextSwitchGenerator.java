package JavaObfuscator.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jack Barker on 30/04/2017.
 */
public class NextSwitchGenerator {
    List<Integer> _inUse = new ArrayList<>();

    int min =0;
    int max = Integer.MAX_VALUE - 1;

    public Integer getRandomInteger(){
        boolean found = false;
        Integer next = 0;
        Random random  = new Random();
        while(!found){

            next = random.nextInt(max);
            if(!_inUse.contains(next)){
                found = true;
                _inUse.add(next);
            }
        }
        return next;
    }

    public void reset() {
        _inUse = new ArrayList<>();
        _inUse = new ArrayList<>();
    }
}
