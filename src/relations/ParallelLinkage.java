package relations;

import java.util.HashSet;

/**
 * Created by a on 2017-04-14.
 */
public class ParallelLinkage extends HashSet<Integer> {

    public ParallelLinkage(){
        super();
    }

    public ParallelLinkage(Integer... integers){
        super();
        for(Integer i : integers) this.add(i);
    }

    public int getLastIndex(){
        int max = 0;
        while(this.iterator().hasNext()){
            int tmp = this.iterator().next();
            if(max < tmp) max = tmp;
        }

        return max;
    }
}
