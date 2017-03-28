package relations;

import kr.co.shineware.util.common.model.Pair;

/**
 * Created by a on 2017-03-28.
 */
public class TypedPair extends Pair<String, String> {

    public static final int TYPE_DEFAULT = -1;

    public static final int TYPE_VERB        = 0;
    public static final int TYPE_SUBJECT     = 1;
    public static final int TYPE_OBJECT      = 2;
    public static final int TYPE_ADV         = 3;

    private int type = TYPE_DEFAULT;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
