package relations;

import DB.DBManager;
import kr.co.shineware.util.common.model.Pair;
import util.KoreanUtil;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class Linker {

    public static List<String> memory;

    public static final String MY_NAME = "RES";

    private static final int MEMORY_SIZE = 100;

    private static final int SENTENCE_ORDER = 10;
    private static final int SENTENCE_PLAIN = 20;
    private static final int SENTENCE_QUESTION = 30;

    private static final Set<String> SUBJECTS = new HashSet<>();
    private static final Set<String> GENERAL_NOUN = new HashSet<>();
    private static final Set<String> DEPNOUN = new HashSet<>();
    private static final Set<String> VERBS = new HashSet<>();
    private static final Set<String> REQUESTS = new HashSet<>();
    private static final Set<String> OBJECTS= new HashSet<>();
    private static final Set<String> ADJECTIVES= new HashSet<>();
    private static final Set<String> ADVERBS = new HashSet<>();
    private static final Set<String> DETERMINERS = new HashSet<>();
    private static final Set<String> BASES = new HashSet<>();

    private static final String PATTERN_VERB = "NNGXSV";

    private List<List<Pair<String, String>>> morphemes;
    private KnowledgeBase base;
    private DBManager dbManager;

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

    private void init(){
        dbManager = new DBManager();
        memory = new ArrayList<>();
        this.base = new KnowledgeBase(dbManager);

        for(String s : new String[]{"NP", "NN", "NNG", "NNP"}) SUBJECTS.add(s);
        for(String s : new String[]{"NNB"}) DEPNOUN.add(s);
        for(String s : new String[]{"VV"}) VERBS.add(s);
        for(String s : new String[]{"VA"}) ADJECTIVES.add(s);
        for(String s : new String[]{"VV"}) REQUESTS.add(s);
        for(String s : new String[]{"MM"}) DETERMINERS.add(s);
        for(String s : new String[]{"XR"}) BASES.add(s);
        for(String s : new String[]{"SL", "SH", "SW", "NF", "SN", "NA"}) GENERAL_NOUN.add(s);
        GENERAL_NOUN.addAll(SUBJECTS);
    }

    private List<TypedPair> groupingPhrase(List<TypedPair> cores){
        List<TypedPair> retVal = new ArrayList<>();

        return retVal;
    }

    private List<TypedPair> shortenNounNounPhrase(List<TypedPair> cores){
        List<TypedPair> retVal = new ArrayList<>();

        boolean flag = false;

        TypedPair entry;
        String newFirst = "";

        for(int i = 0; i < cores.size(); i++){
            TypedPair pair = cores.get(i);
            if(GENERAL_NOUN.contains(pair.getSecond())){
                if(flag){
                    if(KoreanUtil.isDerivable(cores.get(i))) {
                        if (cores.size() > i + 1 && KoreanUtil.isDeriver(cores.get(i + 1))) { // 파생접미사 처리
                            TypedPair typedPair = new TypedPair();
                            typedPair.setFirst(cores.get(i).getFirst() + cores.get(i + 1).getFirst());
                            if (KoreanUtil.isVerbalDeriver(cores.get(i + 1)) || KoreanUtil.isAdjectiveDeriver(cores.get(i + 1))) {
                                entry = new TypedPair();
                                entry.setFirst(newFirst);
                                entry.setSecond("NNG");
                                retVal.add(entry);

                                retVal.add(pair);
                                flag = false;
                            }
                        }
                    }
                    if(flag) newFirst = newFirst + " " + pair.getFirst();

                }else{
                    newFirst = pair.getFirst();
                    flag = true;
                }
            }else{
                if(flag){
                    entry = new TypedPair();
                    entry.setFirst(newFirst);
                    entry.setSecond("NNG");
                    retVal.add(entry);
                    flag = false;
                }
                retVal.add(pair);
            }
        }

        return retVal;
    }

    private Arc getLinkedArc(List<TypedPair> cores){

        List<Integer> vIdx = new ArrayList<>();
        List<Integer> adjIdx = new ArrayList<>();
        List<Integer> oIdx = new ArrayList<>();
        List<Integer> sIdx = new ArrayList<>();
        List<Integer> aIdx = new ArrayList<>();
        List<Integer> soIdx = new ArrayList<>();

        Arc retVal = new Arc(cores);

        int sD = cores.size();

        for(int i = 0; i < cores.size(); i++) {

            Pair<String, String> pair = cores.get(i);

            if(VERBS.contains(pair.getSecond()) || ADJECTIVES.contains(pair.getSecond())) { // 동사 혹은 형용사로 현재 페어가 입력될 수 있는 경우
                if(cores.size() > i + 1 && KoreanUtil.isDeterminingHead(cores.get(i + 1)) && ADJECTIVES.contains(pair.getSecond())) {
                    cores.get(i).setType(TypedPair.TYPE_ADJ);
                    adjIdx.add(i);
                }else if(cores.size() > i + 1 && KoreanUtil.isDeterminingHead(cores.get(i + 1)) && VERBS.contains(pair.getSecond())){
                    cores.get(i).setType(TypedPair.TYPE_VADJ);
                    adjIdx.add(i);
                }else {
                    if (cores.size() > i + 1 && KoreanUtil.isConcatHead(cores.get(i + 1))) {
                        cores.get(i).setType(TypedPair.TYPE_ADV);
                        aIdx.add(i);
                    } else {
                        cores.get(i).setType(TypedPair.TYPE_VERB);
                        vIdx.add(i);
                    }
                }
            }else if(SUBJECTS.contains(pair.getSecond()) && !(cores.size() > i + 1 && KoreanUtil.isDeriver(cores.get(i + 1)))){ // 주어 혹은 목적어로 현재 페어가 입력될 수 있는 경우
                if(KoreanUtil.isQuestion(cores.get(i))){
                    cores.get(i).setType(TypedPair.TYPE_QUESTION);
                    sIdx.add(i);
                }else {
                    if (cores.size() > i + 1 && KoreanUtil.isSubjectivePost(cores.get(i + 1))) { // 다음 페어가 주격조사인 경우
                        cores.get(i).setType(TypedPair.TYPE_SUBJECT);
                        sIdx.add(i);
                    } else {
                        cores.get(i).setType(TypedPair.TYPE_OBJECT); // 다음 페어에 주격조사가 아닌 경우 목적어로 간주
                        oIdx.add(i);
                    }
                }
            }else{ // 예외 상황 처리
                if(KoreanUtil.isDerivable(cores.get(i))) {
                    if (cores.size() > i + 1 && KoreanUtil.isDeriver(cores.get(i + 1))) { // 파생접미사 처리
                        TypedPair typedPair = new TypedPair();
                        typedPair.setFirst(cores.get(i).getFirst() + cores.get(i + 1).getFirst());
                        if (KoreanUtil.isVerbalDeriver(cores.get(i + 1))) {
                            typedPair.setSecond("VV");
                            cores.remove(i);
                            cores.remove(i);
                            cores.add(i, typedPair);
                            i--;
                        } else if (KoreanUtil.isAdjectiveDeriver(cores.get(i + 1))) {
                            typedPair.setSecond("VA");
                            cores.remove(i);
                            cores.remove(i);
                            cores.add(i, typedPair);
                            i--;
                        }
                    }
                }
            }
        }

        // 형용사 기준 링킹

        for(int k = 0; k < adjIdx.size(); k++){

            double weight = 0;
            int candidate = -1;
            int dependantNoun = -1;

            Pair<String, String> adj = cores.get(adjIdx.get(k));

            // 형용사와 명사 연결
            soIdx.addAll(sIdx);
            soIdx.addAll(oIdx);

            for (int j = 0; j < soIdx.size(); j++) { // 본 루프에서는 형용사의 접미사로서 관형형전성어미가 접속되어 'ㄴ' 까지를 인덱스로 간주, 해당 형용사에 대해 우측을 검사할 때는 관형형전성어미의 인덱스를 기준으로 가중치를 계산
                double currentWofOJ = 0;

                if(cores.get(adjIdx.get(k)).getType() == TypedPair.TYPE_ADJ || cores.get(adjIdx.get(k)).getType() == TypedPair.TYPE_VADJ) {
                    if(soIdx.get(j) == adjIdx.get(k) + 1 && KoreanUtil.isDependantNoun(cores.get(soIdx.get(j)))){
                        dependantNoun = soIdx.get(j);
                    }
                    else if (soIdx.get(j) >= adjIdx.get(k))
                        currentWofOJ = base.getWeightOf(cores.get(soIdx.get(j)).getFirst(), adj.getFirst()) + (((double) sD - (double) Math.abs(soIdx.get(j) - adjIdx.get(k))) / (double) sD);
                    else
                        currentWofOJ = base.getWeightOf(cores.get(soIdx.get(j)).getFirst(), adj.getFirst()) + (((double) sD - (double) Math.abs(soIdx.get(j) - (adjIdx.get(k) + 1))) / (double) sD);

                    if (weight <= currentWofOJ) { // IMPORTANT : 등호가 포함됨(우측 수식거리 우선)
                        weight = currentWofOJ;
                        candidate = soIdx.get(j);
                    }
                }else{
                    // DO NOTHING
                }
            }

            // 아크 생성
            if (candidate != -1) {
                retVal.connect(candidate, adjIdx.get(k));
            }

            if(dependantNoun != -1){
                // 의존 명사가 지시하는 것이 무엇인지 찾아낸 상태
            }

        }

        // 동사 기준 링킹

        if(vIdx.size() == 0){ // 동사 혹은 형용사가 없는 경우 - 의문문 / 홑단어

        }else {
            for (int i = 0; i < vIdx.size(); i++) {
                double weight = 0;
                int candidate = -1;

                Pair<String, String> verb = cores.get(vIdx.get(i));

                // 목적어와 동사 연결
                for (int j = 0; j < oIdx.size(); j++) {
                    double currentWofNV = base.getWeightOf(cores.get(oIdx.get(j)).getFirst(), verb.getFirst()) + (((double) sD - (double) Math.abs(oIdx.get(j) - vIdx.get(i))) / (double) sD);
                    if (weight < currentWofNV) {
                        weight = currentWofNV;
                        candidate = oIdx.get(j);
                    }
                }

                // 아크 생성
                if (candidate != -1) {
                    retVal.connect(candidate, vIdx.get(i));
                }

                // 사용된 변수 초기화
                weight = 0;
                candidate = -1;

                // 부사와 동사 연결
                for (int j = 0; j < aIdx.size(); j++) {
                    double currentWofAV = base.getWeightOf(cores.get(aIdx.get(j)).getFirst(), verb.getFirst()) + (((double) sD - (double) Math.abs(aIdx.get(j) - vIdx.get(i))) / (double) sD);
                    if (weight < currentWofAV) {
                        weight = currentWofAV;
                        candidate = aIdx.get(j);
                    }
                }

                // 아크 생성
                if (candidate != -1) {
                    retVal.connect(vIdx.get(i), candidate);
                }

                // 사용된 변수 초기화
                weight = 0;
                candidate = -1;

                // 주어와 동사 연결
                for (int j = 0; j < sIdx.size(); j++) {
                    double currentWofAV = base.getWeightOf(cores.get(sIdx.get(j)).getFirst(), verb.getFirst()) + (((double) sD - (double) Math.abs(sIdx.get(j) - vIdx.get(i))) / (double) sD);
                    if (weight < currentWofAV) {
                        weight = currentWofAV;
                        candidate = sIdx.get(j);
                    }
                }

                // 아크 생성
                if (candidate != -1) {
                    retVal.connect(vIdx.get(i), candidate);
                }
            }
        }

        return retVal;
    }

    private void link(){
        if(this.morphemes == null){
            System.out.println("There is no morpheme data.");
        }

        String prev = "";
        if(memory.size() >= 1) prev = memory.get(memory.size() - 1);

        memory.add(temporaryMemory);

        if(prev.equals(temporaryMemory) && memory.size() >= 1){
            System.out.println(MY_NAME + " : 같은 말을 두 번하지 않아도 다 알아듣고 있어요!!!");
            return;
        }

        if(memory.size() > MEMORY_SIZE) {
            memory.clear();
            memory.add(prev);
        }

        List<TypedPair> cores = new ArrayList<>();

        for (List<Pair<String, String>> eojeolResult : this.morphemes) {
            for (Pair<String, String> wordMorph : eojeolResult) {
                cores.add(new TypedPair(wordMorph));
            }
        }

        Arc procArc = getLinkedArc(shortenNounNounPhrase(cores));

        // 명령문 분기 By isOrder
        addProcData(procArc, isOrder(procArc.getWords()));

    }

    private int isOrder(List<TypedPair> words){
        int questions = 0;
        for(int i = 0; i < words.size() ; i++) {
            TypedPair pair = words.get(i); // TODO 문장 구분
            if(pair.getType() == TypedPair.TYPE_SUBJECT && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))) {
                if (words.size() > i + 1 && KoreanUtil.isSubjectivePost(words.get(i + 1))) return SENTENCE_PLAIN;
            }else if(pair.getType() == TypedPair.TYPE_QUESTION && SUBJECTS.contains(pair.getSecond()) && !(words.size() > i + 1 && KoreanUtil.isDeriver(words.get(i + 1)))){
                questions++;
            }
        }

        if(questions > 0) return SENTENCE_QUESTION;

        return SENTENCE_ORDER;
    }

    private void addProcData(Arc arc, int what){
        for(Integer key : arc.keySet()) {
            addProcData(arc.getWord(key), arc.getWord(arc.get(key)), what);
        }
    }

    private void addProcData(TypedPair pop, TypedPair word, int what){
        if(pop == null) System.out.println("pop null");
        if(word == null) System.out.println("word null");
        List<TypedPair> temp = new ArrayList<>();
        temp.add(word);
        temp.add(pop);
        learnLinkPair(temp, what);
    }

    private void learnLinkPair(List<TypedPair> know, int what){

        if(what == SENTENCE_PLAIN) {
            if (base.doYouKnow(know) > 0) {
                String concat = "는";
                if (know.get(1).getSecond().equals("VA")) concat = "은";

                if (know.get(1).getType() == TypedPair.TYPE_ADV) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'는 " + know.get(1).getFirst() + "게!!! 이미 " + base.doYouKnow(know) + "번 들었어요.");
                } else if (know.get(1).getType() == TypedPair.TYPE_SUBJECT) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'의 주체는 " + know.get(1).getFirst() + "인거죠?! " + base.doYouKnow(know) + "번 봤던 문장구조예요.");
                } else if (know.get(1).getType() == TypedPair.TYPE_QUESTION) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'에 해당하는 게 " + know.get(1).getFirst() + "인지 궁금한거죠? " + base.doYouKnow(know) + "번 봤던 문장구조예요.");
                } else if (know.get(1).getType() == TypedPair.TYPE_ADJ) {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + "다는거죠! " + base.doYouKnow(know) + "번 봤던 수식구조예요.");
                } else if (know.get(1).getType() == TypedPair.TYPE_VADJ) {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "이", "가") + " " + know.get(1).getFirst() + "다! " + base.doYouKnow(know) + "번 봤던 구조예요.");
                } else {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + concat + " 것이라고 이미 알고 있다구요!!!!! 사람들이 이미 " + base.doYouKnow(know) + "번 말했어요.");
                }

            } else {
                String concat = "는";
                if (know.get(1).getSecond().equals("VA")) concat = "은";

                if (know.get(1).getType() == TypedPair.TYPE_ADV) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'는 " + know.get(1).getFirst() + "게!!! 새롭게 알게됐어요!-");
                } else if (know.get(1).getType() == TypedPair.TYPE_SUBJECT) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'의 주체는 " + know.get(1).getFirst() + "인거죠?! 처음보는 문장구조네요.");
                } else if (know.get(1).getType() == TypedPair.TYPE_QUESTION) {
                    System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'에 해당하는 게 " + know.get(1).getFirst() + "인지 궁금하신가요?!");
                } else if (know.get(1).getType() == TypedPair.TYPE_ADJ) {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + "다는거죠?!");
                } else if (know.get(1).getType() == TypedPair.TYPE_VADJ) {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "이", "가") + " " + know.get(1).getFirst() + "다! 알아둘게요.");
                } else {
                    System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + concat + " 것이라고 기억해둘게요.");
                }
            }

        }else if(what == SENTENCE_ORDER){
            if(know.get(1).getType() == TypedPair.TYPE_ADV){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'는 " + know.get(1).getFirst() + "게!!! 새롭게 알게됐어요!!");
            }else if(know.get(1).getType() == TypedPair.TYPE_SUBJECT){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'의 주체는 " + know.get(1).getFirst() + "인거죠?! 처음보는 문장구조네요.");
            }else if(know.get(1).getType() == TypedPair.TYPE_QUESTION){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'에 해당하는 게 " + know.get(1).getFirst() + "인지 궁금하신가요?!");
            }else if(know.get(1).getType() == TypedPair.TYPE_ADJ){
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + "다는거죠?!");
            }else if(know.get(1).getType() == TypedPair.TYPE_VADJ){
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "이", "가") + " " + know.get(1).getFirst() + "다! 알아둘게요.");
            }else {
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "을", "를") + " " + know.get(1).getFirst() + "겠습니다. [서비스 호출]");
            }
        }else if(what == SENTENCE_QUESTION){
            if(know.get(1).getType() == TypedPair.TYPE_ADV){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'는 " + know.get(1).getFirst() + "게!!! 새롭게 알게됐어요!?");
            }else if(know.get(1).getType() == TypedPair.TYPE_SUBJECT){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'의 주체는 " + know.get(1).getFirst() + "인거죠?! 처음보는 문장구조네요.");
            }else if(know.get(1).getType() == TypedPair.TYPE_QUESTION){
                System.out.println(MY_NAME + " : \'" + know.get(0).getFirst() + "다\'에 해당하는 정보가 " + know.get(1).getFirst() + "인지 탐색합니다. [검색 호출]");
            }else if(know.get(1).getType() == TypedPair.TYPE_ADJ){
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "은", "는") + " " + know.get(1).getFirst() + "다는거죠?!");
            }else if(know.get(1).getType() == TypedPair.TYPE_VADJ){
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "이", "가") + " " + know.get(1).getFirst() + "다! 알아둘게요.");
            }else {
                System.out.println(MY_NAME + " : " + KoreanUtil.getComleteWordByJongsung(know.get(0).getFirst(), "을", "를") + " " + know.get(1).getFirst() + "겠습니다. [서비스 호출]");
            }
        }else{}
        base.learn(know);

    }

    public void printResult(){
        link();
    }


}
