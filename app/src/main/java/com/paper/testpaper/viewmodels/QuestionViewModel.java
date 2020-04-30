package com.paper.testpaper.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.paper.testpaper.AppRepo;
import com.paper.testpaper.models.Question;

import java.util.List;

public class QuestionViewModel extends AndroidViewModel {
    private AppRepo repo;
    public QuestionViewModel(@NonNull Application application) {
        super(application);
        repo = new AppRepo(application);
    }
    public void insert(Question question){
        repo.insertQuestion(question);
    }
    public void update(List<Question> question){
        repo.updateQuestion(question);
    }
    public List<Question> getQuestionByPackId(String id){
        return repo.getQuestionByPackId(id);
    }
}
