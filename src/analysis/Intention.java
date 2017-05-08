package analysis;

import kr.co.shineware.util.common.model.Pair;
import relations.PairCluster;
import util.TimeExpression;

import java.util.HashMap;

/**
 * Created by a on 2017-05-08.
 */
public class Intention {
    private String intentionCode = "";
    private PairCluster subject;
    private PairCluster object;
    private int sentenceType;
    private TimeExpression timeExpression;
    private String speechAct;
    private PairCluster verb;
    private HashMap<String, Object> extra;
    private double confidence;

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
        this.subject = subject;
    }

    public PairCluster getObject() {
        return object;
    }

    public void setObject(PairCluster object) {
        this.object = object;
    }

    public int getSentenceType() {
        return sentenceType;
    }

    public void setSentenceType(int sentenceType) {
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
        this.speechAct = speechAct;
    }

    public PairCluster getVerb() {
        return verb;
    }

    public void setVerb(PairCluster verb) {
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
        return "Intention{" +
                "intentionCode='" + intentionCode + '\'' +
                ", subject=" + subject +
                ", object=" + object +
                ", sentenceType=" + sentenceType +
                ", timeExpression=" + timeExpression +
                ", speechAct='" + speechAct + '\'' +
                ", verb=" + verb +
                ", extra=" + extra +
                ", confidence=" + confidence +
                '}';
    }
}
