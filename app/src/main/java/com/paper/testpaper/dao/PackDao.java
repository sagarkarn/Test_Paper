package com.paper.testpaper.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.paper.testpaper.models.QuestionPack;

import java.util.List;

@Dao
public interface PackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(QuestionPack pack);
    @Update
    void update(QuestionPack pack);
    @Query("SELECT * FROM questionpack")
    LiveData<List<QuestionPack>> getAllPack();
    @Query("SELECT * FROM questionpack WHERE id=:id")
    QuestionPack getPackById(String id);
    @Delete
    void delete(QuestionPack questionPack);
}
