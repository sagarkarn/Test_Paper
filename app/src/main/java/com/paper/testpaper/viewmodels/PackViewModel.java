package com.paper.testpaper.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.paper.testpaper.AppRepo;
import com.paper.testpaper.models.QuestionPack;

import java.util.List;

public class PackViewModel extends AndroidViewModel {
    AppRepo repo;
    LiveData<List<QuestionPack>> data;
    public PackViewModel(@NonNull Application application) {
        super(application);
        repo = new AppRepo(application);
        data = repo.getAllPacks();
    }
    public void insert(QuestionPack pack){
        repo.insertPack(pack);
    }
    public void update(QuestionPack pack){
        repo.updatePack(pack);
    }
    public LiveData<List<QuestionPack>> getAllPack(){
        return data;
    }
    public QuestionPack getPackById(String id){
        return repo.getPackById(id);
    }

    public void delete(QuestionPack questionPack) {
        repo.deletePack(questionPack);
    }
}
