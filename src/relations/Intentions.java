package relations;

import kr.co.shineware.util.common.model.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 함의진
 * 문장 단위를 캡슐화하는 클래스.
 */
public class Intentions {
    private List<Pair<String, String>> determiners;
    private List<Pair<String, String>> subjects;
    private List<Pair<String, String>> verbs;
    private List<Pair<String, String>> objects;
    private List<Pair<String, String>> adjectives;
    private List<Pair<String, String>> adverbs;

    public Intentions(List<Pair<String, String>> determiners, List<Pair<String, String>> subjects, List<Pair<String, String>> verbs, List<Pair<String, String>> objects, List<Pair<String, String>> adjectives, List<Pair<String, String>> adverbs){
        this.determiners = determiners;
        this.subjects = subjects;
        this.verbs = verbs;
        this.objects = objects;
        this.adjectives = adjectives;
        this.adverbs = adverbs;
    }

    public Intentions(List<Pair<String, String>> determiners, List<Pair<String, String>> subjects, List<Pair<String, String>> verbs, List<Pair<String, String>> objects){
        this.determiners = determiners;
        this.subjects = subjects;
        this.verbs = verbs;
        this.objects = objects;
        this.adjectives = new ArrayList<>();
        this.adverbs = new ArrayList<>();
    }

    public Intentions(List<Pair<String, String>> subjects, List<Pair<String, String>> verbs, List<Pair<String, String>> objects){
        this.subjects = subjects;
        this.verbs = verbs;
        this.objects = objects;
        this.adjectives = new ArrayList<>();
        this.adverbs = new ArrayList<>();
    }

    public Intentions(){
        this.determiners = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.verbs = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.adjectives = new ArrayList<>();
        this.adverbs = new ArrayList<>();
    }

    public List<Pair<String, String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Pair<String, String>> subjects) {
        this.subjects = subjects;
    }

    public List<Pair<String, String>> getVerbs() {
        return verbs;
    }

    public void setVerbs(List<Pair<String, String>> verbs) {
        this.verbs = verbs;
    }

    public List<Pair<String, String>> getObjects() {
        return objects;
    }

    public void setObjects(List<Pair<String, String>> objects) {
        this.objects = objects;
    }

    public List<Pair<String, String>> getAdjectives() {
        return adjectives;
    }

    public void setAdjectives(List<Pair<String, String>> adjectives) {
        this.adjectives = adjectives;
    }

    public List<Pair<String, String>> getAdverbs() {
        return adverbs;
    }

    public void setAdverbs(List<Pair<String, String>> adverbs) {
        this.adverbs = adverbs;
    }

    public List<Pair<String, String>> getDeterminers() {
        return determiners;
    }

    public void setDeterminers(List<Pair<String, String>> determiners) {
        this.determiners = determiners;
    }
}
