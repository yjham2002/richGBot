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

    private int uniqueKey;
    private List<TypedPair> list;
    private String csv = "";
    private String tag = "";

    public PairCluster(String tag, List<TypedPair> pairs){
        super();

        this.tag = tag;
        this.list = new ArrayList<>();
        for(int i = 0; i < pairs.size(); i++) {
            TypedPair pair = pairs.get(i);
            list.add(pair);
            this.csv += pair.getFirst();
            if(i + 1 < pairs.size()) this.csv += ",";
            this.add(pair);
        }
    }

    public PairCluster(String tag, List<TypedPair> pairs, int uniqueKey){
        this(tag, pairs);
        this.uniqueKey = uniqueKey;
    }

    public PairCluster(String tag, TypedPair... pairs){
        super();

        this.tag = tag;
        this.list = new ArrayList<>();
        for(int i = 0; i < pairs.length; i++) {
            TypedPair pair = pairs[i];
            list.add(pair);
            this.csv += pair.getFirst();
            if(i + 1 < pairs.length) this.csv += ",";
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

    public String getTag() {
        return tag;
    }
}
