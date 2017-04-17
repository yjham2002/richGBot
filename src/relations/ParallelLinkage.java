package relations;

import java.util.HashSet;

/**
 * Created by a on 2017-04-14.
 */
public class ParallelLinkage extends HashSet<Integer> {

    private int maxValue = -1;
    private int minValue = Integer.MAX_VALUE;

    public ParallelLinkage(){
        super();
    }

    public ParallelLinkage(Integer... integers){
        super();
        for(Integer i : integers) this.add(i);
    }

    @Override
    public boolean add(Integer i){
        if(i < minValue) minValue = i;
        if(maxValue < i) maxValue = i;
        return super.add(i);
    }

    public int getFirstIndex(){
        return minValue;
    }

    public int getLastIndex(){
        return maxValue;
    }
}
