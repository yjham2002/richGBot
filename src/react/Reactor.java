package react;

import analysis.Intention;
import analysis.SpeechActAnalyser;
import exceptions.PurposeSizeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static react.PurposeEncloser.getPurpose;

/**
 * Created by a on 2017-05-09.
 */
public class Reactor{

    private static String currentIntention;
    private static int currentLevel = 0;
    private List<Intention> intentions;
    private static HashSet<String> expectation;
    private static Reactor instance;

    public static Reactor getInstance(List<Intention> intentions){
        if(instance == null) instance = new Reactor(intentions);
        return instance;
    }

    public static Reactor getInstanceForAnswer(){
        return instance;
    }

    public static void clear(){
        currentLevel = 0;
        instance = null;
        currentIntention = null;
    }

    private Reactor(List<Intention> intentions){
        this.intentions = intentions;
    }

    public static boolean isEOC(){
        return currentLevel >= getPurpose(currentIntention).getMaximum();
    }

    public static String debugExtra(){
        String msg = "";
        HashMap<String, Object> map = PurposeEncloser.getPurpose(currentIntention).getExtra();
        for(String s : map.keySet()){
            msg += s + " :: " + map.get(s) + "\n";
        }

        return msg;
    }

    public static List<String> finalizePurpose() throws PurposeSizeException{
        List<String> extraMessage = new ArrayList<>();

        HashMap<String, Object> map = PurposeEncloser.getPurpose(currentIntention).getExtra();
        for(String s : map.keySet()){
            System.out.println(s + " :: " + map.get(s));
        }
        System.out.println();
        extraMessage.addAll(PurposeEncloser.getPurpose(currentIntention).run());
        PurposeEncloser.flush();
        Reactor.clear();

        return extraMessage;
    }

    public List<String> getResponse(){
        List<String> conversation = new ArrayList<>();
        if(currentLevel <= getPurpose(currentIntention).getMaximum()) conversation.add(getPurpose(currentIntention).getLevelMessages().get(currentLevel++));
        else return null;

        return conversation;
    }

    public static HashSet<String> extractExpectation(Intention intention, boolean triggered){

        if(triggered) currentIntention = intention.getIntentionCode();

        expectation = new HashSet<>();

        if(PurposeEncloser.containsCode(currentIntention)){
            PurposeEncloser.getPurpose(currentIntention).getExtra().put(PurposeEncloser.getPurpose(currentIntention).getMapper().get(currentLevel), intention.getOriginalMessage());
            expectation = PurposeEncloser.getFilter(currentIntention, currentLevel - 1);
        }

        return expectation;

    }

}
