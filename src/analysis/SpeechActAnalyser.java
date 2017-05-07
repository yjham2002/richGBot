package analysis;

import relations.KnowledgeBase;
import relations.PairCluster;
import relations.Sentence;

import java.util.HashMap;

/**
 * @author 함의진
 * 화행 분석을 수행하기 위한 클래스로, 절대로 정적 멤버를 사용해서는 안됨
 */
public abstract class SpeechActAnalyser extends HashMap<String, Object>{

    private KnowledgeBase base;
    private KnowledgeBase metaBase;
    private Sentence sentence;

    /**
     * 참고 논문 : 서강대학교 "한국어 대화체 문장의 화행 분석", 이현정 외, 1996
     */
    public static final String SPEECH_ACT_UNDEFINED = "undefined"; // 미정
    public static final String SPEECH_ACT_ASK_REF = "ask_ref"; // 정보요구 (REFERENCE)
    public static final String SPEECH_ACT_ASK_IF = "ask_if"; // 정보 요구 (YES or NO)
    public static final String SPEECH_ACT_INFORM = "inform"; // 정보 제공
    public static final String SPEECH_ACT_RESPONSE = "response"; // 응답
    public static final String SPEECH_ACT_REQUEST_CONF = "request_conf"; // 확인 요구
    public static final String SPEECH_ACT_REQUEST_ACT = "request_act"; // 행위 요구
    public static final String SPEECH_ACT_ACCEPT = "accept"; // 호응 (긍정적 반응)
    public static final String SPEECH_ACT_CORRECT = "correct"; // 정정
    public static final String SPEECH_ACT_CONFIRM = "confirm"; // 확인 (확인을 요구하는 발화에 대한 응답)
    public static final String SPEECH_ACT_GREETING = "greeting"; // 인사말
    public static final String SPEECH_ACT_PROMISE = "promise"; // 특정 행위를 약속
    public static final String SPEECH_ACT_REJECT = "reject"; // 거절

    public static final String OBJECT = "OBJECT";
    public static final String SUBJECT = "SUBJECT";
    public static final String CONFIDENT = "CONFIDENT";
    public static final String TIME = "TIME";
    public static final String SENTENCETYPE = "SENTENCETYPE";
    public static final String INTENTION = "INTENTION";
    public static final String SPEECH = "SPEECH";
    public static final String VERBAL = "VERBAL";

    public static final int SENTENCE_ORDER = 10;
    public static final int SENTENCE_PLAIN = 20;
    public static final int SENTENCE_QUESTION = 30;
    public static final int SENTENCE_META = 40;
    public static final int SENTENCE_METAPHORICAL_QUESTION = 50;

    protected SpeechActAnalyser(){}

    protected SpeechActAnalyser(KnowledgeBase base, KnowledgeBase metaBase){
        this();
        this.base = base;
        this.metaBase = metaBase;
    }

}
