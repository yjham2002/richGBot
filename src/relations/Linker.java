package relations;

import kr.co.shineware.util.common.model.Pair;
import util.KoreanUtil;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class Linker {

    public static List<String> memory;

    private static final int MEMORY_SIZE = 100;

    private static final Set<String> SUBJECTS = new HashSet<>();
    private static final Set<String> VERBS = new HashSet<>();
    private static final Set<String> REQUESTS = new HashSet<>();

    private static final String PATTERN_VERB = "NNGXSV";

    private List<List<Pair<String, String>>> morphemes;
    private List<Pair<String, String>> linear;
    private List<List<Pair<String, String>>> proc;
    private Set<String> base;

    private String temporaryMemory = "";

    public Linker(List<List<Pair<String, String>>> morphemes){
        this.morphemes = morphemes;
        init();
    }

    public Linker(){
        init();
    }

    public void setMorphemes(List<List<Pair<String, String>>> morphemes, String origin){
        this.morphemes = morphemes;
        temporaryMemory = origin;
    }

    public void init(){
        memory = new ArrayList<>();
        this.linear = new ArrayList<>();
        this.proc = new ArrayList<>();
        this.base = new HashSet<>();

        if(this.morphemes != null) {
            for (List<Pair<String, String>> eojeolResult : morphemes)
                for (Pair<String, String> wordMorph : eojeolResult) linear.add(wordMorph);
        }

        for(String s : new String[]{"NP", "NN", "NNG", "NNP"}) SUBJECTS.add(s);
        for(String s : new String[]{"VA", "VV"}) VERBS.add(s);
        for(String s : new String[]{"VV"}) REQUESTS.add(s);
    }

    private void link(){
        if(this.morphemes == null){
            System.out.println("There is no morpheme data.");
        }

        String prev = "";
        if(memory.size() >= 1) prev = memory.get(memory.size() - 1);

        memory.add(temporaryMemory);

        if(prev.equals(temporaryMemory) && memory.size() >= 1){
            System.out.println("GBOT : 같은 말을 두 번하지 않아도 다 알아듣고 있어요!!!");
            return;
        }

        if(memory.size() > MEMORY_SIZE) {
            memory.clear();
            memory.add(prev);
        }

        Stack<Pair<String, String>> stack = new Stack<>();
        int currentCursor = 0;
        do {
            for (List<Pair<String, String>> eojeolResult : this.morphemes) {
                for (Pair<String, String> wordMorph : eojeolResult) {
                    currentCursor++;
                    Pair<String, String> pending = null;
                    if (SUBJECTS.contains(wordMorph.getSecond())) {
                        if(currentCursor < eojeolResult.size()){
                            stack.push(wordMorph);
                        }else{
                            if(wordMorph.getFirst().equals("내") && wordMorph.getSecond().equals("NP")) wordMorph.setFirst("나");
                            stack.push(wordMorph);
                        }

                    }else if(VERBS.contains(wordMorph.getSecond())){
                        if(!stack.empty()) {
                            addProcData(stack.pop(), wordMorph);
                        }
                    }
                }
            }
        }while(currentCursor < linear.size());
        while(!stack.empty()) {
            System.out.println("[DEBUG] :: " + KoreanUtil.getComleteWordByJongsung(stack.pop().getFirst(), "이", "가") + " 검출되었으나 동사와 매칭되지 않음");
        }
    }

    public void addProcData(Pair<String, String> pop, Pair<String, String> word){
        List<Pair<String, String>> temp = new ArrayList<>();
        temp.add(pop);
        temp.add(word);
        if(REQUESTS.contains(word.getSecond()) && word.getFirst().equals("하")){
            System.out.println(pop.getFirst() + " 서비스를 호출합니다.");
        }else{
            learnLinkPair(temp);
        }
    }

    public String generateHash(List<Pair<String, String>> know){
        String hash = (know.get(0) + "#_HASH_#" + know.get(1)).replaceAll(" ", "#");
        return hash;
    }

    public boolean isHaving(List<Pair<String, String>> know){
        return base.contains(generateHash(know));
    }

    public void learnLinkPair(List<Pair<String, String>> know){
        if(isHaving(know)){
            String concat = "는";
            if(know.get(1).getSecond().equals("VA")) concat = "은";
            System.out.println("GBOT : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는")+ " " + know.get(1).getFirst() + concat + " 것이라고 이미 알고 있다구요!!!!!");
        }else{
            String concat = "는";
            if(know.get(1).getSecond().equals("VA")) concat = "은";
            System.out.println("GBOT : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는")+ " " + know.get(1).getFirst() + concat + " 것이라고 기억해둘게요.");
            proc.add(know);
            base.add(generateHash(know));
        }

    }

    public void printResult(){
        link();
    }


}
