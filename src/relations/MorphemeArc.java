package relations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author  함의진
 * @version 1.0
 * 별도의 제약조건을 설정하지 않은 리스트형 아크 자료구조
 * Arc에 비해 한 노드로부터 여러 노드로의 연결이 가능하게 설정되었으며, 의미상 국문학적 제약이 설정되지 않음.
 */
public class MorphemeArc extends HashMap<Integer, ArrayList<Integer>> {

    protected static boolean semaphore = false;

    protected static final int MODE_DEBUG = 100;
    protected static final int MODE_ONGOING = 200;

    protected static final int CURRENT = MODE_DEBUG;

    protected static final int INVALID = -1;
    protected static final int VALID = 1;

    private boolean flag = false;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    protected List<TypedPair> words;

    public MorphemeArc(List<TypedPair> linearWords){
        super();
        if(linearWords == null) words = new ArrayList<>();
        else words = linearWords;
    }

    /**
     * 투영의 원칙 검출을 위한 아크의 교차 여부 검사 - 시간복잡도는 기 연결된 아크의 개수에 대해 O(N)이다.
     * @param dominant
     * @param dependant
     * @return
     */
    private boolean isCrossed(int dominant, int dependant){
        int leftSide = dependant;
        int rightSide = dominant;
        if(dominant < dependant){
            leftSide = dominant;
            rightSide = dependant;
        }
        for(Integer i : this.keySet()){
            for(int cursor = 0; cursor < this.get(i).size(); cursor++) {
                Integer j = this.get(i).get(cursor);
                int key = i;

                int scoreOrigin = Math.abs(key - j);
                int scoreNewly =  Math.abs(dominant - dependant);

                int mLeft = i;
                int mRight = j;
                if (mLeft > mRight) {
                    mLeft = j;
                    mRight = i;
                }

                if (leftSide > mLeft && rightSide > mRight && mRight > leftSide) {
//                    if(scoreNewly <= scoreOrigin) {
//                        this.get(i).remove(cursor);
//                        return false;
//                    }
                    return true;
                }
                if (leftSide < mLeft && rightSide < mRight && mRight < leftSide) {
//                    if(scoreNewly <= scoreOrigin) {
//                        this.get(i).remove(cursor);
//                        return false;
//                    }
                    return true;
                }
            }
        }
        return false;
    }

    public List<TypedPair> getWords() {
        return words;
    }

    public void setWords(List<TypedPair> words) {
        this.words = words;
    }

    /**
     * 아크를 생성하기 위한 연결 메소드 - 연결과 동시에 여러 종류의 의존성 제약을 검출한다.
     * @param dominant
     * @param dependant
     */
    public int connect(Integer dominant, Integer dependant){

        semaphore = true;
        String debugPos = "DO[" + dominant + "] DE[" + dependant + "]";
        boolean doNothing = false;
        if(dominant < 0 || dependant < 0 || dominant >= words.size() || dependant >= words.size()){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [Unable to Refer] : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
            doNothing = true;
        }
//        if(dominant <= dependant){
//            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [지배소 후위의 원칙]이 위반된 아크입니다. : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
//            doNothing = true;
//        }

        if(isCrossed(dominant, dependant)){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [Projection's Law] has been violated  : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
            doNothing = true;
        }

//        if(this.containsKey(dependant)){
//            if(isNotTheOnlyOne(dependant, dominant)){
//                if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [지배소 유일의 원칙]이 위반된 아크입니다. : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
//                doNothing = true;
//            }
//        }

        if(doNothing) {
            semaphore = false;
            return INVALID;
        }

        this.put(dependant, dominant);

        if(CURRENT == MODE_DEBUG) System.out.println("INFO :: [Arc Linked] : "
                + debugPos + " [" + words.get(dominant).getFirst() + "["
                + dominant + "]" + "/" + words.get(dependant).getFirst() + "[" + dependant + "]" + "] :: SIZE [" + this.get(dependant).size() + "]");

        return VALID;
    }

    private boolean isNotTheOnlyOne(Integer dependant, Integer key){
        for(Integer i : this.get(dependant)){
            if(i != key) return true;
        }
        return false;
    }

    @Override
    public ArrayList<Integer> put(Integer i, ArrayList<Integer> ii){
        if(semaphore){
            if(!this.containsKey(i)) super.put(i, new ArrayList<>());
            this.get(i).addAll(ii);
            semaphore = false;
            return ii;
        }else {
            if (CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [의존성 제약을 위반할 수 있는 임의 삽입]");
            return null;
        }
    }

    public ArrayList<Integer> put(Integer i, Integer ii){
        ArrayList<Integer> entry = new ArrayList<>();
        entry.add(ii);


        return this.put(i, entry);
    }

    private boolean containsValue(Integer key){
        for(ArrayList<Integer> list : this.values()){
            if(list.contains(key)) return true;
        }
        return false;
    }

    public TypedPair getWord(Integer key){
        if((!this.containsKey(key) && !this.containsValue(key)) || key < 0 || key >= words.size()){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [존재하지 않는 인덱스] SIZE[" + words.size() + "] REF[" + key + "]");
            return null;
        }
        return words.get(key);
    }

}
