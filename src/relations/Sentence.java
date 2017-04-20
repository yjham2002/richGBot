package relations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 함의진
 * @version 1.0
 * @since 2017.04.20
 * 화행 분석 및 문장 단위 추상화를 위한 캡슐화 클래스
 */
public class Sentence extends HashMap<String, ArrayList<TypedPair>>{
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

    /**
     * 멤버 애트리뷰트
     */
    private String speechAct = SPEECH_ACT_UNDEFINED; // 화행 분석 결과
    private double score = 0.0; // 화행 분석 예상 정확도

    /**
     * 문장 추상화 클래스 생성자 - 생성과 동시에 화행 분석을 수행하여 speechAct 변수를 설정하고 확신의 정도를 score에 기록함
     *
     * @param wordList 문장 구성 요소를 입력받기 위한 파라미터로 문장별로 구분되어 전달된 점이 링키지 클래스와 구분됨
     * @param roughAnalysis 대략적인 문장 의도 분석 결과가 포함된 파라미터로 LinkageFactory의 SENTENCE 상수들을 도메인으로 함
     * @param base 문장 구조 지식 베이스
     * @param metaBase 단어 기반 지식 베이스
     */
    public Sentence(List<TypedPair> wordList, int roughAnalysis, KnowledgeBase base, KnowledgeBase metaBase){

    }

}
