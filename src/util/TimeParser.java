package util;

import DB.DBManager;
import kr.co.shineware.util.common.model.Pair;
import relations.TypedPair;

import java.sql.Time;
import java.util.*;

/**
 * Created by a on 2017-04-18.
 */
public class TimeParser extends ArrayList<Pair<Pair<Integer, Integer>, Date>> {

    private DBManager dbManager;
    private static HashMap<String, TimeUnit> TIME_DICTIONARY;
    private static HashMap<String, NumberUnit> NUMBER_DICTIONARY;

    private static final String POS_NUMBER = "SN";

    public TimeParser(DBManager dbManager){
        super();
        this.dbManager = dbManager;
        init();
    }

    private boolean containsAndEqualWithNumber(TypedPair pair){
        if(isNumeric(pair.getFirst()) && pair.getSecond().equals(POS_NUMBER)) return true;
        if(NUMBER_DICTIONARY.containsKey(pair.getFirst())){
            if(NUMBER_DICTIONARY.get(pair.getFirst()).getTag().equals(pair.getSecond())) return true;
        }else{
            try {
                KoreanUtil.toNumber(pair.getFirst());
                return true;
            }catch (IndexOutOfBoundsException e){
                return false;
            }
        }
        return false;
    }

    private boolean isStandalone(TypedPair pair){
        if(TIME_DICTIONARY.containsKey(pair.getFirst())){
            if(TIME_DICTIONARY.get(pair.getFirst()).isStandalone()) return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private boolean containsAndEqualWithTime(TypedPair pair){
        if(TIME_DICTIONARY.containsKey(pair.getFirst())){
            return true;
        }
        return false;
    }

    public List<TimeExpression> parse(List<TypedPair> pairs){

        List<TimeExpression> list = new ArrayList<>();
        List<Pair<Integer, Integer>> history = new ArrayList<>();

        boolean flag = false;
        TimeExpression timeExpression = null;
        String express = "";

        for(int i = 0; i < pairs.size(); i++){
            TypedPair pair = pairs.get(i);
            if(containsAndEqualWithTime(pair)){
                if((i > 0 && containsAndEqualWithNumber(pairs.get(i - 1)))) {
                    int value;
                    if(isNumeric(pairs.get(i - 1).getFirst())){
                        value = Integer.parseInt(pairs.get(i - 1).getFirst());
                    }else if(NUMBER_DICTIONARY.containsKey(pairs.get(i - 1))){
                        value = NUMBER_DICTIONARY.get(pairs.get(i - 1).getFirst()).getValue();
                    }else{
                        value = (int)KoreanUtil.toNumber(pairs.get(i - 1).getFirst());
                    }
                    int unit = TimeUnit.toCalendarType(TIME_DICTIONARY.get(pair.getFirst()).getMeaning());

                    boolean isRelative = false;

                    if(i + 1 < pairs.size() && (KoreanUtil.isAfter(pairs.get(i + 1)) || KoreanUtil.isBefore(pairs.get(i + 1)))) isRelative = true;

                    int beforeAfter = 1;
                    if((i + 1 < pairs.size() && KoreanUtil.isBefore(pairs.get(i + 1)))) beforeAfter = -1;

                    if(!flag) {
                        flag = true;
                        timeExpression = new TimeExpression();
                        timeExpression.setStart(i - 1);
                        history.clear();

                        if (unit != -1) {
                            if(unit == Calendar.MONTH) value -= 1;
                            if(!isRelative) {
                                timeExpression.getDate().set(unit, value);
                                history.add(new Pair<>(unit, value));
                                express += (unit == Calendar.MONTH ? value + 1 : value) + pair.getFirst() + " ";
                            }else{
                                timeExpression.getDate().set(unit, value);
                                history.add(new Pair<>(unit, value));
                                express += (unit == Calendar.MONTH ? value + 1 : value) + pair.getFirst() + " ";

                                Calendar cal = Calendar.getInstance();
                                for(Pair<Integer, Integer> tpair : history){
                                    int newVal = tpair.getSecond();
                                    if(tpair.getFirst() == Calendar.MONTH) newVal = newVal + 1;
                                    cal.add(tpair.getFirst(), beforeAfter * newVal);
                                }
                                timeExpression.setDate(cal);

                                if(beforeAfter == 1) express += "후 ";
                                else express += "전 ";
                            }
                        }
                    }else{
                        if(unit != -1) {
                            if(unit == Calendar.MONTH) value -= 1;
                            if(!isRelative) {
                                timeExpression.getDate().set(unit, value);
                                history.add(new Pair<>(unit, value));
                                express += (unit == Calendar.MONTH ? value + 1 : value) + pair.getFirst() + " ";
                            }else{
                                timeExpression.getDate().set(unit, value);
                                history.add(new Pair<>(unit, value));
                                express += (unit == Calendar.MONTH ? value + 1 : value) + pair.getFirst() + " ";

                                Calendar cal = Calendar.getInstance();
                                for(Pair<Integer, Integer> tpair : history) {
                                    int newVal = tpair.getSecond();
                                    if(tpair.getFirst() == Calendar.MONTH) newVal = newVal + 1;
                                    cal.add(tpair.getFirst(), beforeAfter * newVal);
                                }
                                timeExpression.setDate(cal);

                                if(beforeAfter == 1) express += "후 ";
                                else express += "전 ";
                            }
                        }
                    }
                }
                else if(isStandalone(pair)) {
                    int unit = TimeUnit.toCalendarType(TIME_DICTIONARY.get(pair.getFirst()).getMeaning());
                    int value = TIME_DICTIONARY.get(pair.getFirst()).getDiff();

                    timeExpression = new TimeExpression();
                    timeExpression.setStart(i);
                    timeExpression.setEnd(i);
                    timeExpression.setExpression(pair.getFirst());

                    if(unit == Calendar.DAY_OF_WEEK){
                        timeExpression.getDate().set(unit, value);
                    }else{
                        Calendar cal = Calendar.getInstance();
                        cal.add(unit, value);
                        timeExpression.setDate(cal);
                    }
                    list.add(timeExpression);
                }
            }else if(containsAndEqualWithNumber(pair)){
                // DO NOTHING
            }else{
                if(flag) {
                    timeExpression.setExpression(express.trim());
                    timeExpression.setEnd(i);
                    list.add(timeExpression);
                    flag = false;
                }
            }

            if(i + 1 >= pairs.size() && flag){
                timeExpression.setExpression(express.trim());
                timeExpression.setEnd(i);
                list.add(timeExpression);
                flag = false;
            }
        }

        return list;
    }

    private void init(){
        if(TIME_DICTIONARY == null){
            System.out.println("[INFO :: TimeUnit Dictionary is loading onto cache.]");
            TIME_DICTIONARY = new HashMap<>();
            List<TimeUnit> timeList = dbManager.getTimeDictionary();
            for(TimeUnit unit : timeList) TIME_DICTIONARY.put(unit.getDesc(), unit);
        }
        if(NUMBER_DICTIONARY == null){
            System.out.println("[INFO :: NumberUnit Dictionary is loading onto cache.]");
            NUMBER_DICTIONARY = new HashMap<>();
            List<NumberUnit> numList = dbManager.getNumberDictionary();
            for(NumberUnit unit : numList) NUMBER_DICTIONARY.put(unit.getDesc(), unit);
        }

    }

}
