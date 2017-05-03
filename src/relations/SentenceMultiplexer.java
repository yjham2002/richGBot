package relations;

import kr.co.shineware.util.common.model.Pair;
import relations.KnowledgeBase;
import relations.LinkageFactory;
import relations.MorphemeArc;
import relations.TypedPair;
import tree.GenericTree;
import tree.GenericTreeNode;
import util.KoreanUtil;
import util.TimeExpression;

import java.util.*;

import static relations.LinkageFactory.*;

/**
 * @author Ham.EuiJin
 * 바이그램으로부터 방향성 그래프를 추상화하여 이를 다중화된 문장 추상화 객체로 분리하기 위한 링키지 위임 클래스
 * Created by a on 2017-04-19.
 */
public class SentenceMultiplexer { // TODO 임시 분리임 - 설계필요

    private MorphemeArc arc;
    private KnowledgeBase base;
    private KnowledgeBase metaBase;
    private  List<TimeExpression> timeExpressions;
    private List<String> instantRes;

    /**
     *
     * 본 메소드는 Sentence에 특화시켜 재작성이 필요함
     */
//    //        int type = isOrder(arc.getWords());
//    private int isOrder(List<TypedPair> words){
//        int questions = 0;
//        for(int i = 0; i < words.size() ; i++) {
//            TypedPair pair = words.get(i); // TODO 문장 구분
//            if(pair.getType() == TypedPair.TYPE_METAPHORE) return SENTENCE_META;
//        }
//
//        for(int i = 0; i < words.size() ; i++) {
//            TypedPair pair = words.get(i); // TODO 문장 구분
//            if(pair.getType() == TypedPair.TYPE_SUBJECT && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))) {
//                if (words.size() > i + 1 && KoreanUtil.isSubjectivePost(words.get(i + 1))) return SENTENCE_PLAIN;
//            }else if(pair.getType() == TypedPair.TYPE_QUESTION && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))){
//                questions++;
//            }
//        }
//
//        if(questions > 0) return SENTENCE_QUESTION;
//
//        return SENTENCE_ORDER;
//    }

    /**
     * 문장 컬렉션 추출을 위한 메소드로 해시를 통해 병합 및 그래프 폐구간 추출을 통한 문장 분리를 수행
     * @return
     */
    public List<Sentence> extractSentences(){
        List<Sentence> sentences = new ArrayList<>();

        HashMap<Integer, List<Integer>> topologies = new HashMap<>();
        HashMap<Integer, PairCluster> clusters = new HashMap<>();

        HashSet<Integer> visited = new HashSet<>();
        HashSet<Integer> remainings = new HashSet<>();

        for(Integer key : arc.keySet()) {
            for(Integer subKey : arc.get(key)) {
                clusters.put(key, getTypedPairToExpandedPairCluster(arc.getWord(key), key));
                clusters.put(subKey, getTypedPairToExpandedPairCluster(arc.getWord(subKey), subKey));

                // Interdirective Connection - Upper Way
                if(topologies.containsKey(key)){
                    topologies.get(key).add(subKey);
                }else{
                    List<Integer> entry = new ArrayList<>();
                    entry.add(subKey);
                    topologies.put(key, entry);
                }
                // Interdirective Connection - Lower Way
                if(topologies.containsKey(subKey)){
                    topologies.get(subKey).add(key);
                }else{
                    List<Integer> entry = new ArrayList<>();
                    entry.add(key);
                    topologies.put(subKey, entry);
                }

                remainings.add(key);
                remainings.add(subKey);
            }
        }

        List<List<Integer>> units = new ArrayList<>();
        HashMap<String, List<Integer>> mapper = new HashMap<>();

        // BFS 를 통해 불연속 경로를 분리/추출한다.
        while(!remainings.isEmpty()) {
            List<Integer> unit = BFS(remainings, topologies, visited);
            units.add(unit);
        }

        List<Integer> redundant = new ArrayList<>();

        for(int q = 0; q < units.size(); q++) { // 선형 순환을 통해 해시된 코드로 오류 분기를 병합하는 과정
            List<Integer> unit = units.get(q);
            for (Integer i : unit) {
                String hashCode = clusters.get(i).hash();
                if (mapper.containsKey(hashCode) && mapper.get(hashCode) != unit) {
                    // 중복 순환 및 재귀 병합을 방지하기 위해 레퍼런스 포인터를 비교
                    for (int e = 0; e < unit.size(); e++) {
                        if (unit.get(e) != i) {
                            mapper.get(hashCode).add(unit.get(e));
                        }
                    }
                    redundant.add(q); // 동시성 이슈를 방지하기 위해 리스트에 추가하여 본 루프 이후 선형처리한다.
                    break;
                } else {
                    mapper.put(hashCode, unit);
                }
            }
        }

        for(Integer del : redundant) { // 선형 병합 처리
            units.remove((int)del); // Integer To Int 캐스팅 (중요)
        }

        for(List<Integer> list : units){

        //  멋진 세호는 맛있는 밥과 반찬을 빠르게 먹고 토했다 그리고 잘생긴 현수는 잠을 잤다.

            // TODO 추상 구문 트리에 대해 방향성을 가진 N-ary 트리로 링크해야 함

            HashMap<String, GenericTreeNode<PairCluster>> nodeStorage = new HashMap<>();
            List<GenericTreeNode<PairCluster>> roots = new ArrayList<>();

            for(Integer i : list){

                PairCluster cursor = clusters.get(i);

                if(!nodeStorage.containsKey(cursor.hash())){
                    nodeStorage.put(cursor.hash(), new GenericTreeNode<>(cursor));
                }

                if(arc.containsKey(i)){
                    for(Integer l : arc.get(i)) {
                        if(clusters.containsKey(l)) {
                            PairCluster link = clusters.get(l);
                            if(!nodeStorage.containsKey(link.hash())) nodeStorage.put(link.hash(), new GenericTreeNode<>(link));
                            nodeStorage.get(link.hash()).addChild(nodeStorage.get(cursor.hash()));

                        }
                    }
                }else {
                    roots.add(nodeStorage.get(cursor.hash()));
                }

            }

            GenericTreeNode<PairCluster> temporaryRoot = new GenericTreeNode<>(PairCluster.createDummy());
            temporaryRoot.setChildren(roots);

            double weightOfTime = Double.MAX_VALUE;
            TimeExpression candidate = null;

            for(TimeExpression time : timeExpressions){
                double weight = Math.abs(((double)time.getStart() + (double)time.getEnd() - (double)temporaryRoot.getData().getUniqueKey()));
                if(weightOfTime > weight) {
                    weightOfTime = weight;
                    candidate = time;
                }
            }

            Sentence sentence = new Sentence(base, metaBase, temporaryRoot, candidate, true); // 지식베이스 인스턴스 전달

            sentences.add(sentence); // 의도 추출 전 문장 객체를 수집

        }

        return sentences;
    }

    /**
     * BFS 직접 호출을 위한 주체 메소드로 해시맵을 통해 추상화된 그래프 구조를 순회하며 불연속 지점을 추출하여 반환
     * @param remainings
     * @param map
     * @param visited
     * @return 폐구간을 수집하여 추출된 인덱스를 리스트 형태로 반환
     */
    public List<Integer> BFS(HashSet<Integer> remainings, HashMap<Integer, List<Integer>> map, HashSet<Integer> visited) {
        if(map.isEmpty() || !remainings.iterator().hasNext()) return null;
        Integer start = remainings.iterator().next();
        List<Integer> unit = new ArrayList<>();
        unit.add(start);
        remainings.remove(start);
        visited.add(start);
        BFS(remainings, start, map, visited, unit);
        return unit;
    }

    /**
     * 재귀 순환을 위한 너비우선 탐색 메소드로 위 메소드의 종속 메소드
     * @param remainings
     * @param start
     * @param map
     * @param visited
     * @param unit
     */
    private void BFS(HashSet<Integer> remainings, Integer start, HashMap<Integer, List<Integer>> map, HashSet<Integer> visited, List<Integer> unit) {
        if(!map.containsKey(start)) return;
        for( Integer neighbor : map.get(start)) {
            if(!visited.contains(neighbor)) {
                remainings.remove(neighbor);
                unit.add(neighbor);
            }
        }

        for(Integer neighbor : map.get(start)) {
            if(!visited.contains(neighbor)) {
                visited.add(neighbor);
                BFS(remainings, neighbor, map, visited, unit);
            }
        }
    }

    /**
     * 문장 다중화기의 생성자 오버로드
     * @param arc
     * @param base
     * @param metaBase
     * @param responses
     * @param timeExpressions
     */
    public SentenceMultiplexer(MorphemeArc arc, KnowledgeBase base, KnowledgeBase metaBase, List<String> responses, List<TimeExpression> timeExpressions){
        this.arc = arc;
        this.base = base;
        this.metaBase = metaBase;
        this.instantRes = responses;
        this.timeExpressions = timeExpressions;
    }

    /**
     * 타입페어의 클러스터링을 위한 메소드
     * @param pair
     * @param uniqueKey
     * @return
     */
    public PairCluster getTypedPairToExpandedPairCluster(TypedPair pair, int uniqueKey){
        PairCluster pc = pair.toPairCluster();
        if(pair.isLinked()){ // TEMPORARY
            pc = new PairCluster("NNG", expandLinkage(pair, arc), uniqueKey);
        }
        return pc;
    }

    /**
     * 타입페어가 클러스터링된 상태인지 확인하여 이를 확장하는 메소드
     * @param pair
     * @param arc
     * @return
     */
    private List<TypedPair> expandLinkage(TypedPair pair, MorphemeArc arc){
        List<TypedPair> list = new ArrayList<>();
        if(pair.isLinked()){
            Iterator<Integer> iterator = pair.getParallelLinkage().iterator();
            while(iterator.hasNext()){
                Integer unit = iterator.next();
                list.add(arc.getWords().get(unit));
            }
        }else{
            list.add(pair);
        }
        return list;
    }

}

