package relations;

import analysis.ITrigger;
import analysis.Intention;
import analysis.SpeechActAnalyser;
import kr.co.shineware.util.common.model.Pair;
import nlp.NaturalLanguageEngine;
import react.PurposeEncloser;
import react.Reactor;
import util.KoreanUtil;
import util.TimeExpression;

import java.util.*;

import static relations.LinkageFactory.*;

/**
 * Created by a on 2017-04-19.
 */
public class Linkage {

    private String prediction;
    private double predictionP;

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public double getPredictionP() {
        return predictionP;
    }

    public void setPredictionP(double predictionP) {
        this.predictionP = predictionP;
    }

    private boolean ongoing = false;
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

    public boolean isOngoing(){
        return ongoing;
    }

    /**
     * 모든 상호작용에 대한 응답을 이곳에서 담당한다. 추후 응답 및 시나리오 관련 클래스 생성 시 이 부분을 설계변경해야 한다.
     * @return
     */
    public Pair<List<String>, List<String>> interaction(ITrigger trigger, NaturalLanguageEngine nlp){

        List<Sentence> sentences;
        if(arc != null) {
            sentences = toSentences();
        }else{
            sentences = new ArrayList<>();
        }

        List<String> analysisResposes = new ArrayList<>();

        for(Sentence sentence : sentences){
            for(Intention intention : sentence.getIntentions()){

                boolean triggered = false;
                if(PurposeEncloser.containsCode(intention.getIntentionCode()) && intention.getSpeechAct().equals(SpeechActAnalyser.SPEECH_ACT_REQUEST_ACT)) {
                    System.out.println("ONGOING TRIGGERED");
                    this.ongoing = true;
                    triggered = true;
                }

                assert intention != null;

                HashSet<String> expects = Reactor.extractExpectation(intention, triggered);

                System.out.print("[INFO :: INTENT :: " + intention.getSpeechAct() + "]");

                if(((expects == null && triggered) || (expects != null && expects.contains(intention.getSpeechAct())))){
                    if(Reactor.isEOC()){
                        System.out.println(" / MODE :: END OF PURPOSE");
                        // TODO Finalize
                        nlp.setFinalPurpose(true);
                        this.ongoing = false;
                    }else {
                        System.out.println(" / MODE :: ON PURPOSE");
                        if (!this.ongoing) {
                            trigger.run();
                        }
                        this.ongoing = true;
                    }
                }else{
                    if(triggered){

                    }else{
                        System.out.println(" / MODE :: NOT ON PURPOSE");
                        PurposeEncloser.flush();
                        Reactor.clear();
                        this.ongoing = false;
                    }
                    triggered = false;

                }
            }
            analysisResposes.add(sentence.getSummarized());
        }

        Pair<List<String>, List<String>> pair = new Pair<>(instantResponses, analysisResposes);

        // TODO 임시 테스트 설계로서 문장 클래스로부터 응답을 수행하는 로직이 작성되어야 함

        return pair;
    }

    public List<Sentence> toSentences(){

        SentenceMultiplexer sentenceMultiplexer = new SentenceMultiplexer(arc, base, metaBase, timeExpressions);

        List<Sentence> sentences = sentenceMultiplexer.extractSentences(prediction, predictionP, originalMessage);

        return sentences;
    }

}
