package relations;

import relations.KnowledgeBase;
import relations.LinkageFactory;
import relations.MorphemeArc;
import relations.TypedPair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a on 2017-04-19.
 */
public class SentenceMultiplexer { // TODO 임시 분리임 - 설계필요


    public SentenceMultiplexer(List<TypedPair> know, int what, MorphemeArc arc, KnowledgeBase base, KnowledgeBase metaBase, List<String> responses){

        TypedPair objFirst = know.get(0);
        TypedPair objLast = know.get(1);
        if(know.get(0).isLinked()){ // TEMPORARY
            TypedPair temp = new TypedPair();
            String first = "";
            String tempPOS = "NNG";
            for(int loop = 0; loop < expandLinkage(know.get(0), arc).size(); loop++){
                TypedPair pair = expandLinkage(know.get(0), arc).get(loop);
                first += pair.getFirst();
                if(loop + 1 < expandLinkage(know.get(0), arc).size()) {
                    first += ",";
                }
            }
            temp.setFirst(first);
            temp.setSecond(tempPOS);
            objFirst = temp;
        }
        if(know.get(1).isLinked()){ // TEMPORARY
            TypedPair temp = new TypedPair();
            String first = "";
            String tempPOS = "NNG";
            for(int loop = 0; loop < expandLinkage(know.get(1), arc).size(); loop++){
                TypedPair pair = expandLinkage(know.get(1), arc).get(loop);
                first += pair.getFirst();
                if(loop + 1 < expandLinkage(know.get(1), arc).size()) {
                    first += ",";
                }
            }
            temp.setFirst(first);
            temp.setSecond(tempPOS);
            objLast = temp;
        }

        String classified = "";

        switch(what){
            case LinkageFactory.SENTENCE_PLAIN: classified = "[평서문] >> "; break;
            case LinkageFactory.SENTENCE_ORDER: classified = "[명령문] >> "; break;
            case LinkageFactory.SENTENCE_QUESTION: classified = "[의문문] >> "; break;
            case LinkageFactory.SENTENCE_META: classified = "[은유형 대입문] >> "; break;
            case LinkageFactory.SENTENCE_METAPHORICAL_QUESTION: classified = "[은유형 의문문] >> "; break;
            default: break;
        }
        System.out.print(classified);

        switch(what){
            case LinkageFactory.SENTENCE_PLAIN: case LinkageFactory.SENTENCE_ORDER: case LinkageFactory.SENTENCE_QUESTION:
                System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
                responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                if (objLast.getType() == TypedPair.TYPE_ADV) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                } else if (objLast.getType() == TypedPair.TYPE_SUBJECT) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                } else if (objLast.getType() == TypedPair.TYPE_QUESTION) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                } else if (objLast.getType() == TypedPair.TYPE_ADJ) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                } else if (objLast.getType() == TypedPair.TYPE_VADJ) {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                } else {
//                    System.out.println(LinkageFactory.MY_NAME + " : [" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                    responses.add("[" + objFirst.getFirst() + "]-[" + objLast.getFirst() + "] [FREQ : " + base.doYouKnow(know) + "]");
//                }
                break;
            case LinkageFactory.SENTENCE_META:
                if (objFirst.getType() == TypedPair.TYPE_METAPHORE) {
                    System.out.println(LinkageFactory.MY_NAME + " : [종속 : " + objLast.getFirst() + "] [주체 :" + objFirst.getFirst() + "] [빈도 : " + metaBase.doYouKnow(know) + "]");
                    responses.add("[종속 : " + objLast.getFirst() + "] [주체 :" + objFirst.getFirst() + "] [빈도 : " + metaBase.doYouKnow(know) + "]");
                }
                break;
            case LinkageFactory.SENTENCE_METAPHORICAL_QUESTION:
                if (objFirst.getType() == TypedPair.TYPE_METAPHORE) {
                    List<String> backTrack = metaBase.getBackTrackingList(objLast.getFirst(), new ArrayList<>());
                    System.out.print("\'" + objLast.getFirst() + "\'에 대한 지식 백트랙킹 : [");
                    String streamStr = "\'" + objLast.getFirst() + "\'에 대한 지식 백트랙킹 : [";
                    for(int e = 0; e < backTrack.size(); e++){
                        System.out.print(backTrack.get(e));
                        streamStr += backTrack.get(e);
                        if(e < backTrack.size() - 1) {
                            System.out.print(", ");
                            streamStr += ", ";
                        }
                        else {
                            System.out.print("]\n");
                            streamStr += "]\n";
                        }
                    }
                    responses.add(streamStr);
                }
                break;
            default: break;
        }

        if(what != LinkageFactory.SENTENCE_META && what != LinkageFactory.SENTENCE_METAPHORICAL_QUESTION) {
            base.learn(know);
        }
        else if(what == LinkageFactory.SENTENCE_METAPHORICAL_QUESTION){
            // DO NOTHING
        }
        else{
            metaBase.learn(know);
        }

    }


    private List<TypedPair> expandLinkage(TypedPair pair, MorphemeArc arc){
        List<TypedPair> list = new ArrayList<>();
        if(pair.isLinked()){
            Iterator<Integer> iterator = pair.getParallelLinkage().iterator();
            while(iterator.hasNext()){
                Integer unit = iterator.next();
                list.add(arc.getWords().get(unit));
            }
        }else{
            list.add(pair);
        }
        return list;
    }

}
