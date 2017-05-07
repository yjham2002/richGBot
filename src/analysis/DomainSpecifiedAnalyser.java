package analysis;

import relations.KnowledgeBase;
import relations.PairCluster;
import relations.Sentence;

/**
 * @author 함의진
 * 화행 분석을 수행하기 위한 클래스로 닫힌 도메인 내에서 분석을 수행하며, 설계상 절대로 정적 멤버를 가져서는 안됨
 *
 * [설계 관련]
 * 화행 분석과 의도 분석에 있어서 큰 복잡도를 요구하는 로직들은 결합도(Coupling)의 증대가 불가피하므로, 응집도(Cohesion)를 고려하지 않고 해당 클래스의 생성과 함께 로직을 수행하도록 한다.
 *
 * 참고논문 및 기저개념 : Recipes for plan inference and domain specific knowledge (Litman and Allen, 1987; Carberry, 1989).
 */
public class DomainSpecifiedAnalyser extends SpeechActAnalyser {

    private DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase){
        super(base, metaBase);
    }

    public DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase, Sentence sentence){
        this(base, metaBase);

        this.clear();

        this.put(SUBJECT, ""); // 행위의 주체
        this.put(OBJECT, ""); // 행위의 대상
        this.put(SENTENCETYPE, ""); // 문장의 대략적 종류
        this.put(TIME, sentence.getTimeExpression()); // 시간 정보 삽입
        this.put(SPEECH, SPEECH_ACT_UNDEFINED); // 화행 분석 결과를 삽입
        this.put(VERBAL, ""); // 동사 삽입

        this.put(INTENTION, ""); // 종합적 정리 문장 (아래 모든 정보 요약)
        // TODO 화행에 따라 Intention 또한 변화해야 함

        this.put(CONFIDENT, 0.5d);
    }

}
