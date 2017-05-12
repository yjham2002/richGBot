package nlp;

import analysis.ITrigger;
import analysis.SpeechActAnalyser;
import exceptions.PurposeSizeException;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import react.PurposeEncloser;
import react.Reactor;
import relations.Linkage;
import relations.LinkageFactory;
import statics.StaticResponser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static relations.LinkageFactory.MY_NAME;

/**
 * Created by a on 2017-04-19.
 */
public class NaturalLanguageEngine extends ContextNLP{

    private static final int MEMORY_SIZE = 100;

    public static final int MODE_DEBUG = 100;
    public static final int MODE_REAL = 200;
    private static List<Linkage> memory;

    private boolean finalPurpose = false;
    private static int mode = MODE_REAL;
    private static Komoran komoran;
    private static LinkageFactory linkageFactory;

    private static String modelPath = "./models";
    private static String dicPath = "./user_data/NIADic.user";

    private static NaturalLanguageEngine instance; // Singleton instance

    private NaturalLanguageEngine(){
        try {
            PurposeEncloser.start();
            System.out.println(" ::: NLP Engine has been started successfully ::: \n");
        }catch(Exception e){
            System.out.println(" ::: An error occured while Starting NLP Engine ::: \n");
        }

    }

    public static NaturalLanguageEngine getInstance(){
        if(komoran == null) {
            komoran = new Komoran(modelPath);
            //komoran.setUserDic(dicPath);
        }
        if(memory == null) memory = new ArrayList<>();
        if(linkageFactory == null) linkageFactory = new LinkageFactory();
        if(instance == null) instance = new NaturalLanguageEngine();
        return instance;
    }

    public static NaturalLanguageEngine setDebugMode(boolean mode){
        if(mode) instance.mode = MODE_DEBUG;
        else instance.mode = MODE_REAL;
        return instance;
    }

    private void memorize(Linkage linkage){
        if(memory.size() > MEMORY_SIZE) {
            memory.clear();
        }
        memory.add(linkage);
    }

    public List<String> analyzeInstantly(String text, boolean enableSimilarityMode){
        linkageFactory.setSimilarityMode(enableSimilarityMode);
        List<List<Pair<String, String>>> result = komoran.analyze(text);
        linkageFactory.setMorphemes(result, text);
        if(mode == MODE_DEBUG) {
            for (List<Pair<String, String>> eojeolResult : result) {
                for (Pair<String, String> wordMorph : eojeolResult) {
                    System.out.print(wordMorph + "   ");
                }
                System.out.println();
            }
        }
        Linkage linkage = linkageFactory.link();

        memorize(linkage);

        // TODO 임시 설계 - 응답 클래스가 별도로 생성될 경우, 변경이 필수적임
        Pair<List<String>, List<String>> resPair = linkage.interaction(new ITrigger() {
            @Override
            public boolean run(HashMap<String, Object> extra,  List<String> ref) {

                return false;
            }
        }, this);

        if(linkage.isOngoing() || finalPurpose){
            List<String> response = new ArrayList<>();
            response.addAll(Reactor.getInstanceForAnswer().getResponse());
            response.add(Reactor.debugExtra()); // TODO DELETE
            try {
                if (finalPurpose) response.addAll(Reactor.finalizePurpose());
            }catch(PurposeSizeException pe){
                pe.printStackTrace();
            }
            finalPurpose = false;
            return response;
        }else{
            List<String> merged = new ArrayList<>();
            if(resPair != null && resPair.getFirst() != null && resPair.getSecond() != null) {
                merged.addAll(resPair.getFirst());
                merged.addAll(resPair.getSecond());
            }

            return merged;
        }
    }

    public boolean isFinalPurpose() {
        return finalPurpose;
    }

    public void setFinalPurpose(boolean finalPurpose) {
        this.finalPurpose = finalPurpose;
    }
}
