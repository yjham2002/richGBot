package react;

import analysis.ITrigger;
import util.ConfirmReport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

        ClosedPurpose cp = new ClosedPurpose(4,  "REPORT");
        cp.init("", "(이)가 누락되었습니다.","완료되었습니다." ,  "오늘도 수고하셨어용ㅎㅎ 이름이 뭐예용 ㅇㅅㅇ??!", "주간 업무를 말하세요.", "일일업무를 말하세요", "보고서를 발급하시겠습니까?");
        cp.initExtra("NAME", "WEEK", "DAY", "DONE");
        cp.initModeSet(ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_ASK_IF);
        cp.initRun(new ITrigger() {
            @Override
            public boolean run(HashMap<String, Object> extra, List<String> ref) {
                System.out.println("보고서 파일 저장");
                ref.add("보고서는 아직 파일 양식이 없네여~~ 작성 취소할게여 ㅋ");
                return false;
            }
        });

        ClosedPurpose cp1 = new ClosedPurpose(1,  "EXIT");
        cp1.init("", "","ㅇㅅㅇ" ,  "진짜루요...? ㅠㅠㅠㅠㅠㅠㅠ");
        cp1.initExtra("DONE");
        cp1.initModeSet(ClosedPurpose.MODE_ASK_IF);
        cp1.initRun(new ITrigger() {
            @Override
            public boolean run(HashMap<String, Object> extra, List<String> ref) {
                System.exit(0);
                return false;
            }
        });

        ClosedPurpose cp2 = new ClosedPurpose(4,  "CONFIRM");
        cp2.init("", "(이)가 누락되었습니다.","완료되었습니다." ,  "또 뭘 잘못한거예요!! 이름이나 말해요 ㅡㅡ", "사유를 간결하게 입력하세요(ex : 지각 / 결근 등)", "내용을 입력하세요.", "보고서를 발급하시겠습니까?");
        cp2.initExtra("NAME", "REASON", "CONTENT", "DONE");
        cp2.initModeSet(ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_ASK_IF);
        cp2.initRun(new ITrigger() {
            @Override
            public boolean run(HashMap<String, Object> extra,  List<String> ref) {
                ConfirmReport confirmReport = new ConfirmReport(extra.get("NAME").toString(), extra.get("REASON").toString(), extra.get("CONTENT").toString());
                String path = confirmReport.writeFile();
                System.out.println("시말서 파일 저장 :: " + path);

                String relative = "http://192.168.0.38:8171/" + path;

                ref.add(relative);

                return false;
            }
        });

        ClosedPurpose cp3 = new ClosedPurpose(4,  "CONFIRM");
        cp2.init("", "(이)가 누락되었습니다.","완료되었습니다." ,  "또 뭘 잘못한거예요!! 이름이나 말해요 ㅡㅡ", "사유를 간결하게 입력하세요(ex : 지각 / 결근 등)", "내용을 입력하세요.", "보고서를 발급하시겠습니까?");
        cp2.initExtra("NAME", "REASON", "CONTENT", "DONE");
        cp2.initModeSet(ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_NO_MATTER, ClosedPurpose.MODE_ASK_IF);
        cp2.initRun(new ITrigger() {
            @Override
            public boolean run(HashMap<String, Object> extra,  List<String> ref) {
                ConfirmReport confirmReport = new ConfirmReport(extra.get("NAME").toString(), extra.get("REASON").toString(), extra.get("CONTENT").toString());
                String path = confirmReport.writeFile();
                System.out.println("시말서 파일 저장 :: " + path);

                String relative = "http://192.168.0.38:8171/" + path;

                ref.add(relative);

                return false;
            }
        });

        instance.put(cp);
        instance.put(cp1);
        instance.put(cp2);

    }

}
