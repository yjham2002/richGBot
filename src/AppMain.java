
import nlp.NaturalLanguageEngine;
import relations.LinkageFactory;
import util.WeatherParser;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class AppMain {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        NaturalLanguageEngine nlpEngine = NaturalLanguageEngine.getInstance().setDebugMode(true);

        while(true) {
            System.out.print("USER : ");
            String command = scanner.nextLine();
            List<String> list = nlpEngine.analyzeInstantly(command, false);
            for(String res : list){
                System.out.println(LinkageFactory.MY_NAME + " : " + res);
            }
        }

    }


}

