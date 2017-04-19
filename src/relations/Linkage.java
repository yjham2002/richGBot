package relations;

import response.ResponseGenerator;
import util.KoreanUtil;
import util.TimeExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static relations.LinkageFactory.*;

/**
 * Created by a on 2017-04-19.
 */
public class Linkage {

    private String originalMessage;
    private List<TimeExpression> timeExpressions;
    private HashMap<Integer, Integer> timeRange;
    private MorphemeArc arc;
    private List<String> instantResponses;
    private KnowledgeBase base; // 가중치 부여 및 단순 문장 링킹를 위한 지식베이스
    private KnowledgeBase metaBase; // 은유적 1:N 관계를 기술하기 위한 지식베이스 (단, 1은 큰 범위의 의미이고 N은 작은 범위의 의미)
    private KnowledgeBase staticBase; // 정적 문장에 대한 단순 매칭을 수행하는 정적 지식베이스

    public Linkage(){}

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public HashMap<Integer, Integer> getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(HashMap<Integer, Integer> timeRange) {
        this.timeRange = timeRange;
    }

    public List<TimeExpression> getTimeExpressions() {
        return timeExpressions;
    }

    public void setTimeExpressions(List<TimeExpression> timeExpressions) {
        this.timeExpressions = timeExpressions;
    }

    public KnowledgeBase getBase() {
        return base;
    }

    public void setBase(KnowledgeBase base) {
        this.base = base;
    }

    public KnowledgeBase getMetaBase() {
        return metaBase;
    }

    public void setMetaBase(KnowledgeBase metaBase) {
        this.metaBase = metaBase;
    }

    public KnowledgeBase getStaticBase() {
        return staticBase;
    }

    public void setStaticBase(KnowledgeBase staticBase) {
        this.staticBase = staticBase;
    }

    public MorphemeArc getArc() {
        return arc;
    }

    public void setArc(MorphemeArc arc) {
        this.arc = arc;
    }

    public List<String> getInstantResponses() {
        return instantResponses;
    }

    public void setInstantResponses(List<String> instantResponses) {
        this.instantResponses = instantResponses;
    }

    private int isOrder(List<TypedPair> words){
        int questions = 0;
        for(int i = 0; i < words.size() ; i++) {
            TypedPair pair = words.get(i); // TODO 문장 구분
            if(pair.getType() == TypedPair.TYPE_METAPHORE) return SENTENCE_META;
        }

        for(int i = 0; i < words.size() ; i++) {
            TypedPair pair = words.get(i); // TODO 문장 구분
            if(pair.getType() == TypedPair.TYPE_SUBJECT && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))) {
                if (words.size() > i + 1 && KoreanUtil.isSubjectivePost(words.get(i + 1))) return SENTENCE_PLAIN;
            }else if(pair.getType() == TypedPair.TYPE_QUESTION && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))){
                questions++;
            }
        }

        if(questions > 0) return SENTENCE_QUESTION;

        return SENTENCE_ORDER;
    }

    private void addProcData(MorphemeArc arc, int what){
        for(Integer key : arc.keySet()) {
            for(Integer subKey : arc.get(key)) {
                addProcData(arc.getWord(key), arc.getWord(subKey), what, arc);
            }
        }
    }

    private void addProcData(TypedPair pop, TypedPair word, int what, MorphemeArc arc){
        int type = what;
        List<TypedPair> temp = new ArrayList<>();
        if(KoreanUtil.isMetaQuestion(word)){
            type = SENTENCE_METAPHORICAL_QUESTION;
        }

        temp.add(word);
        temp.add(pop);

        ResponseGenerator responseGenerator = new ResponseGenerator(temp, type, arc, base, metaBase, instantResponses);
    }

    public void printResult(){
        if(arc != null) addProcData(arc, isOrder(arc.getWords()));
    }

    public List<String> interaction(){
        if(arc != null) addProcData(arc, isOrder(arc.getWords()));
        return instantResponses;
    }

    public List<Sentence> toSentences(){
        List<Sentence> sentences = new ArrayList<>();



        return sentences;
    }

}
