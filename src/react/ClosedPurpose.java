package react;

import analysis.ITrigger;
import analysis.SpeechActAnalyser;
import exceptions.PurposeSizeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by a on 2017-05-09.
 */
public class ClosedPurpose {

    public static final int MODE_NONE = -1;
    public static final int MODE_REQUEST_BULK = 10;
    public static final int MODE_REQUEST_ONLY = 20;
    public static final int MODE_ASK_IF = 30;
    public static final int MODE_ASK_TIME = 40;
    public static final int MODE_NO_MATTER = 50;

    public static HashMap<Integer, HashSet<String>> filterMap;

    private int maximum;
    private int current = 0;
    private HashMap<String, Object> extra;
    private String intentionCode;
    private List<String> levelMessages;
    private List<Integer> modeList;
    private String header;
    private String footer;
    private List<String> mapper;
    private ITrigger callback;

    public ClosedPurpose(int maximum, int current, String intentionCode){
        this.extra = new HashMap<>();
        this.maximum = maximum;
        this.current = current;
        this.intentionCode = intentionCode;

        if(filterMap == null) initModes();
    }

    public static void initModes(){
        filterMap = new HashMap<>();
        HashSet<String> MODE_NONE_SET = new HashSet<>();
        HashSet<String> MODE_REQUEST_BULK_SET = new HashSet<>();
        HashSet<String> MODE_REQUEST_ONLY_SET = new HashSet<>();
        HashSet<String> MODE_ASK_IF_SET = new HashSet<>();
        HashSet<String> MODE_ASK_TIME_SET = new HashSet<>();
        HashSet<String> MODE_NO_MATTER_SET = new HashSet<>();

        String[] MODE_NONE_INIT = new String[]{};
        String[] MODE_REQUEST_BULK_INIT = new String[]{SpeechActAnalyser.SPEECH_ACT_UNDEFINED, SpeechActAnalyser.SPEECH_ACT_FACT, SpeechActAnalyser.SPEECH_ACT_INFORM, SpeechActAnalyser.SPEECH_ACT_RESPONSE};
        String[] MODE_REQUEST_ONLY_INIT = new String[]{SpeechActAnalyser.SPEECH_ACT_UNDEFINED, SpeechActAnalyser.SPEECH_ACT_FACT, SpeechActAnalyser.SPEECH_ACT_INFORM, SpeechActAnalyser.SPEECH_ACT_RESPONSE};
        String[] MODE_ASK_IF_INIT = new String[]{SpeechActAnalyser.SPEECH_ACT_ACCEPT, SpeechActAnalyser.SPEECH_ACT_REJECT};
        String[] MODE_ASK_INIT = new String[]{SpeechActAnalyser.SPEECH_ACT_UNDEFINED, SpeechActAnalyser.SPEECH_ACT_FACT, SpeechActAnalyser.SPEECH_ACT_INFORM, SpeechActAnalyser.SPEECH_ACT_RESPONSE};
        String[] MODE_NO_MATTER_INIT = new String[]{
                SpeechActAnalyser.SPEECH_ACT_UNDEFINED,
                SpeechActAnalyser.SPEECH_ACT_FACT,
                SpeechActAnalyser.SPEECH_ACT_INFORM,
                SpeechActAnalyser.SPEECH_ACT_RESPONSE,
//                SpeechActAnalyser.SPEECH_ACT_ACCEPT,
//                SpeechActAnalyser.SPEECH_ACT_REJECT,
                SpeechActAnalyser.SPEECH_ACT_ASK_IF,
                SpeechActAnalyser.SPEECH_ACT_ASK_REF,
                SpeechActAnalyser.SPEECH_ACT_REQUEST_ACT,
                SpeechActAnalyser.SPEECH_ACT_CORRECT,
                SpeechActAnalyser.SPEECH_ACT_CONFIRM
        };

        for(String s : MODE_NONE_INIT) MODE_NONE_SET.add(s);
        for(String s : MODE_REQUEST_BULK_INIT) MODE_REQUEST_BULK_SET.add(s);
        for(String s : MODE_REQUEST_ONLY_INIT) MODE_REQUEST_ONLY_SET.add(s);
        for(String s : MODE_ASK_IF_INIT) MODE_ASK_IF_SET.add(s);
        for(String s : MODE_ASK_INIT) MODE_ASK_TIME_SET.add(s);
        for(String s : MODE_NO_MATTER_INIT) MODE_NO_MATTER_SET.add(s);

        filterMap.put(MODE_NONE, MODE_NONE_SET);
        filterMap.put(MODE_REQUEST_BULK, MODE_REQUEST_BULK_SET);
        filterMap.put(MODE_REQUEST_ONLY, MODE_REQUEST_ONLY_SET);
        filterMap.put(MODE_ASK_IF, MODE_ASK_IF_SET);
        filterMap.put(MODE_ASK_TIME, MODE_ASK_TIME_SET);
        filterMap.put(MODE_NO_MATTER, MODE_NO_MATTER_SET);

    }

    public HashSet<String> getModeAppliedFIlter(int level){
        return filterMap.get(modeList.get(level));
    }

    /***
     * 응답 메시지를 지정한다.
     * 아래 요구 메시지 헤더와 풋터는 "말씀하신 정보 중 " <= Header / ..... / " 이 누락되었습니다." <= Footer
     * @param reqMessageHeader EX)
     * @param reqMessageFooter
     * @param messages
     */
    public void init(String reqMessageHeader, String reqMessageFooter, String endMessage, String... messages){
        this.header = reqMessageHeader;
        this.footer = reqMessageFooter;
        this.levelMessages = new ArrayList<>();
        for(String msg : messages){
            this.levelMessages.add(msg);
        }
        this.levelMessages.add(endMessage);
    }

    public void initRun(ITrigger todo){
        callback = todo;
    }

    public void initExtra(String... messages){
        this.mapper = new ArrayList<>();
        this.mapper.add("START_OF_PURPOSE");
        for(String msg : messages){
            this.mapper.add(msg);
        }
    }

    public void initModeSet(Integer... integers){
        this.modeList = new ArrayList<>();
        for(Integer i : integers) this.modeList.add(i);
    }

    public void run() throws  PurposeSizeException{
        if(modeList.size() + 1 != mapper.size() || levelMessages.size() != mapper.size()) throw new PurposeSizeException();
        if(callback != null) callback.run(extra);
    }

    public String getMessage(int level, boolean classify) throws NullPointerException, IndexOutOfBoundsException {

        if(classify) {
            if (modeList.get(level) == MODE_REQUEST_BULK) {
                String msg = this.header.trim() + " ";
                for (String key : extra.keySet()) {
                    msg += key + " ";
                }
                msg += this.footer.trim();

                return msg;
            }
        }
        return levelMessages.get(level);
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public HashMap<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, Object> extra) {
        this.extra = extra;
    }

    public String getIntentionCode() {
        return intentionCode;
    }

    public void setIntentionCode(String intentionCode) {
        this.intentionCode = intentionCode;
    }

    public List<String> getLevelMessages() {
        return levelMessages;
    }

    public void setLevelMessages(List<String> levelMessages) {
        this.levelMessages = levelMessages;
    }

    public List<Integer> getModeList() {
        return modeList;
    }

    public void setModeList(List<Integer> modeList) {
        this.modeList = modeList;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public List<String> getMapper() {
        return mapper;
    }

    public void setMapper(List<String> mapper) {
        this.mapper = mapper;
    }
}
