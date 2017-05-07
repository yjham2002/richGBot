
import nlp.NaturalLanguageEngine;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class AppMain {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        NaturalLanguageEngine nlpEngine = NaturalLanguageEngine.getInstance().setDebugMode(false);

        while(true) {
            System.out.print("USER : ");
            String command = scanner.nextLine();
            nlpEngine.analyzeAndPrint(command, false);
        }

    }


}

