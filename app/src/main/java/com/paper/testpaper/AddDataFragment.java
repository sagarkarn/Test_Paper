 package com.paper.testpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddDataFragment extends Fragment {

    private final int REQUEST_CODE = 2;

    public AddDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addDataBtn = view.findViewById(R.id.add_json_btn);
        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Uri uri = Objects.requireNonNull(data).getData();
                String jsonData = getJsonFromFile(uri);
                writeJsonToDb(jsonData);
            }
        }
    }

    private void writeJsonToDb(String jsonData) {
        Gson gson = new Gson();
        HashMap<String,Objects> map= gson.fromJson(jsonData,new TypeToken<HashMap<String,Object>>(){}.getType());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("question_pack").push();
        ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
//                    Toast.makeText(getActivity(),"Added Successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getJsonFromFile(Uri uri) {
        StringBuilder builder = new StringBuilder();
        try{
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
             String line = "";
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
