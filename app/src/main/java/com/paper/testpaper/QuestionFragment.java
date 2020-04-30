package com.paper.testpaper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.paper.testpaper.models.Question;
import com.paper.testpaper.models.QuestionPack;
import com.paper.testpaper.viewmodels.PackViewModel;
import com.paper.testpaper.viewmodels.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private TextView timer;
    private Button nextBtn, prevBtn;
    private WebView questionWebView;

    private QuestionViewModel viewModel;
    private PackViewModel packViewModel;

    private AppJavascriptInterface scriptInterface;
    private CountDownTimer countDownTimer;

    private List<Question> questions;
    private int counter = 0;
    private String id;
    private long finishedTime = 3600000;
    private boolean isCancel = false;
    private QuestionPack pack;

    AlertDialog dialog = null;
    TextView dialogTextView = null;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_question, container, false);
        timer = view.findViewById(R.id.count_down_timer);
        nextBtn = view.findViewById(R.id.next_button);
        prevBtn = view.findViewById(R.id.prev_button);
        questionWebView = view.findViewById(R.id.question_web_view);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        id = getArguments() != null ? getArguments().getString("id") : null;
        viewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        packViewModel = new ViewModelProvider(this).get(PackViewModel.class);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setPadding(0,50,0,50);
        ProgressBar progressBar = new ProgressBar(getContext());
        layout.addView(progressBar);
        dialogTextView = new TextView(getContext());
        dialogTextView.setText("Loading...");
        layout.addView(dialogTextView);
        dialog = new AlertDialog.Builder(getContext())
                .setView(layout)
                .setCancelable(false)
                .show();

        scriptInterface = new AppJavascriptInterface(getContext());
        questionWebView.getSettings().setJavaScriptEnabled(true);
        questionWebView.getSettings().setDomStorageEnabled(true);
        questionWebView.getSettings().setDatabaseEnabled(true);

        questionWebView.addJavascriptInterface(scriptInterface, "Android");
        questionWebView.setWebViewClient(new WebViewClient());

        prevBtn.setEnabled(false);
        if(savedInstanceState == null) {
            new QuestionLoaderAsync().execute();
        }
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View eView) {
                if (counter < questions.size()) {
                    Question question = questions.get(counter);
                    question.givenAnswer = scriptInterface.getChecked();
                    questions.set(counter, question);
                    scriptInterface.clearChecked();
                    counter++;
                    prevBtn.setEnabled(true);

                    if (counter < questions.size()) {
                        setQuestionAt(counter);
                    }
                    if (counter >= questions.size()) {
                        submit(view);
                    }
                    if (counter == 99) {
                        nextBtn.setText("Submit");
                    } else nextBtn.setText("Next");
                }
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                if (counter == 0) {
                    prevBtn.setEnabled(false);
                }
                if (counter < questions.size()) {
                    setQuestionAt(counter);
                }

            }
        });
        /* @callback on back handle */
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setTitle("Exit")
                        .setMessage("Data will not stored\nAre you sure?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove();
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            }
                        })
                        .setNegativeButton("Continue", null)
                        .show();


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void submit(final View view) {
        if(questions != null) {
            viewModel.update(questions);
            pack.timeTaken = (int) finishedTime;
            pack.isAnswered = true;
            packViewModel.update(pack);
            dialogTextView.setText("Submitting");
            dialog.show();
            countDownTimer.cancel();
            new CountDownTimer(3000, 1000) {

                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                    NavOptions options = new NavOptions.Builder()
                            .setPopUpTo(R.id.mainPageFragment, false)
                            .build();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    Navigation.findNavController(view).navigate(R.id.action_questionFragment_to_resultFragment, bundle, options);
                }
            }.start();
        }
    }

    private void setQuestionAt(int counter)
    {
        if(counter < 100) {
            Question question = questions.get(counter);
            question = toHtml(question);
            String builder = "<html><head>" +
                    getCss() +
                    "</head><body>" +
                    question.category + " - " + question.questionNo +
                    "<br>" +
                    (question.comprehension.length() > 0 ? "<i>comprehension:</i><br>" : "") +
                    question.comprehension +
                    "<br><strong>Question: </strong><br>" +
                    question.question +
                    "<br><br>" +
                    question.options +
                    getScript() +
                    "</body></html>";
            questionWebView.loadDataWithBaseURL("https://", builder, "text/html", "utf-8", "");
        }

    }

    private Question toHtml(Question question) {
        String que = replaceTag(question.question);
        String solution = replaceTag(question.solution);
        String comprehension = replaceTag(question.comprehension);
        String op = replaceTag(question.options);
        String opWithRadio = optionRadio(op.split("__option_concat__"),question.givenAnswer);
        return new Question(question.id, que, opWithRadio, question.answer, question.givenAnswer, solution, question.packId, comprehension, question.category, question.questionNo);
    }

    private String optionRadio(String[] s,int answer) {
        String checked1,checked2,checked3,checked4;
        checked1 = checked2 = checked3 = checked4 = "";
        switch (answer){
            case 1:
                checked1 = "checked";
                break;
            case 2:
                checked2 = "checked";
                break;
            case 3:
                checked3 = "checked";
                break;
            case 4:
                checked4 = "checked";
                break;
        }
        return "<label class=\"container\">" + s[0] + "<input type=\"radio\" onClick=\"setChecked(1)\"  name=\"radio\" " + checked1 + "><span class=\"checkmark\"></span></label>" +
                "<label class=\"container\" >" + s[1] + "<input type=\"radio\" onClick=\"setChecked(2)\" name=\"radio\" " + checked2 + "><span class=\"checkmark\"></span></label>" +

                "<label class=\"container\" >" + s[2] +
                "  <input type=\"radio\" onClick=\"setChecked(3)\" name=\"radio\" " + checked3 + ">" +
                "  <span class=\"checkmark\"></span>" +
                "</label>" +

                "<label class=\"container\" >" + s[3] +
                "  <input type=\"radio\" onClick=\"setChecked(4)\" name=\"radio\" " + checked4 + ">" +
                "  <span class=\"checkmark\"></span>" +
                "</label>";
    }

    private String getCss() {
        return "<style>" +
                ".container {" +
                "  display: block;" +
                "  position: relative;" +
                "  padding-left: 35px;" +
                "  margin-bottom: 12px;" +
                "  cursor: pointer;" +
                "  font-size: 22px;" +
                "  -webkit-user-select: none;" +
                "  -moz-user-select: none;" +
                "  -ms-user-select: none;" +
                "  user-select: none;" +
                "}" +
                "" +
                "/* Hide the browser's default radio button */" +
                ".container input {" +
                "  position: absolute;" +
                "  opacity: 0;" +
                "  cursor: pointer;" +
                "}" +
                "" +
                "/* Create a custom radio button */" +
                ".checkmark {" +
                "  position: absolute;" +
                "  top: 0;" +
                "  left: 0;" +
                "  height: 25px;" +
                "  width: 25px;" +
                "  background-color: #eee;" +
                "  border-radius: 50%;" +
                "}" +
                "" +
                "/* On mouse-over, add a grey background color */" +
                ".container:hover input ~ .checkmark {" +
                "  background-color: #ccc;" +
                "}" +
                "" +
                "/* When the radio button is checked, add a blue background */" +
                ".container input:checked ~ .checkmark {" +
                "  background-color: #2196F3;" +
                "}" +
                "" +
                "/* Create the indicator (the dot/circle - hidden when not checked) */" +
                ".checkmark:after {" +
                "  content: \"\";" +
                "  position: absolute;" +
                "  display: none;" +
                "}" +
                "" +
                "/* Show the indicator (dot/circle) when checked */" +
                ".container input:checked ~ .checkmark:after {" +
                "  display: block;" +
                "}" +
                "" +
                "/* Style the indicator (dot/circle) */" +
                ".container .checkmark:after {" +
                " \ttop: 9px;" +
                "\tleft: 9px;" +
                "\twidth: 8px;" +
                "\theight: 8px;" +
                "\tborder-radius: 50%;" +
                "\tbackground: white;" +
                "}" +
                "img{" +
                "max-width: 100%;" +
                "}" +
                "</style>";
    }

    private String getScript() {
        return "<script src=\"File:///android_asset/question_radio.js\">" +
                "</script>" +
                "<script src=\"File:///android_asset/tex-mml-chtml.js\" id=\"MathJax-script\" async></script>";
    }

    private String replaceTag(String str) {
        return str.replaceAll("__o_a_b__", "<")
                .replaceAll("__c_a_b__", ">")
                .replaceAll("__s_q__", "\'")
                .replaceAll("__d_q__", "\"")
                .replaceAll("src=\"//", "src=\"https://");
    }

    private void timerCountDown(final long startTime) {
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(startTime, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTick(long l) {
                finishedTime = l;
                if(l>60000){
                    timer.setText(String.format("%02dM:%02dS",l / 60000,(l % 60000) / 1000));
                }
                else {
                    timer.setText(String.format("%02dS",(l % 60000) / 1000));
                }
            }

            @Override
            public void onFinish() {
                if(!isCancel) {
                    submit(requireView());
                }
            }
        };
        countDownTimer.start();

    }

    @SuppressLint("StaticFieldLeak")
    class QuestionLoaderAsync extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {

            questions = viewModel.getQuestionByPackId(id);
            pack = packViewModel.getPackById(id);
            for(int i = 0; i < questions.size(); i++){
                Question question = questions.get(i);
                question.givenAnswer = -1;
                questions.set(i,question);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setQuestionAt(counter);
            new CountDownTimer(3000,1000){

                @Override
                public void onTick(long l) {
                    dialogTextView.setText("Your Test will start in "+l/1000+"s");
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                    timerCountDown(3600000);
                }
            }.start();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter",counter);
        ArrayList<String> questionString = new ArrayList<>();
        for(Question question:questions){
            questionString.add(question.toString());
        }
        outState.putStringArrayList("questionString", questionString);
        outState.putLong("finishedTime",finishedTime);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
            ArrayList<String> questionString = savedInstanceState.getStringArrayList("questionString");
            questions = new ArrayList<>();
            assert questionString != null;
            for(int i = 0; i < questionString.size(); i++){
                questions.add(i,Question.toQuestion(questionString.get(i)));
            }
            finishedTime = savedInstanceState.getLong("finishedTime");
            setQuestionAt(counter);
            timerCountDown(finishedTime);
            if(counter != 0){
                prevBtn.setEnabled(true);
            }
            dialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isCancel = true;
        countDownTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        isCancel = false;
        timerCountDown(finishedTime);
    }
}
