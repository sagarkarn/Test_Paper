package com.paper.testpaper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.database.DataSnapshot;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;
import com.paper.testpaper.viewmodels.PackViewModel;
import com.paper.testpaper.viewmodels.QuestionViewModel;

import java.util.Objects;


