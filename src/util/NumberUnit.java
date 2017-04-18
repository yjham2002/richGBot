package util;

/**
 * Created by a on 2017-04-18.
 */
public class NumberUnit {
    private String desc;
    private String tag;
    private int value;

    public NumberUnit(String desc, String tag, int value){
        this.desc = desc;
        this.tag = tag;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
