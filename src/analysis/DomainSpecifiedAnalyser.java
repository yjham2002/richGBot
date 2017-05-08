package analysis;

import relations.KnowledgeBase;
import relations.PairCluster;
import relations.Sentence;
import relations.TypedPair;
import tree.GenericTreeNode;
import util.KoreanUtil;

import java.util.ArrayList;
import java.util.List;

import static relations.LinkageFactory.SUBJECTS;

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

    private Sentence sentence;
    private PairCluster subject;
    private PairCluster object;
    private int sentenceType;
    private List<PairCluster> clusters;
    private double confidence = 0.0d;

    private DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase){
        super(base, metaBase);
    }

    public DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase, Sentence sentence){
        this(base, metaBase);
        this.sentence = sentence;

        // TODO START_POINT

        init();

        String speechAct = getSpeechAct();

        this.put(SUBJECT, subject); // 행위의 주체
        this.put(OBJECT, object); // 행위의 대상
        this.put(SENTENCETYPE, sentenceType); // 문장의 대략적 종류
        this.put(TIME, sentence.getTimeExpression()); // 시간 정보 삽입
        this.put(SPEECH, speechAct); // 화행 분석 결과를 삽입
        this.put(VERBAL, clusters); // 동사 삽입

        this.put(INTENTION, ""); // 종합적 정리 문장 (위 모든 정보 요약)
        // TODO 화행에 따라 Intention 또한 변화해야 함

        this.put(CONFIDENT, confidence);
    }

    private void init(){
        this.clear();
        clusters = new ArrayList<>();
        for(GenericTreeNode<PairCluster> clusterGenericTreeNode : sentence.getRoot().getChildren()){
            clusters.add(clusterGenericTreeNode.getData());
        }

        subject = null;
        object = null;
        sentenceType = 0;
        confidence = 0.0d;
    }

    private String getSpeechAct(){
        return SPEECH_ACT_UNDEFINED;
    }

//    private int getRoughType() {
//        int questions = 0;
//        for (int i = 0; i < words.size(); i++) {
//            TypedPair pair = words.get(i); // TODO 문장 구분
//            if (pair.getType() == TypedPair.TYPE_METAPHORE) return SENTENCE_META;
//        }
//
//        for (int i = 0; i < words.size(); i++) {
//            TypedPair pair = words.get(i); // TODO 문장 구분
//            if (pair.getType() == TypedPair.TYPE_SUBJECT && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))) {
//                if (words.size() > i + 1 && KoreanUtil.isSubjectivePost(words.get(i + 1))) return SENTENCE_PLAIN;
//            } else if (pair.getType() == TypedPair.TYPE_QUESTION && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))) {
//                questions++;
//            }
//        }
//    }

}
