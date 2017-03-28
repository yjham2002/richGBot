package util;

import kr.co.shineware.util.common.model.Pair;

public class KoreanUtil {
    public static final String getComleteWordByJongsung(String name, String firstValue, String secondValue) {
        char lastName = name.charAt(name.length() - 1);
        if (lastName < 0xAC00 || lastName > 0xD7A3) {
            return name;
        }
        String seletedValue = (lastName - 0xAC00) % 28 > 0 ? firstValue : secondValue;
        return name+seletedValue;
    }

    public static boolean isConcatHead(Pair<String, String> pair){
        if(pair.getFirst().equals("게") && pair.getSecond().equals("EC")) return true;
        return false;
    }

    public static boolean isDerivable(Pair<String, String> pair){
        if(pair.getSecond().equals("XR") || pair.getSecond().equals("NNG")) return true;
        return false;
    }

    public static boolean isSubjectivePost(Pair<String, String> pair){
        if(pair.getSecond().equals("JKS")) return true;
        return false;
    }

    public static boolean isAdjectiveDeriver(Pair<String, String> pair){
        if(pair.getSecond().equals("XSA")) return true;
        return false;
    }

    public static boolean isVerbalDeriver(Pair<String, String> pair){
        if(pair.getSecond().equals("XSV")) return true;
        return false;
    }

    public static boolean isDeriver(Pair<String, String> pair){
        if(pair.getSecond().equals("XSA") || pair.getSecond().equals("XSV") || pair.getSecond().equals("XSN") || pair.getSecond().equals("XSB")) return true;
        return false;
    }

    public static boolean isObjectivePost(Pair<String, String> pair){
        if(pair.getSecond().equals("JKO")) return true;
        return false;
    }

}
