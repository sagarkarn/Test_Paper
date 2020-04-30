package com.paper.testpaper.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QuestionPack {
    @NonNull
    @PrimaryKey
    public final String id;

    public final String name;
    public int timeTaken;
    public int totalMarks;
    public boolean isAnswered;
    public QuestionPack(@NonNull String id, String name, int timeTaken, int totalMarks, boolean isAnswered) {
        this.id = id;
        this.name = name;
        this.timeTaken = timeTaken;
        this.totalMarks = totalMarks;
        this.isAnswered = isAnswered;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ " + id + " , " + name + " , " + timeTaken + " , " + totalMarks + " , "+isAnswered+" ]";
    }
}
