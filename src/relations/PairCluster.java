package relations;

import kr.co.shineware.util.common.model.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author 함의진
 * 병렬 단위 혹은 단일의 태깅 개체를 클러스터링하기 위한 클래스로 CSV(콤마 구분 문자)와 리스트화를 지원한다.
 */
public class PairCluster extends HashSet<TypedPair> {

    public static final String TAG_ROOT = "TAG_ROOT_CONST_STRING";
    public static final int KEY_ROOT = -100;

    private int uniqueKey;
    private List<TypedPair> list;
    private String csv = "";
    private String csvUnique = "";
    private String tag = "";

    public boolean equals(PairCluster pairCluster){
        if(this.list.size() != pairCluster.list.size()) return false;
        for(int i = 0; i < this.list.size(); i++){
            if(this.list.get(i).getDivisionKey() != pairCluster.list.get(i).getDivisionKey()) return false;
            if(!this.list.get(i).getFirst().equals(pairCluster.list.get(i).getFirst())) return false;
            if(!this.list.get(i).getSecond().equals(pairCluster.list.get(i).getSecond())) return false;
        }
        return true;
    }

    public String hash(){
        String hash = "HASH_START_" + this.list.size() + "_SIZE_";
        for(int i = 0; i < this.list.size(); i++){
            hash += i + "_IDX_" + this.list.get(i).getDivisionKey() + "_DIV_" + this.list.get(i).getFirst() + "_PAIR_" + this.list.get(i).getSecond() + "_HASH_END";
        }
        return hash;
    }

    public PairCluster(String tag, List<TypedPair> pairs){
        super();

        this.tag = tag;
        this.list = new ArrayList<>();
        for(int i = 0; i < pairs.size(); i++) {
            TypedPair pair = pairs.get(i);
            list.add(pair);
            this.csv += pair.getFirst();
            this.csvUnique += pair.getFirst() + "[" + pair.getDivisionKey() + "]";
            if(i + 1 < pairs.size()) {
                this.csv += ",";
                this.csvUnique += ",";
            }
            this.add(pair);
        }
    }

    public PairCluster(String tag, List<TypedPair> pairs, int uniqueKey){
        this(tag, pairs);
        this.uniqueKey = uniqueKey;
    }

    private PairCluster(){}

    public static PairCluster createDummy(){
        PairCluster dummy = new PairCluster();
        dummy.tag = TAG_ROOT;

        return dummy;
    }

    public static boolean isDummy(PairCluster cluster){
        return cluster.tag.equals(PairCluster.TAG_ROOT);
    }

    public PairCluster(String tag, TypedPair... pairs){
        super();

        this.tag = tag;
        this.list = new ArrayList<>();
        for(int i = 0; i < pairs.length; i++) {
            TypedPair pair = pairs[i];
            list.add(pair);
            this.csv += pair.getFirst();
            this.csvUnique += pair.getFirst() + "[" + pair.getDivisionKey() + "]";
            if(i + 1 < pairs.length) {
                this.csv += ",";
                this.csvUnique += ",";
            }
            this.add(pair);
        }
    }

    public int getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(int uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public TypedPair toCSVTypedPair(){
       return new TypedPair(csv, tag, TypedPair.TYPE_DEFAULT);
    }

    public List<TypedPair> toList(){
        return list;
    }

    public String toCSV() {
        return csv;
    }

    public String toUniqueCSV(){
        return csvUnique;
    }

    public String getTag() {
        return tag;
    }
}
