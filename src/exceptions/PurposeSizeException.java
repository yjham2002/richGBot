package exceptions;

import react.ClosedPurpose;

/**
 * Created by a on 2017-05-12.
 */
public class PurposeSizeException extends Exception {

    public PurposeSizeException(String msg){// 생성자
        super(msg);
    }

    public PurposeSizeException(){
        super("Size of components are inappropriate.");
    }

}
