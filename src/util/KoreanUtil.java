package util;

public class KoreanUtil {
    public static final String getComleteWordByJongsung(String name, String firstValue, String secondValue) {
        char lastName = name.charAt(name.length() - 1);
        if (lastName < 0xAC00 || lastName > 0xD7A3) {
            return name;
        }
        String seletedValue = (lastName - 0xAC00) % 28 > 0 ? firstValue : secondValue;
        return name+seletedValue;
    }

}
