package com.paper.testpaper.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;

@Entity
public class Question {
    @NonNull
    @PrimaryKey
    public final String id;
    public final String question;
    public final String options;
    public int answer;
    public int givenAnswer;
    public final String solution;
    public final String packId;
    public final String comprehension;
    public final String category;
    public final int questionNo;
    public Question(@NonNull String id, String question, String options, int answer,
                    int givenAnswer, String solution, String packId, String comprehension, String category, int questionNo) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.givenAnswer = givenAnswer;
        this.solution = solution;
        this.packId = packId;
        this.comprehension = comprehension;
        this.category = category;
        this.questionNo = questionNo;
    }

    @NonNull
    @Override
    public String toString() {

        return id + "_divider_" + question + "_divider_" + options + "_divider_" + answer + "_divider_" + givenAnswer + "_divider_" + solution + "_divider_" + packId + "_divider_" +
                comprehension + "_divider_" + category + "_divider_" + questionNo;
    }

    public static Question toQuestion(String strings){
        List<String> qu = Arrays.asList(strings.split("_divider_"));
        return new Question(qu.get(0),
                qu.get(1),
                qu.get(2),
                Integer.parseInt(qu.get(3)),
                Integer.parseInt(qu.get(4)),
                qu.get(5),
                qu.get(6),
                qu.get(7),
                qu.get(8),
                Integer.parseInt(qu.get(9))
        );
    }
}
