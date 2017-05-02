package relations;

import kr.co.shineware.util.common.model.Pair;
import relations.KnowledgeBase;
import relations.LinkageFactory;
import relations.MorphemeArc;
import relations.TypedPair;
import util.KoreanUtil;
import util.TimeExpression;

import java.util.*;

import static relations.LinkageFactory.*;

/**
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

        for(int q = 0; q < units.size(); q++) {
            List<Integer> unit = units.get(q);
            for (Integer i : unit) {
                String hashCode = clusters.get(i).hash();
                if (mapper.containsKey(hashCode) && mapper.get(hashCode) != unit) {
                    for (int e = 0; e < unit.size(); e++) {
                        if (unit.get(e) != i) {
                            mapper.get(hashCode).add(unit.get(e));
                        }
                    }
                    redundant.add(q);
                    break;
                } else {
                    mapper.put(hashCode, unit);
                }
            }
        }

        for(Integer del : redundant) {
            units.remove((int)del);
        }

        for(List<Integer> list : units){
            Sentence sentence = new Sentence(base, metaBase);
//  멋진 세호는 맛있는 밥과 반찬을 빠르게 먹었다 그리고 잘생긴 현수는 잠을 잤다.

            sentences.add(sentence);
            for(Integer i : list){
                System.out.print("[" + clusters.get(i).toCSV() + "]->");
            }
            System.out.println();
        }

        return sentences;
    }

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

    public SentenceMultiplexer(MorphemeArc arc, KnowledgeBase base, KnowledgeBase metaBase, List<String> responses, List<TimeExpression> timeExpressions){
        this.arc = arc;
        this.base = base;
        this.metaBase = metaBase;
        this.instantRes = responses;
        this.timeExpressions = timeExpressions;
    }

    public PairCluster getTypedPairToExpandedPairCluster(TypedPair pair, int uniqueKey){
        PairCluster pc = pair.toPairCluster();
        if(pair.isLinked()){ // TEMPORARY
            pc = new PairCluster("NNG", expandLinkage(pair, arc), uniqueKey);
        }
        return pc;
    }

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

