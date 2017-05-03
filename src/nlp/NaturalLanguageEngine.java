package nlp;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import relations.Linkage;
import relations.LinkageFactory;
import statics.StaticResponser;

import java.util.ArrayList;
import java.util.List;

import static relations.LinkageFactory.MY_NAME;

/**
 * Created by a on 2017-04-19.
 */
public class NaturalLanguageEngine {

    private static final int MEMORY_SIZE = 100;

    public static final int MODE_DEBUG = 100;
    public static final int MODE_REAL = 200;
    private static List<Linkage> memory;

    private static int mode = MODE_REAL;
    private static Komoran komoran;
    private static LinkageFactory linkageFactory;

    private static String modelPath = "./models";
    private static String dicPath = "./user_data/NIADic.user";

    private static NaturalLanguageEngine instance; // Singleton instance

    private NaturalLanguageEngine(){}

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

        return linkage.interaction();
    }

    public void analyzeAndPrint(String text, boolean enableSimilarityMode){
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

        linkage.interaction();
    }

}
