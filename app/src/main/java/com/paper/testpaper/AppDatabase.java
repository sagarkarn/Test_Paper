package com.paper.testpaper;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.paper.testpaper.dao.PackDao;
import com.paper.testpaper.dao.QuestionDao;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Question.class, QuestionPack.class},version = 1)
abstract class AppDatabase extends RoomDatabase {
    abstract QuestionDao getQuestionDao();
    abstract PackDao getPackDao();
    private static volatile AppDatabase instance;
    final static ExecutorService databaseWriter = Executors.newFixedThreadPool(4);
    static AppDatabase getInstance(final Context context){
        if(instance == null){
            synchronized(AppDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
