package statics;

import java.util.Date;

/**
 * Created by a on 2017-04-13.
 */
public class StaticResponser {
    public static final String INTENT_TIME = "TIME";

    public static String talk(String intent){
        String response = "";
        switch (intent){
            case "TIME": response = "현재 시각은 [" + new Date() + "] 입니다."; break;
            default : break;
        }

        return response;
    }
}
