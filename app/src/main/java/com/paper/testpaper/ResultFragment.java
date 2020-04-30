package com.paper.testpaper;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.resources.TextAppearance;
import com.google.android.material.tabs.TabLayout;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;
import com.paper.testpaper.viewmodels.PackViewModel;
import com.paper.testpaper.viewmodels.QuestionViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    public ResultFragment() {
        // Required empty public constructor
    }

    private TextView timeTakenTv,totalMarksTv;
    private TableLayout table;
    private Button btnAnswer;

    private QuestionViewModel questionViewModel;
    private PackViewModel packViewModel;
    private TableRow row;
    private String id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }
        timeTakenTv = view.findViewById(R.id.result_time_taken);
        table = view.findViewById(R.id.result_table);
        row = view.findViewById(R.id.table_row);
        totalMarksTv = view.findViewById(R.id.totalMark);
        btnAnswer = view.findViewById(R.id.check_your_answer);

        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        packViewModel = new ViewModelProvider(this).get(PackViewModel.class);
        final NavController controller = Navigation.findNavController(view);
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                controller.navigate(R.id.action_resultFragment_to_analysisFragment,bundle);
            }
        });

        new UiLoader().execute();


    }
    int[] getResult(List<Question> questions, String category){

        int[] answerList = new int[3];
        for(Question question:questions){
            if(question.category.equalsIgnoreCase(category)){
                if(question.givenAnswer==question.answer){
                    answerList[2]++;
                    Log.d("RuslultFragment",answerList[0]+"");
                }
                if(question.givenAnswer == -1){
                    answerList[1]++;
                }
                else {
                    answerList[0]++;
                }
            }
        }
        return answerList;

    }
    @SuppressLint("SetTextI18n")
    private void updateUi(int[] ints, String category){
        TableRow tableRow = new TableRow(this.getContext());
        TextView tv = new TextView(this.getContext());
        if(category.equalsIgnoreCase("Total")){
            tv.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6);
            tv.setTextSize(18);
        }
        tv.setText(category);
        tableRow.addView(tv);
        for(int i = 0; i < 3; i++){
            TextView textView = new TextView(getContext());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(0,20,0,20);
            if(category.equalsIgnoreCase("Total")){
                textView.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6);
            }
            textView.setText(ints[i]+"");
            tableRow.addView(textView);
        }
        TableRow row1 = new TableRow(this.getContext());
        for(int i = 0; i < 4; i++) {
            TextView textView1 = new TextView(this.getContext());
            textView1.setHeight(2);
            textView1.setBackgroundColor(Color.BLACK);
            row1.addView(textView1);
        }
        table.addView(tableRow);
        table.addView(row1);
    }
    @SuppressLint("StaticFieldLeak")
    class UiLoader extends AsyncTask<Void,Void,Void>{
        int[] english;
        int[] awareness;
        int[] intelligence;
        int[] aptitude;
        int[] total;
        QuestionPack questionPack;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Question> questions = questionViewModel.getQuestionByPackId(id);
            questionPack = packViewModel.getPackById(id);
            english = getResult(questions,"English Language");
            awareness = getResult(questions, "General Awareness");
            intelligence = getResult(questions,"General Intelligence");
            aptitude = getResult(questions,"Quantitative Aptitude");
            total = new int[]{english[0]+awareness[0]+intelligence[0]+aptitude[0],
                    english[1]+awareness[1]+intelligence[1]+aptitude[1],
                    english[2]+awareness[2]+intelligence[2]+aptitude[2]};
            return null;
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUi(english,"English Language");
            updateUi(awareness,"General Awareness");
            updateUi(intelligence,"General Intelligence");
            updateUi(aptitude,"Quantitative Aptitude");
            updateUi(total,"Total");
            int totalScore =  (total[2]*2)-total[0];
            int timeTaken = 3600000-questionPack.timeTaken;
            int minute = timeTaken/60000;
            int sec = (timeTaken%60000)/1000;

            timeTakenTv.setText(String.format("%02dM:%02dS",minute,sec));

            questionPack.totalMarks = totalScore;
            packViewModel.update(questionPack);
            totalMarksTv.setText("TotalMarks: " + totalScore+"/200\n"+totalScore/2+"%");
        }
    }

}
