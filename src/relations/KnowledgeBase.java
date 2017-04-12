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

    public static final int SET_SENTENCE_RECOGNIZE = 100;
    public static final int SET_METAPHOR_RECOGNIZE = 200;

    private int current_set = SET_SENTENCE_RECOGNIZE;

    public static final int DEBUG_MODE = 100;
    public static final int REAL_MODE = 200;
    public static final int CURRENT_MODE = REAL_MODE;

    public static final int ALREADY_KNOW = 100;
    public static final int LEARNED = 200;

    public DBManager dbManager;

    public static final int TYPE_METAPHOR   = -100;
    public static final int TYPE_SYNONYM    = -200;

    public KnowledgeBase(DBManager dbManager, int set){
        this.dbManager = dbManager;
        this.current_set = set;
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
        List<KnowledgeFraction> knowledgeFractions;
        int counter =  1;

        switch (current_set){
            case SET_SENTENCE_RECOGNIZE :
                System.out.println("[INFO :: Loading [SETENCE RECOGNIZER] cache has been started.]");
                knowledgeFractions = dbManager.getKnowledges();

                for(KnowledgeFraction know : knowledgeFractions) {
                    HashMap<String, Integer> entry = new HashMap<>();
                    entry.put(know.getRefWord(), know.getFrequency());
                    if(this.containsKey(know.getWord())) {
                        this.get(know.getWord()).put(know.getRefWord(), know.getFrequency());
                    }else{
                        this.put(know.getWord(), entry);
                    }
                    if(CURRENT_MODE == DEBUG_MODE){
                        String percentage = String.format("%02.1f", 100.0f * (double)counter++/(double)knowledgeFractions.size()) + "%";
                        System.out.println("Loading KnowledgeBase on cache :: (" + percentage + ") :: " + know);
                    }
                }
                break;
            case SET_METAPHOR_RECOGNIZE :
                System.out.println("[INFO :: Loading [METAPHOR RECOGNIZER] cache has been started.]");
                knowledgeFractions = dbManager.getMetaphors();

                for(KnowledgeFraction know : knowledgeFractions) {
                    HashMap<String, Integer> entry = new HashMap<>();
                    entry.put(know.getRefWord(), know.getFrequency());
                    if(this.containsKey(know.getWord())) {
                        this.get(know.getWord()).put(know.getRefWord(), know.getFrequency());
                    }else{
                        this.put(know.getWord(), entry);
                    }
                    if(CURRENT_MODE == DEBUG_MODE){
                        String percentage = String.format("%02.1f", 100.0f * (double)counter++/(double)knowledgeFractions.size()) + "%";
                        System.out.println("Loading MetaphoreBase on cache :: (" + percentage + ") :: " + know);
                    }
                }
                break;
            default: break;
        }

        System.out.println("[INFO :: Loading cache has done successfully.]");

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

    public String getRootMeaning(String current){
        if(current_set == SET_SENTENCE_RECOGNIZE) {
            System.out.println("[DEBUG :: 문장인식기 내에서의 루트 탐색 호출 - 예상치 못한 결과가 도출될 수 있음]");
        }

        for(String key : this.keySet()){
            if(this.get(key).containsKey(current)){
                return getRootMeaning(key);
            }
        }

        return current;
    }

    public int learn(List<TypedPair> linkPair){
        if(CURRENT_MODE == REAL_MODE) {
            boolean reverse = linkPair.get(1).getType() == TypedPair.TYPE_ADV;
            switch (current_set){
                case SET_SENTENCE_RECOGNIZE : dbManager.saveKnowledgeLink(linkPair.get(0), linkPair.get(1), reverse); break;
                case SET_METAPHOR_RECOGNIZE : dbManager.saveMetaLink(linkPair.get(0), linkPair.get(1)); break;
                default: break;
            }
        }
        return learn(linkPair.get(0).getFirst(), linkPair.get(1).getFirst());
    }

}
