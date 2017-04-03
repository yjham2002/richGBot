package relations;

/**
 * Created by a on 2017-04-03.
 */
public class KnowledgeFraction {
    private String word;
    private String refWord;
    private int frequency;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getRefWord() {
        return refWord;
    }

    public void setRefWord(String refWord) {
        this.refWord = refWord;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "KnowledgeFraction{" +
                "word='" + word + '\'' +
                ", refWord='" + refWord + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
