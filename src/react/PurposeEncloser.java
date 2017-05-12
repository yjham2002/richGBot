package react;

import analysis.ITrigger;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by a on 2017-05-11.
 */
public class PurposeEncloser extends HashMap<String, ClosedPurpose> {
    private static PurposeEncloser instance;

    public static void start(){
        if(instance == null) instance = new PurposeEncloser();
        init();
    }

    private PurposeEncloser(){
    }

    public static ClosedPurpose getPurpose(String intentionCode){
        return instance.get(intentionCode);
    }

    public static boolean containsCode(String intentionCode){
        return instance.containsKey(intentionCode);
    }

    public static HashSet<String> getFilter(String intentionCode, int level){
        if(level < 0) return null;
        if(instance.containsKey(intentionCode)) return instance.get(intentionCode).getModeAppliedFIlter(level);
        else return null;
    }

    public static void init(){
        System.out.println("NLP Engine :: Initializing Purpose Encloser");
        setPurpose();
    }

    public static ClosedPurpose put(ClosedPurpose cp){
        return instance.put(cp.getIntentionCode(), cp);
    }

    public static void flush(){
        setPurpose();
    }

    public static void setPurpose(){

        ClosedPurpose cp = new ClosedPurpose(4, 0, "REPORT");
        cp.init("", "(이)가 누락되었습니다.","완료되었습니다." ,  "오늘도 수고하셨어용ㅎㅎ 이름이 뭐예용 ㅇㅅㅇ??!", "주간 업무를 말하세요.", "일일업무를 말하세요", "보고서를 발급하시겠습니까?");
        cp.initExtra("NAME", "WEEK", "DAY", "DONE");
        cp.initModeSet(ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_ASK_IF);
        cp.initRun(new ITrigger() {
            @Override
            public boolean run() {
                System.out.println("보고서 파일 저장");
                return false;
            }
        });

        ClosedPurpose cp2 = new ClosedPurpose(4, 0, "CONFIRM");
        cp2.init("", "(이)가 누락되었습니다.","완료되었습니다." ,  "또 뭘 잘못한거예요!! 이름이나 말해요 ㅡㅡ", "사유를 간결하게 입력하세요(ex : 지각 / 결근 등)", "내용을 입력하세요.", "보고서를 발급하시겠습니까?");
        cp2.initExtra("NAME", "REASON", "CONTENT", "DONE");
        cp2.initModeSet(ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_ASK_IF);
        cp2.initRun(new ITrigger() {
            @Override
            public boolean run() {
                System.out.println("시말서 파일 저장");
                return false;
            }
        });

        instance.put(cp);
        instance.put(cp2);

    }

}
