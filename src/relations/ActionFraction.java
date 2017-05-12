package relations;

/**
 * Created by a on 2017-05-12.
 */
public class ActionFraction {
    private String keyValue;
    private String original;
    private String objectSerial;
    private String verbSerial;
    private String intentionCode;
    private String desc;
    private int frequency;

    public ActionFraction(String original, String objectSerial, String verbSerial, String intentionCode, String desc, int frequency, String keyValue) {
        this.original = original;
        this.objectSerial = objectSerial;
        this.verbSerial = verbSerial;
        this.intentionCode = intentionCode;
        this.desc = desc;
        this.frequency = frequency;
        this.keyValue = keyValue;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getObjectSerial() {
        return objectSerial;
    }

    public void setObjectSerial(String objectSerial) {
        this.objectSerial = objectSerial;
    }

    public String getVerbSerial() {
        return verbSerial;
    }

    public void setVerbSerial(String verbSerial) {
        this.verbSerial = verbSerial;
    }

    public String getIntentionCode() {
        return intentionCode;
    }

    public void setIntentionCode(String intentionCode) {
        this.intentionCode = intentionCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "ActionFraction{" +
                "original='" + original + '\'' +
                ", objectSerial='" + objectSerial + '\'' +
                ", verbSerial='" + verbSerial + '\'' +
                ", intentionCode='" + intentionCode + '\'' +
                ", desc='" + desc + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
