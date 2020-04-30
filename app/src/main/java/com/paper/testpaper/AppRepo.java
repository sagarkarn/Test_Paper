package com.paper.testpaper;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.paper.testpaper.dao.PackDao;
import com.paper.testpaper.dao.QuestionDao;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;

import java.util.List;

public class AppRepo {
    private QuestionDao questionDao;
    private PackDao packDao;
    private LiveData<List<QuestionPack>> packData;
    public AppRepo(Context context){
        AppDatabase database = AppDatabase.getInstance(context);
        questionDao = database.getQuestionDao();
        packDao = database.getPackDao();
        packData = packDao.getAllPack();
    }
    /* Question Table functions */
    public void insertQuestion(final Question question){
        AppDatabase.databaseWriter.execute(new Runnable() {
            @Override
            public void run() {
                questionDao.insert(question);
            }
        });
    }

    public void updateQuestion(final List<Question> question){
        AppDatabase.databaseWriter.execute(new Runnable() {
            @Override
            public void run() {
                questionDao.update(question);
            }
        });
    }
    public List<Question> getQuestionByPackId(String id){
        return questionDao.getQuestionByPackId(id);
    }
    /* */

    /* question pack functions */
    public void insertPack(final QuestionPack questionPack){
        AppDatabase.databaseWriter.execute(new Runnable() {
            @Override
            public void run() {
                packDao.insert(questionPack);
            }
        });
    }
    public void updatePack(final QuestionPack questionPack){
        AppDatabase.databaseWriter.execute(new Runnable() {
            @Override
            public void run() {
                packDao.update(questionPack);
            }
        });
    }
    public LiveData<List<QuestionPack>> getAllPacks(){
        return packData;
    }
    public QuestionPack getPackById(String id){
        return packDao.getPackById(id);
    }

    public void deletePack(final QuestionPack questionPack) {
        AppDatabase.databaseWriter.execute(new Runnable() {
            @Override
            public void run() {
                packDao.delete(questionPack);
            }
        });
    }
}
