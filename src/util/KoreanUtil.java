package util;

import kr.co.shineware.util.common.model.Pair;
import relations.TypedPair;

import java.util.StringTokenizer;

public class KoreanUtil {

    private static final String TAG_POSITIVE_DESIGNATOR = "VCP";

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

    public static boolean isEOS(Pair<String, String> pair){
        if((pair.getFirst().equals("다") || pair.getFirst().equals("야") || pair.getFirst().equals("이야")) && pair.getSecond().equals("JX")) return true;
        return false;
    }

    public static boolean isDeterminingHead(Pair<String, String> pair){
        if(pair.getSecond().equals("ETM")) return true;
        return false;
    }

    public static boolean isDependantNoun(Pair<String, String> pair){
        if(pair.getSecond().equals("NNB")) return true;
        return false;
    }

    public static boolean isDerivable(Pair<String, String> pair){
        if(pair.getSecond().equals("XR") || pair.getSecond().equals("NNG")) return true;
        return false;
    }

    public static boolean isSpecialCharacter(Pair<String, String> pair){
        if(pair.getSecond().equals("SF") || pair.getSecond().equals("SW") || pair.getSecond().equals("SP") || pair.getSecond().equals("SE") || pair.getSecond().equals("SS") || pair.getSecond().equals("SO")) return true;
        return false;
    }

    public static boolean isSubjectivePost(Pair<String, String> pair){
        // TODO '도' 에 대한 문맥적 구분 필요
        if((pair.getSecond().equals("JKS") || pair.getSecond().equals("JX")) && !isEOS(pair) && !pair.getFirst().equals("도")) return true;
        return false;
    }

    public static boolean isPositiveDesignator(Pair<String, String> pair){
        if(pair.getSecond().equals(TAG_POSITIVE_DESIGNATOR)) return true;
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

    public static boolean isConcatenation(Pair<String, String> pair){
        if(pair.getFirst().equals(",") || pair.getSecond().equals("JC") || (pair.getFirst().equals("랑") && pair.getSecond().equals("JKB"))) return true;
        return false;
    }

    public static boolean isMetaQuestion(Pair<String, String> pair){
        if(pair.getSecond().equals("NP")){
            switch (pair.getFirst()){
                case "누구": case "어디": case "무엇": case "뭐": return true;
            }
        }else if(pair.getSecond().equals("NNG")){
            switch (pair.getFirst()){
                case "누구": case "어디": case "무엇": case "뭐": return true;
            }
        }
        return false;
    }

    public static boolean isQuestion(Pair<String, String> pair){
        if(pair.getSecond().equals("NP")){
            switch (pair.getFirst()){
                case "누구": case "어디": case "무엇": case "뭐": return true;
            }
        }
        return false;
    }

    public static double getEditDistanceRate(String str1, String str2, boolean eliminateMeaningless){
        String str1_m = str1.toString().trim();
        String str2_m = str2.toString().trim();
        if(eliminateMeaningless){
            str1_m = str1.replaceAll("ㅋ", "").trim();
            if(str1_m.length() == 0) str1_m = str1;
            str2_m = str2.replaceAll("ㅋ", "").trim();
            if(str2_m.length() == 0) str2_m = str2;
        }
        int length = str1_m.length();
        if(length < str2_m.length()) length = str2_m.length();
        return 1.0 - (double)getEditDistance(str1_m, str2_m)/(double)length;
    }

    public static int getEditDistance(String str1, String str2){
        int len1 = str1.length()+1;
        int len2 = str2.length()+1;
        char[] cstr1=str1.toCharArray();
        char[] cstr2=str2.toCharArray();

        int[][] matrix = new int[len1][len2];
        int isreplace = 0;

        for(int i=0;i<len1;i++) matrix[i][0] = i;
        for(int j=0;j<len2;j++) matrix[0][j] = j;

        for(int i = 1; i < len1; i++){
            for(int j = 1; j < len2; j++){
                if( str1.charAt(i - 1) == str2.charAt(j - 1)){
                    isreplace = 0;
                }else{
                    isreplace = 1;
                }
                matrix[i][j] = getMin(matrix[i-1][j] + 1, matrix[i][j-1] + 1, matrix[i-1][j-1] + isreplace);
            }
        }

        return matrix[len1-1][len2-1];
    }

    public static String eliminateMeaningLess(String str){
        String retVal = str.replaceAll("ㅋ", "").trim();
        return retVal;
    }

    private static int getMin(int a,int b, int c){
        int min = 99999; // 오차의 한계
        if(a < min) min = a;
        if(b < min) min = b;
        if(c < min) min = c;

        return min;
    }

    public static boolean isAfter(TypedPair pair){
        if((pair.getFirst().equals("뒤") || pair.getFirst().equals("후") || pair.getFirst().equals("이후")) && pair.getSecond().equals("NNG")) return true;
        return false;
    }

    public static boolean isBefore(TypedPair pair){
        if((pair.getFirst().equals("전") || pair.getFirst().equals("이전")) && (pair.getSecond().equals("NNG") || pair.getSecond().equals("MM"))) return true;
        return false;
    }

    public static long toNumber(String input) throws IndexOutOfBoundsException{
        long result = 0;
        long tmpResult = 0;
        long num = 0;
        final String NUMBER = "영일이삼사오육칠팔구";
        final String UNIT = "십백천만억조";
        final long[] UNIT_NUM = { 10, 100, 1000, 10000, (long)Math.pow(10,8), (long)Math.pow(10,12) };
        StringTokenizer st = new StringTokenizer(input, UNIT, true);
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            //숫자인지, 단위(UNIT)인지 확인
            int check = NUMBER.indexOf(token);
            if(check == -1) { //단위인 경우
                if("만억조".indexOf(token) == -1) {
                    tmpResult += (num != 0 ? num : 1) * UNIT_NUM[UNIT.indexOf(token)];
                } else {
                    tmpResult += num;
                    result += (tmpResult != 0 ? tmpResult : 1) * UNIT_NUM[UNIT.indexOf(token)];
                    tmpResult = 0;
                }
                num = 0;
            } else { //숫자인 경우
                num = check;
            }
        }

        return result + tmpResult + num;
    }

}
