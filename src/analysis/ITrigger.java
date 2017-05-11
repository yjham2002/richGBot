package analysis;

import nlp.ContextNLP;
import nlp.NaturalLanguageEngine;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by a on 2017-05-11.
 */
public interface ITrigger extends Serializable{
    public boolean run();
}
