package com.paper.testpaper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;
import com.paper.testpaper.viewmodels.PackViewModel;
import com.paper.testpaper.viewmodels.QuestionViewModel;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment {

    public MainPageFragment() {
        // Required empty public constructor
    }

    private PackViewModel packViewModel;
    private QuestionViewModel questionViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        packViewModel = new ViewModelProvider(this).get(PackViewModel.class);
        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.pack_recycler_view);
        final PackAdapter adapter = new PackAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);



        packViewModel.getAllPack().observe(getViewLifecycleOwner(), new Observer<List<QuestionPack>>() {
            @Override
            public void onChanged(List<QuestionPack> questionPacks) {
                adapter.setAdapter(questionPacks);
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("question_pack");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                childAdded(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void childAdded(DataSnapshot snapshot){
        new DataBaseAsync(snapshot,getContext()).execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_data_menu_item){
            Navigation.findNavController(requireView()).navigate(R.id.action_mainPageFragment_to_addDataFragment);
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("StaticFieldLeak")
    class DataBaseAsync extends AsyncTask<Void, Void, Void> {
        private DataSnapshot snapshot;
        @SuppressLint("StaticFieldLeak")
        DataBaseAsync(DataSnapshot snapshot, Context context) {
            this.snapshot = snapshot;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String id ="";
            if (snapshot.exists()) {
                id = snapshot.getKey();
                if (packViewModel.getPackById(snapshot.getKey()) == null) {
                    String name = snapshot.child("name").getValue(String.class);
                    assert id != null;
                    QuestionPack pack = new QuestionPack(id, name, -1, -1, false);
                    packViewModel.insert(pack);
                    addQuestion(snapshot,id);
                    Log.d("DataBaseAsync", pack.toString());
                }


            }
            return null;
        }


        private void addQuestion(DataSnapshot snapshot, String id) {
            Log.d("snapshotQuestion",snapshot.getKey());
            if (snapshot.hasChild("questions")) {
                Log.d("snapshotQuestion",snapshot.getKey());
                for (DataSnapshot category : snapshot.child("questions").getChildren()) {
                    Log.d("category",category.getKey());
                    if (category.hasChildren()) {
                        for (DataSnapshot questionCount : category.getChildren()) {
                            Log.d("questionCount",questionCount.getKey());
                            if (category.hasChildren()) {
                                Log.d("questionCount",questionCount.getKey());
                                String que = questionCount.child("question").getValue(String.class);
                                String comprehension = "";
                                if(questionCount.hasChild("comprehension")) {
                                    comprehension = questionCount.child("comprehension").getValue(String.class);
                                }
                                String options = questionCount.child("options").getValue(String.class);
                                String solution = questionCount.child("solution").getValue(String.class);
                                int answer = Integer.parseInt(questionCount.child("answer").getValue().toString());
                                String queId = category.getKey() +"0"+ questionCount.getKey();

                                Question question1 = new Question(queId, que, options, answer, -1, solution, id, comprehension, category.getKey(), Integer.parseInt(Objects.requireNonNull(questionCount.getKey())));
                                questionViewModel.insert(question1);
                                Log.d("questionq",question1.toString());

                            }
                        }
                    }
                }
            }
        }
    }

}
