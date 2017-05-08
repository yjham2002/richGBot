package relations;

import analysis.DomainSpecifiedAnalyser;
import analysis.SpeechActAnalyser;
import tree.GenericTree;
import tree.GenericTreeNode;
import util.TimeExpression;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static analysis.SpeechActAnalyser.SPEECH_ACT_UNDEFINED;

/**
 * @author 함의진
 * @version 1.0
 * @since 2017.04.20
 * 화행 분석 및 문장 단위 추상화를 위한 캡슐화 클래스
 */
public class Sentence extends GenericTree<PairCluster>{

    /**
     * 멤버 애트리뷰트
     */
    private DomainSpecifiedAnalyser speechAct; // 화행 분석 결과
    private double score = 0.0; // 화행 분석 예상 정확도
    private KnowledgeBase base;
    private KnowledgeBase metaBase;
    private TimeExpression timeExpression;

    private String summarized = "";

    /**
     * 문장 추상화 클래스 생성자 - 생성과 동시에 화행 분석을 수행하여 speechAct 변수를 설정하고 확신의 정도를 score에 기록함
     *
     * @param base 문장 구조 지식 베이스
     * @param metaBase 단어 기반 지식 베이스
     */
    public Sentence(KnowledgeBase base, KnowledgeBase metaBase, GenericTreeNode<PairCluster> root, TimeExpression timeExpression, boolean printProcess){
        super();
        this.base = base;
        this.metaBase = metaBase;
        this.timeExpression = timeExpression;
        this.setRoot(root);

        init(true);

        if(printProcess){
            int intentionNo = 1;

            List<TypedPair> mergedList = PairCluster.nodeToMergedPairList(root.getChildren());

            // Verb-Verb 링키지의 경우, 목적성을 띈 동사구로서 동사의 목적격 구가 됨

            System.out.println("------------------------------------------------------------------------------------");
            System.out.print(" VERBS(N)  :: " + root.getChildren().size() + " :: ");

            for(TypedPair pair : mergedList) {
                System.out.print(pair.getFirst() + "[" +  pair.tenseToString() + "] ");
            }

            System.out.println();
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println(" [ ATOMICS ]");

            for(GenericTreeNode<PairCluster> node : root.getChildren()){
                System.out.print(" :: " + intentionNo++ + " :: ");
                printIntentionLn(node);
            }

            if(timeExpression != null) summarized += "\n" + timeExpression.getExpression() + " :: " + timeExpression.getDateTime();

            System.out.println("------------------------------------------------------------------------------------");
            System.out.println(" INT/CONF  :: [" + this.speechAct.get(SpeechActAnalyser.INTENTION)
                    + "] : " + ((double)this.speechAct.get(SpeechActAnalyser.CONFIDENT) * 100.0f) + "%");
            if(this.speechAct.get(SpeechActAnalyser.TIME) != null) {
                System.out.println(" TIME      :: " + ((TimeExpression) this.speechAct.get(SpeechActAnalyser.TIME)).getDateTime());
            }
            System.out.println(" SUBJECT   :: " + "");//((PairCluster)this.speechAct.get(SpeechActAnalyser.SUBJECT)).toUniqueCSV());
            System.out.println(" OBJECT    :: " + "");//((PairCluster)this.speechAct.get(SpeechActAnalyser.OBJECT)).toUniqueCSV());
            System.out.println(" SNT CLASS :: " + this.speechAct.get(SpeechActAnalyser.SENTENCETYPE));
            System.out.println(" SPEECH    :: " + this.speechAct.get(SpeechActAnalyser.SPEECH));
            List<PairCluster> pairClusters = (List<PairCluster>)this.speechAct.get(SpeechActAnalyser.VERBAL);
            System.out.print(" VERBAL    :: ");
            for(PairCluster cluster : pairClusters) System.out.print(cluster.toUniqueCSV() + " ");
            System.out.println();
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println();

            summarized += "\n" + " INT/CONF  :: [" + this.speechAct.get(SpeechActAnalyser.INTENTION)
                    + "] : " + ((double)this.speechAct.get(SpeechActAnalyser.CONFIDENT) * 100.0f) + "%";
            summarized += "\n" + " SPEECH    :: " + this.speechAct.get(SpeechActAnalyser.SPEECH);
            if(this.speechAct.get(SpeechActAnalyser.TIME) != null) {
                summarized += "\n" + " TIME      :: " + ((TimeExpression) this.speechAct.get(SpeechActAnalyser.TIME)).getDateTime();
            }

        }

    }

    private void init(boolean isDomainSpecified){
        if(isDomainSpecified) {
            this.speechAct = new DomainSpecifiedAnalyser(base, metaBase, this);
        }else{
            // TODO Neural Network needed

            this.speechAct = new DomainSpecifiedAnalyser(base, metaBase, this);
        }

    }

    public TimeExpression getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(TimeExpression timeExpression) {
        this.timeExpression = timeExpression;
    }

    public String getSummarized() {
        return summarized;
    }

    public void setSummarized(String summarized) {
        this.summarized = summarized;
    }

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

    public void printIntentionLn(GenericTreeNode<PairCluster> cluster){
        summarized = "";
        printIntention(cluster);
        System.out.println();

    }

    private void printIntention(GenericTreeNode<PairCluster> cluster){
        if(cluster == null) return;
        System.out.print(cluster.getData().toUniqueCSV() + ":" + cluster.getData().getTag() + ":[" + cluster.getData().getType() + "]->");
        summarized += cluster.getData().toUniqueCSV() + ":" + cluster.getData().getTag() + ":[" + cluster.getData().getType() + "]->";
        for(GenericTreeNode<PairCluster> unit : cluster.getChildren()){
            printIntention(unit);
        }
    }

    public void printStructure(){
        printStructure(this.getRoot(), 0);
    }

    private void printStructure(GenericTreeNode<PairCluster> cluster, int depth){
        if(cluster == null) return;
        System.out.print(" :: DEPTH[" + depth + "] :: NODES["
                + cluster.getChildren().size() + "] :: THIS[" + cluster.getData().toUniqueCSV()
                + "]");
        if(cluster.getParent() != null){
            System.out.print(" :: PARENT[ ");
            for(GenericTreeNode<PairCluster> parent : cluster.getParent()){
                System.out.print(parent.getData().toUniqueCSV() + " *");
            }
            System.out.print("]");

        }
        System.out.println();

        for(GenericTreeNode<PairCluster> unit : cluster.getChildren()){
            printStructure(unit, depth + 1);
        }
    }

}
