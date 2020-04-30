package com.paper.testpaper.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.paper.testpaper.models.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question question);

    @Update
    void update(List<Question> question);



    @Query("SELECT * FROM question WHERE packId = :packId ORDER BY category,questionNo ASC")
    List<Question> getQuestionByPackId(String packId);

}
