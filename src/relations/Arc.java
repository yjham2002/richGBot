package relations;

import kr.co.shineware.util.common.model.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 함의진
 * 의존 제약 조건에 따른 아크 연결을 위한 클래스로 맵의 키로 의존소를 이용한다.
 * 1. 해시맵의 특성을 통해 지배소 유일의 원칙을 제약한다. (컬리전 시에는 인덱스를 비교한다.)
 * 2. 투영의 원칙은 이하의 isCrossed 메소드를 통해 제약한다.
 * 3. 지배소 후위의 원칙은 지배소와 의존소 각각의 인덱스를 통해 아래 connect 내에서 제약한다. - 자유로운 명령을 위해 제약하지 않음 *
 * 4. 격틀/의미정보 제약은 지식베이스의 기계학습을 통해 제약한다.
 * 5. 필수 성분 제약의 경우, 연결 과정에서 검출할 수 없으므로, 본 클래스에서 제약하지 않는다. *
 */
public class Arc extends HashMap<Integer, Integer>{

    private static boolean semaphore = false;

    private static final int MODE_DEBUG = 100;
    private static final int MODE_ONGOING = 200;

    private static final int CURRENT = MODE_DEBUG;

    private static final int INVALID = -1;
    private static final int VALID = 1;

    private List<TypedPair> words;

    public Arc(List<TypedPair> linearWords){
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
        for(Integer i : this.keySet()){
            if(i >= dependant) break;
            if(this.get(i) > dominant) return true;
        }
        return false;
    }

    /**
     * 아크를 생성하기 위한 연결 메소드 - 연결과 동시에 여러 종류의 의존성 제약을 검출한다.
     * @param dominant
     * @param dependant
     */
    public int connect(int dominant, int dependant){
        semaphore = true;
        String debugPos = "DO[" + dominant + "] DE[" + dependant + "]";
        boolean doNothing = false;
        if(dominant < 0 || dependant < 0 || dominant >= words.size() || dependant >= words.size()){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [참조할 수 없는 위치] : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
            doNothing = true;
        }
//        if(dominant <= dependant){
//            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [지배소 후위의 원칙]이 위반된 아크입니다. : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
//            doNothing = true;
//        }
        if(isCrossed(dominant, dependant)){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [투영의 원칙]이 위반된 아크입니다. : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
            doNothing = true;
        }
        if(this.containsKey(dependant)){
            if(this.get(dependant) != dominant){
                if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [지배소 유일의 원칙]이 위반된 아크입니다. : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
                doNothing = true;
            }
        }

        if(doNothing) {
            semaphore = false;
            return INVALID;
        }

        this.put(dependant, dominant);
        if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [아크가 정상적으로 연결됨] : " + debugPos + " [" + words.get(dominant).getFirst() + "/" + words.get(dependant).getFirst() + "]");
        return VALID;
    }

    @Override
    public Integer put(Integer i, Integer ii){
        if(semaphore){
            super.put(i, ii);
            semaphore = false;
            return VALID;
        }else {
            if (CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [의존성 제약을 위반할 수 있는 임의 삽입]");
            return INVALID;
        }
    }

    public TypedPair getWord(Integer key){
        if((!this.containsKey(key) && !this.containsValue(key)) || key < 0 || key >= words.size()){
            if(CURRENT == MODE_DEBUG) System.out.println("DEBUG :: [존재하지 않는 인덱스] SIZE[" + words.size() + "] REF[" + key + "]");
            return null;
        }
        return words.get(key);
    }

}
