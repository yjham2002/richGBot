package statics;

import java.util.Date;
import java.util.Random;

/**
 * Created by a on 2017-04-13.
 */
public class StaticResponser {
    public static final String INTENT_TIME = "TIME";
    public static final String INTENT_HELLO = "HELLO";
    public static final String INTENT_CALL = "CALL";
    public static final String INTENT_DOING = "DOING";
    public static final String INTENT_BAD = "BAD";
    public static final String INTENT_GOOD = "GOOD";

    public static String talk(String intent){
        String response = "";
        switch (intent){
            case INTENT_TIME: response = "현재 시각은 [" + new Date() + "] 입니다."; break;
            case INTENT_HELLO: response = ResponseConstant.HELLO[new Random().nextInt(ResponseConstant.HELLO.length)]; break;
            case INTENT_CALL: response = ResponseConstant.CALL[new Random().nextInt(ResponseConstant.CALL.length)]; break;
            case INTENT_DOING: response = ResponseConstant.DOING[new Random().nextInt(ResponseConstant.DOING.length)]; break;
            case INTENT_BAD: response = ResponseConstant.BAD[new Random().nextInt(ResponseConstant.BAD.length)]; break;
            case INTENT_GOOD: response = ResponseConstant.GOOD[new Random().nextInt(ResponseConstant.GOOD.length)]; break;
            default : break;
        }

        return response;
    }
}
