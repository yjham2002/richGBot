package relations;

import kr.co.shineware.util.common.model.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 함의진
 * 지식 베이스의 추상화 및 캡슐화를 수행하는 클래스
 */
public class KnowledgeBase extends HashMap<String, HashMap<String, Integer>> {

    public static final int ALREADY_KNOW = 100;
    public static final int LEARNED = 200;

    public KnowledgeBase(){}

    public int doYouKnow(String noun, String verb){
        if(this.containsKey(noun)){
            if(this.get(noun).containsKey(verb)){
                return this.get(noun).get(verb);
            }else return 0;
        }else return 0;
    }

    public int doYouKnow(List<Pair<String, String>> nounVerbPair){
        return doYouKnow(nounVerbPair.get(0).getFirst(), nounVerbPair.get(1).getFirst());
    }

    public int learn(String noun, String verb){
        if(this.containsKey(noun)){
            if(this.get(noun).containsKey(verb)){
                HashMap<String, Integer> entry = this.get(noun);
                int population = entry.get(verb);
                entry.remove(verb);
                entry.put(verb, population + 1);
                return ALREADY_KNOW;
            }else {
                HashMap<String, Integer> entry = this.get(noun);
                entry.put(verb, 1);
                return LEARNED;
            }
        }else {
            HashMap<String, Integer> entry = new HashMap<>();
            entry.put(verb, 1);
            this.put(noun, entry);
            return LEARNED;
        }
    }

    public int learn(List<Pair<String, String>> nounVerbPair){
        return learn(nounVerbPair.get(0).getFirst(), nounVerbPair.get(1).getFirst());
    }

}
