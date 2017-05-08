package relations;

import analysis.DomainSpecifiedAnalyser;
import analysis.Intention;
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

        List<Intention> intentions = speechAct.execute();

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

            System.out.println("------------------------------------------------------------------------------------");

            if(timeExpression != null) summarized += "\n" + timeExpression.getExpression() + " :: " + timeExpression.getDateTime();

            int cnt = 1;
            for(Intention intention : intentions) {
                summarized += "\n" + " Intention No." + cnt++ + " :: " + intention.toString();
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
