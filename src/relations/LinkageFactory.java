package relations;

import DB.DBManager;
import kr.co.shineware.util.common.model.Pair;
import statics.StaticResponser;
import util.KoreanUtil;
import util.TimeExpression;
import util.TimeParser;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class LinkageFactory {

    /*
    문법 검사를 진행하지 않는 강제 명령이 필요할 경우, 이하의 패턴을 문장의 처음에 붙히고 공백으로 구분하여 문장을 시작한다.
    필수적으로, #으로 둘러쌓인 띄어쓰기가 없는 영문으로 아래 패턴을 구성해야 한다.
     */
    // PATTERN_FORCE START
    private static final String COMMAND_PATTERN_FORCE = "#FORCE#"; // 정적 문장에 대한 답변을 문법 분석없이 진행하기 위한 커맨드
    // PATTERN_FORCE END

    private Linkage linkage;

    private StaticResponser staticResponser;

    public static final String MY_NAME = "RES";

    private static boolean SIMILARITY_MODE = true;
    private static final double SIMILARITY_THRESHOLD = 0.5;
    private static final double SIMILARITY_HIJACKING_THRESHOLD = 0.80;

    public static final int SENTENCE_ORDER = 10;
    public static final int SENTENCE_PLAIN = 20;
    public static final int SENTENCE_QUESTION = 30;
    public static final int SENTENCE_META = 40;
    public static final int SENTENCE_METAPHORICAL_QUESTION = 50;

    public static final Set<String> SUBJECTS = new HashSet<>();
    public static final Set<String> GENERAL_NOUN = new HashSet<>();
    public static final Set<String> DEPNOUN = new HashSet<>();
    public static final Set<String> VERBS = new HashSet<>();
    public static final Set<String> REQUESTS = new HashSet<>();
    public static final Set<String> OBJECTS= new HashSet<>();
    public static final Set<String> ADJECTIVES= new HashSet<>();
    public static final Set<String> ADVERBS = new HashSet<>();
    public static final Set<String> DETERMINERS = new HashSet<>();
    public static final Set<String> BASES = new HashSet<>();

    private TimeParser timeParser; // Time Expression Parser
    private List<TimeExpression> times; // Time Expression List
    private HashMap<Integer, Integer> timeRange;

    private List<List<Pair<String, String>>> morphemes; // Analyzed morpheme units

    private KnowledgeBase base; // 가중치 부여 및 단순 문장 링킹를 위한 지식베이스
    private KnowledgeBase metaBase; // 은유적 1:N 관계를 기술하기 위한 지식베이스 (단, 1은 큰 범위의 의미이고 N은 작은 범위의 의미)
    private KnowledgeBase staticBase; // 정적 문장에 대한 단순 매칭을 수행하는 정적 지식베이스

    private DBManager dbManager; // DB Connection Manager

    private String temporaryMemory = ""; // Temporary Sentence memory variable

    private ArrayList<String> responses; // Stream Response Storage

    public LinkageFactory(List<List<Pair<String, String>>> morphemes){
        this.morphemes = morphemes;
        init();
    }

    public LinkageFactory(){
        init();
    }

    public void setMorphemes(List<List<Pair<String, String>>> morphemes, String origin){
        this.morphemes = morphemes;
        temporaryMemory = origin;
    }

    private void init(){

        responses = new ArrayList<>();
        dbManager = new DBManager();

        // 지식 베이스 캐싱 시작
        this.base = new KnowledgeBase(dbManager, KnowledgeBase.SET_SENTENCE_RECOGNIZE);
        this.metaBase = new KnowledgeBase(dbManager, KnowledgeBase.SET_METAPHOR_RECOGNIZE);
        this.staticBase = new KnowledgeBase(dbManager, KnowledgeBase.SET_STATIC_QUESTION);
        // 지식 베이스 캐싱 종료

        timeParser = new TimeParser(dbManager);
        staticResponser = new StaticResponser(dbManager);

        for(String s : new String[]{"NP", "NN", "NNG", "NNP"}) SUBJECTS.add(s);
        for(String s : new String[]{"NP", "NN", "NNG", "NA", "SL", "SH", "SW", "NF", "SN", "NA"}) OBJECTS.add(s);
        for(String s : new String[]{"NNB"}) DEPNOUN.add(s);
        for(String s : new String[]{"VV"}) VERBS.add(s);
        for(String s : new String[]{"VA"}) ADJECTIVES.add(s);
        for(String s : new String[]{"VV"}) REQUESTS.add(s);
        for(String s : new String[]{"MM"}) DETERMINERS.add(s);
        for(String s : new String[]{"XR"}) BASES.add(s);
        for(String s : new String[]{"SL", "SH", "SW", "NF", "SN", "NA", "NR"}) GENERAL_NOUN.add(s);
        GENERAL_NOUN.addAll(SUBJECTS);
    }

    private List<TypedPair> groupingPhrase(List<TypedPair> cores){
        List<TypedPair> retVal = new ArrayList<>();

        return retVal;
    }

    private List<TypedPair> shortenNounNounPhrase(List<TypedPair> cores){

        times = timeParser.parse(cores);
        timeRange = new HashMap<>();

        for(int tIdx = 0; tIdx < times.size(); tIdx++) {
            TimeExpression time = times.get(tIdx);
            for(int tr = time.getStart(); tr <= time.getEnd(); tr++) timeRange.put(tr, tIdx);
            System.out.println(time.getExpression() + " :: " + time.getDateTime());
            responses.add(time.getExpression() + " :: " + time.getDateTime());
        }

        linkage.setTimeExpressions(times);
        linkage.setTimeRange(timeRange);

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

    private List<ParallelLinkage> linkParallels(List<TypedPair> cores, List<Integer> adjIdx){
        List<ParallelLinkage> retVal = new ArrayList<>();

        boolean flag = false;
        ParallelLinkage unit = null;

        for(int i = 0; i < cores.size(); i++){
            TypedPair pair = cores.get(i);
            if(GENERAL_NOUN.contains(pair.getSecond())){
                if(flag){ // 이미 생성되었으며 추가해야하는 경우
                    unit.add(i);
                }else{ // 최초 병합 개시
                    unit = new ParallelLinkage();
                    unit.add(i);
                    flag = true;
                }
            }else{
                if(adjIdx.contains(i)){ // 문법적으로 무의미한 요소 통과
                    continue;
                }else{
                    if(KoreanUtil.isConcatenation(pair)){ // 현재 페어가 접속조사로 추정되는 경우
                        continue;
                    }else{
                        if (flag) {
                            retVal.add(unit);
                        }else{
                            // Do nothing
                        }
                        flag = false;
                    }
                }
            }
        }
        if(flag){
            retVal.add(unit);
        }

        return retVal;
    }

    private MorphemeArc getLinkedArc(List<TypedPair> cores){

        List<Integer> vIdx = new ArrayList<>(); // VERB INDEX
        List<Integer> adjIdx = new ArrayList<>(); // ADJECTIVE INDEX
        List<Integer> oIdx = new ArrayList<>(); // OBJECT INDEX
        List<Integer> sIdx = new ArrayList<>(); // SUBJECT INDEX
        List<Integer> aIdx = new ArrayList<>(); // ADVERB INDEX
        List<Integer> soIdx = new ArrayList<>(); // SUBJECT + OBJECT INDEX
        List<Integer> mIdx = new ArrayList<>(); // METAPHORICAL INDEX
        List<ParallelLinkage> parallelLinkages = new ArrayList<>(); // 병렬 구조 명사

        MorphemeArc retVal = new MorphemeArc(cores);

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
            }else if(SUBJECTS.contains(pair.getSecond()) && !(cores.size() > i + 1 && KoreanUtil.isDeriver(cores.get(i + 1)))) { // 주어 혹은 목적어로 현재 페어가 입력될 수 있는 경우
                if (KoreanUtil.isQuestion(cores.get(i))) {
                    cores.get(i).setType(TypedPair.TYPE_QUESTION);
                    sIdx.add(i);
                } else {
                    if (cores.size() > i + 1 && KoreanUtil.isSubjectivePost(cores.get(i + 1))) { // 다음 페어가 주격조사인 경우
                        cores.get(i).setType(TypedPair.TYPE_SUBJECT);
                        sIdx.add(i);
                    } else {
                        if(OBJECTS.contains(pair.getSecond()) && (KoreanUtil.isEOS(cores.get(i + 1)) || KoreanUtil.isPositiveDesignator(cores.get(i + 1)))) {
                            cores.get(i).setType(TypedPair.TYPE_METAPHORE);
                            mIdx.add(i);
                        }else {
                            cores.get(i).setType(TypedPair.TYPE_OBJECT); // 다음 페어에 주격조사가 아닌 경우 목적어로 간주
                            oIdx.add(i);
                        }
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

        parallelLinkages = linkParallels(cores, adjIdx);

        // 형용사와 명사 연결
        soIdx.addAll(oIdx); // addAll 순서 중요!!!
        soIdx.addAll(sIdx); // addAll 순서 중요!!!

        HashSet<Integer> minSet = new HashSet<>();
        List<Integer> toDelete = new ArrayList<>();
        for(int idxn = 0; idxn < soIdx.size(); idxn++) {
            Integer idx = soIdx.get(idxn);
            ParallelLinkage parallelLinkage = isLinkedPair(idx, parallelLinkages);
            if(parallelLinkage != null){
                cores.get(idx).setLinked(true);
                cores.get(idx).setParallelLinkage(parallelLinkage);

                int min = cores.get(idx).getParallelLinkage().getFirstIndex();
                int max = cores.get(idx).getParallelLinkage().getLastIndex();
                if(idx != min && idx != max) toDelete.add(idxn);
                else if(min != max && cores.get(idx).getParallelLinkage().size() == 2 && !minSet.contains(min)) {
                    minSet.add(min);
                    toDelete.add(idxn);
                }
            }
        }

        HashSet<Integer> unlinkables = new HashSet<>();
        for(Integer del : toDelete) {
            unlinkables.add(soIdx.get(del));
            soIdx.remove(del);
        }

        // 형용사 기준 링킹

        for(int k = 0; k < adjIdx.size(); k++){ // TODO Parallel

            double weight = 0;
            int candidate = -1;
            int dependantNoun = -1;

            Pair<String, String> adj = cores.get(adjIdx.get(k));

            for (int j = 0; j < soIdx.size(); j++) { // 본 루프에서는 형용사의 접미사로서 관형형전성어미가 접속되어 'ㄴ' 까지를 인덱스로 간주, 해당 형용사에 대해 우측을 검사할 때는 관형형전성어미의 인덱스를 기준으로 가중치를 계산
                double currentWofOJ = 0;

                if(cores.get(adjIdx.get(k)).getType() == TypedPair.TYPE_ADJ || cores.get(adjIdx.get(k)).getType() == TypedPair.TYPE_VADJ) {
                    if(soIdx.get(j) == adjIdx.get(k) + 1 && KoreanUtil.isDependantNoun(cores.get(soIdx.get(j)))){
                        dependantNoun = soIdx.get(j);
                    }
                    else if (soIdx.get(j) >= adjIdx.get(k)) {
                        currentWofOJ = base.getWeightOf(cores.get(soIdx.get(j)).getFirst(), adj.getFirst()) + (((double) sD - (double) Math.abs(soIdx.get(j) - adjIdx.get(k))) / (double) sD);
                    }
                    else {

                        currentWofOJ = base.getWeightOf(cores.get(soIdx.get(j)).getFirst(), adj.getFirst()) + (((double) sD - (double) Math.abs(soIdx.get(j) - (adjIdx.get(k) + 1))) / (double) sD);
                    }

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

        // 은유형 표현 링킹
        for(int i = 0; i < mIdx.size(); i++){ // TODO Parallel
            double weight = 0;
            int candidate = -1;

            Pair<String, String> meta = cores.get(mIdx.get(i));

            // 주체적 명사와 종속적 명사 연결
            for (int j = 0; j < sIdx.size(); j++) {
                double currentWofNM = metaBase.getWeightOf(cores.get(sIdx.get(j)).getFirst(), meta.getFirst()) + (((double) sD - (double) Math.abs(sIdx.get(j) - mIdx.get(i))) / (double) sD);
                if (weight < currentWofNM) {
                    weight = currentWofNM;
                    candidate = sIdx.get(j);
                }
            }

            // 아크 생성
            if (candidate != -1) {
                retVal.connect(mIdx.get(i), candidate);
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
                for (int j = 0; j < oIdx.size(); j++) { // TODO Parallel
                    if(unlinkables.contains(oIdx.get(j))) continue;
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
                for (int j = 0; j < sIdx.size(); j++) { // TODO Parallel
                    double prob = 1.0;
                    if(sIdx.get(j) - vIdx.get(i) > 0) prob = (((double) sD - (double) Math.abs(sIdx.get(j) - vIdx.get(i))) / (double) sD);
                    double currentWofAV = base.getWeightOf(cores.get(sIdx.get(j)).getFirst(), verb.getFirst()) + ((((double) sD - (double) Math.abs(sIdx.get(j) - vIdx.get(i))) / (double) sD) * prob);
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

    private ParallelLinkage isLinkedPair(Integer i, List<ParallelLinkage> pList){
        for (ParallelLinkage pLink : pList) {
            if(pLink.contains(i)) return pLink;
        }
        return null;
    }

    public Linkage link(){
        linkage = new Linkage();

        linkage.setOriginalMessage(temporaryMemory);
        linkage.setBase(base);
        linkage.setMetaBase(metaBase);
        linkage.setStaticBase(staticBase);

        responses.clear();

        if(this.morphemes == null){
            System.out.println("There is no morpheme data.");
        }

        List<TypedPair> cores = new ArrayList<>();

        for (List<Pair<String, String>> eojeolResult : this.morphemes) {
            for (Pair<String, String> wordMorph : eojeolResult) {
                cores.add(new TypedPair(wordMorph));
            }
        }

        String intention = "";
        String serialWords = "";
        String serialTags = "";

        for (int q = 0; q < cores.size(); q++) {
            Pair<String, String> pair = cores.get(q);
            if(!KoreanUtil.isSpecialCharacter(pair)) {
                serialWords += pair.getFirst();
                serialTags += pair.getSecond();
            }
        }

        // PATTERN DETECTING START
        if(temporaryMemory.indexOf(COMMAND_PATTERN_FORCE) == 0){
            if(cores.size() < 4) {
                System.out.println("[WARN :: INVALID PARAMETER HAS BEEN DETECTED]");
            }else if(cores.get(3).getFirst().equals(StaticResponser.INTENT_DIRECT)){ // 다이렉트 메시지 강제 학습

                intention = cores.get(3).getFirst();

                String raw = temporaryMemory.replaceAll("#FORCE# DIRECT", "").trim();

                int sharpIndex = raw.indexOf("#");
                if(sharpIndex == -1) return linkage;

                String leftHand = raw.substring(0, sharpIndex).trim();
                if(sharpIndex + 1 > raw.length()) return linkage;

                String rightHand = raw.substring(sharpIndex + 1, raw.length()).trim();

                staticBase.memorize(leftHand, "NNG", intention, rightHand);
                System.out.println("[INFO :: 강제 학습 명령이 정상적으로 수행됨]");

            }else{ // 강제 의도 매핑 학습
                intention = cores.get(3).getFirst();
                serialWords = "";
                serialTags = "";
                for (int q = 4; q < cores.size(); q++) {
                    Pair<String, String> pair = cores.get(q);
                    if(!KoreanUtil.isSpecialCharacter(pair)) {
                        serialWords += pair.getFirst();
                        serialTags += pair.getSecond();
                    }
                }
                staticBase.memorize(serialWords.trim(), serialTags.trim(), intention, "");
                System.out.println("[INFO :: 강제 학습 명령이 정상적으로 수행됨]");
            }
            return linkage;
        }
        // PATTERN DETECTING END

        String cleanedSerial = KoreanUtil.eliminateMeaningLess(serialWords);
        if(staticBase.containsKey(cleanedSerial)){
            String intent = staticBase.get(cleanedSerial).keySet().iterator().next();
            System.out.println(staticResponser.talk(intent, temporaryMemory));
            responses.add(staticResponser.talk(intent, temporaryMemory));

            linkage.setArc(null);
            linkage.setInstantResponses(responses);
            return linkage;
        }

        MorphemeArc procArc = getLinkedArc(shortenNounNounPhrase(cores)); // 아크를 연결하고 분석을 수행

        double prob = 0.0; // 유사도 척도
        String prediction = ""; // 예측된 사용자 의도

        if(SIMILARITY_MODE) {

            prediction = "";
            prob = 0.0;
            String matched = "";
            for (String str : staticBase.keySet()) {
                double newProb = KoreanUtil.getEditDistanceRate(str, serialWords, true);
                if (prob < newProb) {
                    prediction = staticBase.get(str).keySet().iterator().next();
                    prob = newProb;
                    matched = str;
                }
            }

            if(prediction.equals(StaticResponser.INTENT_DIRECT)) temporaryMemory = matched;

            System.out.println("[Similarity : " + prediction + " / " + String.format("%.2f", prob * 100) + "%]");
            //if(stream) responses.add("[INFO :: 유사도 기반 정적 응답 (Similarity : " + prediction + " / " + String.format("%.2f", prob * 100) + "%) ]");

            if (procArc.keySet().size() == 0) {
                if (prob >= SIMILARITY_THRESHOLD) {
                    System.out.println(staticResponser.talk(prediction, temporaryMemory));
                    responses.add(staticResponser.talk(prediction, temporaryMemory));
                } else {
                    System.out.println(staticResponser.talk(StaticResponser.INTENT_NOTHING, temporaryMemory));
                    responses.add(staticResponser.talk(StaticResponser.INTENT_NOTHING, temporaryMemory));
                }
            } else {
                if (prob >= SIMILARITY_HIJACKING_THRESHOLD) {
                    System.out.println(staticResponser.talk(prediction, temporaryMemory));
                    responses.add(staticResponser.talk(prediction, temporaryMemory));
                }
            }
        }

        linkage.setArc(procArc);
        linkage.setInstantResponses(responses);

        return linkage;

    }

    public void setSimilarityMode(boolean mode){ // 유사도 검증 모드 설정 시 실행속도가 크게 저하될 수 있음
        this.SIMILARITY_MODE = mode;
    }


}
