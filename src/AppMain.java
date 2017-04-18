
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import relations.Linker;
import util.KoreanUtil;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class AppMain {

    public static void main(String[] args) {

        Komoran komoran = new Komoran("C:\\Users\\a\\IdeaProjects\\richGBot\\models");
        //komoran.setUserDic("C:\\Users\\a\\IdeaProjects\\GBot\\user_data\\NIADic.user");

        Scanner scanner = new Scanner(System.in);

        Linker linker = new Linker();
        linker.setSimilarityMode(false);

        while(true) {
            System.out.print("USER : ");
            String command = scanner.nextLine();
            List<List<Pair<String, String>>> result = komoran.analyze(command);
            linker.setMorphemes(result, command);
            linker.printResult();
            for (List<Pair<String, String>> eojeolResult : result) {
                for (Pair<String, String> wordMorph : eojeolResult) {
                    System.out.print(wordMorph + "   ");
                }
                System.out.println();
            }
        }

    }


}

