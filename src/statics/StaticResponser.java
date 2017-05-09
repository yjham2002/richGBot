package statics;

import DB.DBManager;
import util.WeatherParser;

import java.util.Date;
import java.util.Random;

/**
 * Created by a on 2017-04-13.
 */
public class StaticResponser {

    private DBManager dbManager;

    public static final String INTENT_TIME = "TIME";
    public static final String INTENT_HELLO = "HELLO";
    public static final String INTENT_CALL = "CALL";
    public static final String INTENT_DOING = "DOING";
    public static final String INTENT_BAD = "BAD";
    public static final String INTENT_GOOD = "GOOD";
    public static final String INTENT_WEATHER = "WEATHER";
    public static final String INTENT_NOTHING = "NOTHING";
    public static final String INTENT_CHEER = "CHEER";
    public static final String INTENT_OBSCURE = "OBSCURE";
    public static final String INTENT_CAPACITY = "CAPACITY";
    public static final String INTENT_INTRODUCE = "INTRODUCE";
    public static final String INTENT_BORED = "BORED";
    public static final String INTENT_LAUGH = "LAUGH";
    public static final String INTENT_FORTUNE = "FORTUNE";
    public static final String INTENT_HELP = "HELP";
    public static final String INTENT_DIRECT = "DIRECT";

    public static final String INTENT_REDUNDANT = "REDUNDANT";

    public StaticResponser(DBManager dbManager){
        this.dbManager = dbManager;
    }

    public String talk(String intent, String msg){
        String response = "";
        switch (intent){
            case INTENT_DIRECT: response = dbManager.getDirectResponse(msg); break;
            case INTENT_REDUNDANT : response = "동일 어휘 반복 입력"; break;
            case INTENT_TIME: response = "현재 시각은 [" + new Date() + "] 입니다."; break;
            case INTENT_HELLO: response = ResponseConstant.HELLO[new Random().nextInt(ResponseConstant.HELLO.length)]; break;
            case INTENT_CALL: response = ResponseConstant.CALL[new Random().nextInt(ResponseConstant.CALL.length)]; break;
            case INTENT_DOING: response = ResponseConstant.DOING[new Random().nextInt(ResponseConstant.DOING.length)]; break;
            case INTENT_BAD: response = ResponseConstant.BAD[new Random().nextInt(ResponseConstant.BAD.length)]; break;
            case INTENT_GOOD: response = ResponseConstant.GOOD[new Random().nextInt(ResponseConstant.GOOD.length)]; break;
            case INTENT_NOTHING: response = ResponseConstant.NOTHING[new Random().nextInt(ResponseConstant.NOTHING.length)]; break;
            case INTENT_WEATHER: response = "날씨 서버와의 연결이 원활하지 않습니다."; break; //WeatherParser.getWeather("서울"); break;
            case INTENT_CHEER: response = ResponseConstant.CHEER[new Random().nextInt(ResponseConstant.CHEER.length)]; break;
            case INTENT_OBSCURE: response = ResponseConstant.OBSCURE[new Random().nextInt(ResponseConstant.OBSCURE.length)]; break;
            case INTENT_CAPACITY: response = ResponseConstant.CAPACITY[new Random().nextInt(ResponseConstant.CAPACITY.length)]; break;
            case INTENT_INTRODUCE: response = ResponseConstant.INTRODUCE[new Random().nextInt(ResponseConstant.INTRODUCE.length)]; break;
            case INTENT_BORED: response = ResponseConstant.BORED[new Random().nextInt(ResponseConstant.BORED.length)]; break;
            case INTENT_LAUGH: response = ResponseConstant.LAUGH[new Random().nextInt(ResponseConstant.LAUGH.length)]; break;
            case INTENT_FORTUNE: response = ResponseConstant.FORTUNE[new Random().nextInt(ResponseConstant.FORTUNE.length)]; break;
            case INTENT_HELP: response = ResponseConstant.HELP[new Random().nextInt(ResponseConstant.HELP.length)]; break;
            default : break;
        }

        return response;
    }
}
