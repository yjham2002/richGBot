
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import relations.Linker;

import java.util.*;

/**
 * Created by a on 2017-03-23.
 */
public class AppMain {

//    public static String sentence1 = "나는 세호가 밥을 먹는다고 생각한다";
//    public static String sentence2 = "독한 감기에 걸리면 정말 아프다 낚싯줄이 감기는 것처럼 좀 쓰린데 이는 내가 많이 어리석어 밥을 아예 먹지 않았기 때문이다.";
//    public static String sentence3 = "컴퓨터를 이용해서 프로그래밍을 하고싶다 그리고 노트에 소설을 쓰고싶다.";
//    public static String sentence4 = "모니터가 참 멋지네요 하지만 벌어진 틈새가 신경 쓰입니다.";
//    public static String sentence5 = "핸드폰을 통해 전화를 받으면 상대방의 목소리가 들립니다 마치 옆에 있는 듯한 느낌을 받아볼 수 있지요.";

    public static void main(String[] args) {

        Set<String> EXIT_COMMAND = new HashSet<>();
        EXIT_COMMAND.add("꺼져");
        EXIT_COMMAND.add("종료");
        EXIT_COMMAND.add("사라져");
        EXIT_COMMAND.add("닥쳐");
        EXIT_COMMAND.add("EXIT");
        EXIT_COMMAND.add("저리가");
        EXIT_COMMAND.add("이제그만");
        EXIT_COMMAND.add("디버그하자");
        EXIT_COMMAND.add("디버그");
        EXIT_COMMAND.add("안녕");
        EXIT_COMMAND.add("헤어져");

        Komoran komoran = new Komoran("C:\\Users\\HP\\IdeaProjects\\richGBot\\models");
        //komoran.setUserDic("C:\\Users\\a\\IdeaProjects\\GBot\\user_data\\NIADic.user");

        Scanner scanner = new Scanner(System.in);

        Linker linker = new Linker();

        while(true) {
            System.out.print("USER : ");
            String command = scanner.nextLine();
            if(EXIT_COMMAND.contains(command)) System.exit(0);
            List<List<Pair<String, String>>> result = komoran.analyze(command);
            linker.setMorphemes(result, command);
            linker.printResult();
//            for (List<Pair<String, String>> eojeolResult : result) {
//                for (Pair<String, String> wordMorph : eojeolResult) {
//                    System.out.print(wordMorph + "   ");
//                }
//                System.out.println();
//            }
        }

    }


}

