package analysis;

import relations.KnowledgeBase;
import relations.PairCluster;
import relations.Sentence;
import relations.TypedPair;
import tree.GenericTreeNode;
import util.KoreanUtil;

import java.util.ArrayList;
import java.util.List;

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

    private boolean generalFact = false;
    private int subCnt = 0;
    private int objCnt = 0;
    private int verbs = 0;
    private int negatives = 0;
    private int positives = 0;
    private int questions = 0;

    private DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase){
        super(base, metaBase);
    }

    public DomainSpecifiedAnalyser(KnowledgeBase base, KnowledgeBase metaBase, Sentence sentence){
        this(base, metaBase);
        this.sentence = sentence;

    }

    public List<Intention> execute(){
        List<Intention> list = new ArrayList<>();

        for(GenericTreeNode<PairCluster> clusterGenericTreeNode : sentence.getRoot().getChildren()){

            // TODO START_POINT

            String speechAct = SPEECH_ACT_UNDEFINED;
            PairCluster subject = null;
            PairCluster object = null;
            int sentenceType = 0;
            double confidence = 0.0d;

            Intention intention = new Intention("intentionCode", subject, object, sentenceType, sentence.getTimeExpression(), speechAct, clusterGenericTreeNode.getData(), confidence);
            list.add(intention);

            traverseAndCount(clusterGenericTreeNode);
        }

        return list;
    }

    public void traverseAndCount(GenericTreeNode<PairCluster> cluster){
        traverseAndCountRecur(cluster);
    }

    private void traverseAndCountRecur(GenericTreeNode<PairCluster> cluster){
        if(cluster == null) return;

        switch (cluster.getData().getType()){
            case TypedPair.TYPE_SUBJECT: subCnt++; break;
            case TypedPair.TYPE_OBJECT: objCnt++; break;
            case TypedPair.TYPE_QUESTION: questions++; break;
            default: break;
        }

        if(KoreanUtil.isVerbal(cluster.getData().getTag())) {
            // TODO START_POINT (인텐션 개수만큼 동적인 인텐션 확장이 필요)
            verbs++;
        }

        for(GenericTreeNode<PairCluster> unit : cluster.getChildren()){
            traverseAndCountRecur(unit);
        }
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
//
//        if(questions > 0) return SENTENCE_QUESTION;
//
//        return SENTENCE_ORDER;
//    }


//    public void analyze(List<TypedPair> know, int what){
//
//        TypedPair objFirst = know.get(0);
//        TypedPair objLast = know.get(1);
//        if(know.get(0).isLinked()){ // TEMPORARY
//            PairCluster pairCluster = new PairCluster("NNG", expandLinkage(know.get(0), arc));
//            objFirst = pairCluster.toCSVTypedPair();
//        }
//        if(know.get(1).isLinked()){ // TEMPORARY
//            PairCluster pairCluster = new PairCluster("NNG", expandLinkage(know.get(1), arc));
//            objLast = pairCluster.toCSVTypedPair();
//        }
//
//        String classified = "";
//
//        switch(what){
//            case SENTENCE_PLAIN: classified = "[평서문] >> "; break;
//            case SENTENCE_ORDER: classified = "[명령문] >> "; break;
//            case SENTENCE_QUESTION: classified = "[의문문] >> "; break;
//            case SENTENCE_META: classified = "[은유형 대입문] >> "; break;
//            case SENTENCE_METAPHORICAL_QUESTION: classified = "[은유형 의문문] >> "; break;
//            default: break;
//        }
//        System.out.print(classified);
//
//        switch(what){
//            case LinkageFactory.SENTENCE_PLAIN: case LinkageFactory.SENTENCE_ORDER: case LinkageFactory.SENTENCE_QUESTION:
//                System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                instantRes.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                if (objLast.getType() == TypedPair.TYPE_ADV) {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                } else if (objLast.getType() == TypedPair.TYPE_SUBJECT) {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                } else if (objLast.getType() == TypedPair.TYPE_QUESTION) {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                } else if (objLast.getType() == TypedPair.TYPE_ADJ) {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                } else if (objLast.getType() == TypedPair.TYPE_VADJ) {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                } else {
////                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
////                }
//                break;
//            case LinkageFactory.SENTENCE_META:
//                if (objFirst.getType() == TypedPair.TYPE_METAPHORE) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [종속 : " + objLast.getFirst() + "] [주체 :" + objFirst.getFirst() + "] [빈도 : " + metaBase.doYouKnow(know) + "]");
//                    instantRes.add("[종속 : " + objLast.getFirst() + "] [주체 :" + objFirst.getFirst() + "] [빈도 : " + metaBase.doYouKnow(know) + "]");
//                }
//                break;
//            case LinkageFactory.SENTENCE_METAPHORICAL_QUESTION:
//                if (objFirst.getType() == TypedPair.TYPE_METAPHORE) {
//                    List<String> backTrack = metaBase.getBackTrackingList(objLast.getFirst(), new ArrayList<>());
//                    System.out.print("\'" + objLast.getFirst() + "\'에 대한 지식 백트랙킹 : [");
//                    String streamStr = "\'" + objLast.getFirst() + "\'에 대한 지식 백트랙킹 : [";
//                    for(int e = 0; e < backTrack.size(); e++){
//                        System.out.print(backTrack.get(e));
//                        streamStr += backTrack.get(e);
//                        if(e < backTrack.size() - 1) {
//                            System.out.print(", ");
//                            streamStr += ", ";
//                        }
//                        else {
//                            System.out.print("]\n");
//                            streamStr += "]\n";
//                        }
//                    }
//                    instantRes.add(streamStr);
//                }
//                break;
//            default: break;
//        }
//
//        if(what != LinkageFactory.SENTENCE_META && what != LinkageFactory.SENTENCE_METAPHORICAL_QUESTION) {
//            base.learn(know);
//        }
//        else if(what == LinkageFactory.SENTENCE_METAPHORICAL_QUESTION){
//            // DO NOTHING
//        }
//        else{
//            metaBase.learn(know);
//        }
//
//    }

}
