package relations;

import kr.co.shineware.util.common.model.Pair;

/**
 * Created by a on 2017-03-28.
 */
public class TypedPair extends Pair<String, String> {

    public static final int TYPE_DEFAULT = -1;

    public static final int TYPE_VERB        = 0; // 동사
    public static final int TYPE_SUBJECT     = 1; // 주어
    public static final int TYPE_OBJECT      = 2; // 목적어
    public static final int TYPE_ADV         = 3; // 수식언
    public static final int TYPE_QUESTION    = 4; // 의문사
    public static final int TYPE_ADJ    = 5; // 형용사 - 관형형전성어미에 의한 형용언의 형용사화
    public static final int TYPE_VADJ    = 6; // 형용사 - 관형형전성어미에 의한 동사의 형용사화
    public static final int TYPE_METAPHORE    = 7; // 형용사 - 관형형전성어미에 의한 동사의 형용사화

    private int type = TYPE_DEFAULT;
    private boolean linked = false;

    private ParallelLinkage parallelLinkage;

    public TypedPair(){}

    public TypedPair(Pair<String, String> pair){
        this.setFirst(pair.getFirst());
        this.setSecond(pair.getSecond());
    }

    public TypedPair(String first, String second, int type){
        this.setFirst(first);
        this.setSecond(second);
        this.type = type;
    }

    public ParallelLinkage getParallelLinkage() {
        return parallelLinkage;
    }

    public void setParallelLinkage(ParallelLinkage parallelLinkage) {
        this.parallelLinkage = parallelLinkage;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    public boolean equals(TypedPair pair){
        if(this.getFirst().equals(pair.getFirst()) && this.getSecond().equals(pair.getSecond())) return true;
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
