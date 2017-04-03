package relations;

import DB.DBManager;
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

    public static final int DEBUG_MODE = 100;
    public static final int REAL_MODE = 200;
    public static final int CURRENT_MODE = REAL_MODE;

    public static final int ALREADY_KNOW = 100;
    public static final int LEARNED = 200;

    public DBManager dbManager;

    public static final int TYPE_METAPHOR   = -100;
    public static final int TYPE_SYNONYM    = -200;

    public KnowledgeBase(DBManager dbManager){
        this.dbManager = dbManager;
        wakeUp();
    }

    public int doYouKnow(String key, String subKey){
        if(this.containsKey(key)){
            if(this.get(key).containsKey(subKey)){
                return this.get(key).get(subKey);
            }else return 0;
        }else return 0;
    }

    public int doYouKnow(List<TypedPair> linkPair){
        return doYouKnow(linkPair.get(0).getFirst(), linkPair.get(1).getFirst());
    }

    public double getWeightOf(String noun, String verb){
        double total = 0.0;
        if(!this.containsKey(noun)) return 0.0;
        if(!this.get(noun).containsKey(verb)) return 0.0;

        double current = (double)this.get(noun).get(verb);
        for(String key : this.get(noun).keySet()){
            total += (double)this.get(noun).get(key);
        }

        return current / total;
    }

    private void wakeUp(){
        List<KnowledgeFraction> knowledgeFractions = dbManager.getKnowledges();

        int counter =  1;
        for(KnowledgeFraction know : knowledgeFractions) {
            HashMap<String, Integer> entry = new HashMap<>();
            entry.put(know.getRefWord(), know.getFrequency());
            this.put(know.getWord(), entry);
            if(CURRENT_MODE == REAL_MODE){
                String percentage = String.format("%02.1f", 100.0f * (double)counter++/(double)knowledgeFractions.size()) + "%";
                System.out.println("Loading KnowledgeBase on cache :: (" + percentage + ") :: " + know);
            }
        }
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

    public int learn(List<TypedPair> linkPair){
        if(CURRENT_MODE == REAL_MODE) dbManager.saveKnowledgeLink(linkPair.get(0), linkPair.get(1));
        return learn(linkPair.get(0).getFirst(), linkPair.get(1).getFirst());
    }

}
