package analysis;

import relations.KnowledgeBase;
import relations.PairCluster;
import relations.Sentence;

import java.util.HashMap;

/**
 * @author 함의진
 * 화행 분석을 수행하기 위한 클래스로, 절대로 정적 멤버를 사용해서는 안됨
 */
public abstract class SpeechActAnalyser{

    private KnowledgeBase base;
    private KnowledgeBase metaBase;
    private Sentence sentence;

    /**
     * 참고 논문 : 서강대학교 "한국어 대화체 문장의 화행 분석", 이현정 외, 1996
     */
    public static final String SPEECH_ACT_UNDEFINED = "undefined"; // 미정
    // 화행 분석 결과가 존재하지 않거나 화행 분석 이전인 경우로, 초기화 값임

    public static final String SPEECH_ACT_ASK_REF = "ask_ref"; // 정보요구 (REFERENCE)
    // 의문사를 포함여부를 통해 검출하며, 요구하는 정보를 알 수 없는 경우, 모호성 답변을 제공하며,
    // 요구하는 정보를 알 수 없으며(AND), 스펙도메인과 일치하지 않는(AND) 경우, 격양된 감정 상태로 해석하여 정정 요구 화행으로 전환

    public static final String SPEECH_ACT_ASK_IF = "ask_if"; // 정보 요구 (YES or NO)
    // 정보 제공과 같은 문장 구조로서, 국문학적으로 은유적 표현의 구조를 가진 경우 검출하며, 요구하는 정보가 도메인에 포함된 경우에 한해 정보 요구로 해석

    public static final String SPEECH_ACT_INFORM = "inform"; // 정보 제공
    // 정보 요구와 같은 문장 구조로서, 국문학적으로 은유적 표현의 구조를 가진 경우 검출하며, 요구하는 정보가 도메인에 포함되지 않은 경우에 한해 정보 제공으로 해석

    public static final String SPEECH_ACT_RESPONSE = "response"; // 응답
    // 홑단어 혹은 구 상태이며, 이전 대화에서 엔진으로부터 화자가 질문을 받은 경우에 한해 검출

    public static final String SPEECH_ACT_REQUEST_ACT = "request_act"; // 행위 요구
    // 시간이 구체적이지 않으며, 현재 시제의 동사만이 포함된 화행으로 명령형 어조를 띄는 경우 행위 요구로 분류

    public static final String SPEECH_ACT_ACCEPT = "accept"; // 호응 (긍정적 반응)
    // 긍/부정이 구분되며, 이전 대화에서 엔진의 발화에 대해 확인 화행의 조건에 부합하지 아니하는 경우

    public static final String SPEECH_ACT_CORRECT = "correct"; // 정정
    // 평문형 문장 구조로서, 사실을 나열하며 문장 구조상 평이함과 동시에 부정형 형태소가 다수 포함된 경우, 정정 화행으로 해석

    public static final String SPEECH_ACT_CONFIRM = "confirm"; // 확인 (확인을 요구하는 발화에 대한 응답)
    // 홑단어 혹은 짧은 구구조이며 긍/부정이 확실하게 구분되고, 이전 대화에서 엔진으로부터 화자가 질문을 받은 경우에 한해 검출(긍정)

    public static final String SPEECH_ACT_GREETING = "greeting"; // 인사말
    // 지식베이스로부터 유사성 기반의 인삿말로 추정되는 경우

    public static final String SPEECH_ACT_PROMISE = "promise"; // 특정 행위를 약속
    // 시간을 구체적으로 언급하며 현재 시제의 동사만이 포함된 화행으로 명령형 어조를 띄는 경우 행위 약속으로 분류

    public static final String SPEECH_ACT_REJECT = "reject"; // 거절
    // 홑단어 혹은 짧은 구구조이며 긍/부정이 확실하게 구분되고, 이전 대화에서 엔진으로부터 화자가 질문을 받은 경우에 한해 검출(부정)

    //    public static final String SPEECH_ACT_REQUEST_CONF = "request_conf"; // 확인 요구 (정보 제공 화행과 구분이 모호하여 삭제)

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
