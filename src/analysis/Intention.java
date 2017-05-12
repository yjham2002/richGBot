package analysis;

import kr.co.shineware.util.common.model.Pair;
import relations.PairCluster;
import util.TimeExpression;

import java.util.HashMap;

import static analysis.SpeechActAnalyser.*;

/**
 * Created by a on 2017-05-08.
 */
public class Intention {

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

    private String intentionCode = "";
    private String originalMessage = "";

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    private PairCluster subject;
    private PairCluster question;
    private PairCluster object;
    private int sentenceType;
    private TimeExpression timeExpression;
    private String speechAct;
    private PairCluster verb;
    private HashMap<String, Object> extra;
    private double confidence;

    private boolean isSubjectSet = false;
    private boolean isObjectSet = false;
    private boolean isSentenceTypeSet = false;
    private boolean isSpeechActSet = false;
    private boolean isVerbSet = false;
    private boolean isQuestionSet = false;

    private boolean includesMeta = false;

    public boolean isIncludesMeta() {
        return includesMeta;
    }

    public void setIncludesMeta(boolean includesMeta) {
        this.includesMeta = includesMeta;
    }

    public boolean isAllSet(){
        return (isSubjectSet && isObjectSet && isVerbSet);
    }

    public boolean isMetaSet(){
        return (isSubjectSet && isObjectSet) || (isSubjectSet && isQuestionSet) && !isVerbSet;
    }

    public boolean isRoughlySet(){
        return (isSubjectSet && isVerbSet);
    }

    public boolean isNoneSubjectSet(){
        return (!isSubjectSet);
    }

    public boolean isNoneVerbalSet(){
        return (!isVerbSet);
    }

    public boolean isQuestionSet(){
        return (isQuestionSet);
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getConfidence() {
        return confidence;
    }

    public PairCluster getQuestion() {
        return question;
    }

    public void setQuestion(PairCluster question) {
        this.isQuestionSet = true;
        this.question = question;
    }

    /**
     * clone 메소드를 위한 내부 생성자
     * @param prediction
     * @param predictionP
     * @param intentionCode
     * @param originalMessage
     * @param subject
     * @param question
     * @param object
     * @param sentenceType
     * @param timeExpression
     * @param speechAct
     * @param verb
     * @param extra
     * @param confidence
     * @param isSubjectSet
     * @param isObjectSet
     * @param isSentenceTypeSet
     * @param isSpeechActSet
     * @param isVerbSet
     * @param isQuestionSet
     * @param includesMeta
     */
    private Intention(String prediction, double predictionP, String intentionCode, String originalMessage, PairCluster subject, PairCluster question, PairCluster object, int sentenceType, TimeExpression timeExpression, String speechAct, PairCluster verb, HashMap<String, Object> extra, double confidence, boolean isSubjectSet, boolean isObjectSet, boolean isSentenceTypeSet, boolean isSpeechActSet, boolean isVerbSet, boolean isQuestionSet, boolean includesMeta) {
        this.prediction = prediction;
        this.predictionP = predictionP;
        this.intentionCode = intentionCode;
        this.originalMessage = originalMessage;
        this.subject = subject;
        this.question = question;
        this.object = object;
        this.sentenceType = sentenceType;
        this.timeExpression = timeExpression;
        this.speechAct = speechAct;
        this.verb = verb;
        this.extra = extra;
        this.confidence = confidence;
        this.isSubjectSet = isSubjectSet;
        this.isObjectSet = isObjectSet;
        this.isSentenceTypeSet = isSentenceTypeSet;
        this.isSpeechActSet = isSpeechActSet;
        this.isVerbSet = isVerbSet;
        this.isQuestionSet = isQuestionSet;
        this.includesMeta = includesMeta;
    }

    public Intention clone(){
        Intention intention = new Intention(prediction, predictionP, intentionCode, originalMessage, subject, question, object, sentenceType, timeExpression, speechAct, verb, extra, confidence, isSubjectSet, isObjectSet, isSentenceTypeSet, isSpeechActSet, isVerbSet, isQuestionSet, includesMeta);
        return intention;
    }

    public Intention(String intentionCode, PairCluster subject, PairCluster object, int sentenceType, TimeExpression timeExpression, String speechAct, PairCluster verb, double confidence, HashMap<String, Object> extra) {
        this.intentionCode = intentionCode;
        this.subject = subject;
        this.object = object;
        this.sentenceType = sentenceType;
        this.timeExpression = timeExpression;
        this.speechAct = speechAct;
        this.verb = verb;
        this.extra = extra;
        this.confidence = confidence;
    }

    public Intention(String intentionCode, PairCluster subject, PairCluster object, int sentenceType, TimeExpression timeExpression, String speechAct, PairCluster verb, double confidence) {
        this.intentionCode = intentionCode;
        this.subject = subject;
        this.object = object;
        this.sentenceType = sentenceType;
        this.timeExpression = timeExpression;
        this.speechAct = speechAct;
        this.verb = verb;
        this.extra = new HashMap<>();
        this.confidence = confidence;
    }

    public Intention(){}

    public String getIntentionCode() {
        return intentionCode;
    }

    public void setIntentionCode(String intentionCode) {
        this.intentionCode = intentionCode;
    }

    public PairCluster getSubject() {
        return subject;
    }

    public void setSubject(PairCluster subject) {
        this.isSubjectSet = true;
        this.subject = subject;
    }

    public PairCluster getObject() {
        return object;
    }

    public void setObject(PairCluster object) {
        this.isObjectSet = true;
        this.object = object;
    }

    public int getSentenceType() {
        return sentenceType;
    }

    public void setSentenceType(int sentenceType) {
        this.isSentenceTypeSet = true;
        this.sentenceType = sentenceType;
    }

    public TimeExpression getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(TimeExpression timeExpression) {
        this.timeExpression = timeExpression;
    }

    public String getSpeechAct() {
        return speechAct;
    }

    public void setSpeechAct(String speechAct) {
        this.isSpeechActSet = true;
        this.speechAct = speechAct;
    }

    public PairCluster getVerb() {
        return verb;
    }

    public void setVerb(PairCluster verb) {
        this.isVerbSet = true;
        this.verb = verb;
    }

    public HashMap<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        String retVal = "INTENTION :: Confidence[" + confidence * 100.0d + "] => CODE[" + intentionCode + "] :: SIMILARITY["+ prediction + "/" + String.format("%.2f", predictionP) +"]";
        if(timeExpression != null) retVal += " Time Expression :: " + timeExpression.getDateTime();
        boolean errorFlag = false;

        retVal += "\n";

        if(speechAct != null) {
            switch (speechAct) {
                case SPEECH_ACT_UNDEFINED:
                    retVal += "화행을 찾을 수 없음";
                    break;
                case SPEECH_ACT_ASK_REF:
                case SPEECH_ACT_ASK_IF:
                    retVal += "정보에 대한 질의 [" + (subject != null ? subject.toUniqueCSV() + " => " : "") + (question != null ? question.toUniqueCSV() : "") + "]";
                    break;
                case SPEECH_ACT_FACT:
                    retVal += "일반 진술 화행 [" + (subject != null ? subject.toUniqueCSV() : "") + (object != null ? " => " + object.toUniqueCSV() : "") + " => " + (verb != null ? " => " + verb.toUniqueCSV() + (verb.isNegative() ? "(부정형)" : "") : "") + "]";
                    break;
                case SPEECH_ACT_INFORM:
                    retVal += "정보 제공 화행 [" + (subject != null ? subject.toUniqueCSV() : "") + (object != null ? " => " + object.toUniqueCSV() : "") + (verb != null ? " => " + verb.toUniqueCSV() + (verb.isNegative() ? "(부정형)" : "") : "") + "]";
                    break;
                case SPEECH_ACT_RESPONSE: // 처리필요
                    retVal += "발화에 대한 응답";
                    break;
                case SPEECH_ACT_REQUEST_ACT: // 빈값
                    retVal += (verb != null ? (verb.isNegative() ? "행위 금지 의도" : "행위 요구 의도") : "") + " [" + (object != null ? object.toUniqueCSV() : "") + " => " + (verb != null ? verb.toUniqueCSV() + (verb.isNegative() ? "(부정형)" : "") : "") + "]";
                    break;
                case SPEECH_ACT_ACCEPT:
                    retVal += "질의에 대한 승인";
                    break;
                case SPEECH_ACT_CORRECT: // 처리 필요
                    retVal += "정정 의도";
                    break;
                case SPEECH_ACT_CONFIRM:
                    retVal += "질의에 대한 확인";
                    break;
                case SPEECH_ACT_REJECT:
                    retVal += "질의에 대한 거절";
                    break;
                default: {
                    retVal += "[ERROR] :: An error occured while processing sentences";
                    errorFlag = true;
                    break;
                }
            }
        }

        if(!errorFlag && extra != null) retVal += "\nExtras[" + extra + "]";

        return retVal;

    }
}
