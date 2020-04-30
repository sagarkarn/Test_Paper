package com.paper.testpaper;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.paper.testpaper.models.Question;
import com.paper.testpaper.viewmodels.PackViewModel;
import com.paper.testpaper.viewmodels.QuestionViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalysisFragment extends Fragment {

    private Spinner spinner;
    private WebView webView;
    private QuestionViewModel questionViewModel;
    private PackViewModel packViewModel;

    public AnalysisFragment() {
        // Required empty public constructor
    }

    private ActionMode actionMode = null;
    private AppJavascriptInterface javascriptInterface;
    private String id;
    private List<Question> questions;
    private List<Question> correctQuestions = new ArrayList<>();
    private List<Question> unAttemptedQuestions = new ArrayList<>();
    private List<Question> wrongQuestions = new ArrayList<>();
    private BottomSheetDialog dialog = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        spinner = view.findViewById(R.id.analysis_sort);
        webView = view.findViewById(R.id.analysis_web_view);
        javascriptInterface = new AppJavascriptInterface(getContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(javascriptInterface,"Android");


        TextView textView = new TextView(getContext());
        textView.setText("Hello in bottom");
        dialog = new BottomSheetDialog(requireContext());


        if (getArguments() != null) {
            id = getArguments().getString("id");
        }
        final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.selece_menu,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if(item.getItemId() == R.id.wiki){
                    webView.loadUrl("javascript: getSelectedText()");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new DialogTextLoaderAsync(javascriptInterface.getSelectedText()).execute();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                dialog.dismiss();
            }
        };

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(actionMode != null) {
                    return false;
                }
                actionMode = requireActivity().startActionMode(actionModeCallback);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<CharSequence> stringArrayAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.spinner,android.R.layout.simple_spinner_item);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        String data = getData(questions);

                        webView.loadDataWithBaseURL("https://",data,"text/html","utf-8","");
                        break;
                    case 1:
                        data = getData(wrongQuestions);
                        data +=getData(unAttemptedQuestions);
                        data +=getData(correctQuestions);
                        webView.loadDataWithBaseURL("https://",data,"text/html","utf-8","");
                        break;
                    case 2:
                        data = getData(unAttemptedQuestions);
                        data +=getData(wrongQuestions);
                        data +=getData(correctQuestions);
                        webView.loadDataWithBaseURL("https://",data,"text/html","utf-8","");
                        break;
                    case 3:

                        data = getData(correctQuestions);
                        data +=getData(unAttemptedQuestions);
                        data +=getData(wrongQuestions);
                        webView.loadDataWithBaseURL("https://",data,"text/html","utf-8","");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        packViewModel = new ViewModelProvider(this).get(PackViewModel.class);

        new LoaderAsync().execute();

    }


    @SuppressLint("StaticFieldLeak")
    class LoaderAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            questions = questionViewModel.getQuestionByPackId(id);
            for(int i = 0; i < questions.size(); i++){
                Question question = questions.get(i);
                if(question.givenAnswer == question.answer){
                    correctQuestions.add(question);
                }
                else if(question.givenAnswer == -1){
                    unAttemptedQuestions.add(question);
                }else wrongQuestions.add(question);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String data = getData(questions);
            webView.loadDataWithBaseURL("https://",data,"text/html","utf-8","");
        }
    }

    private String getData(List<Question> questions) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head>").append(getCss()).append("</head><body onmouseup=\'getSelectedText()\'>");
        for(int i = 0; i < questions.size(); i++){
            final Question question = questions.get(i);
            String color = "";
            if (question.answer == question.givenAnswer){
                color = "#dcedc7";
            }
            else if(question.givenAnswer == -1){
                color = "#d6cbc7";
            }
            else color = "#ffcdd2";
            builder.append("<div class=\'que-ans-box\' style=\"background-color:").append(color).append("\" >");
            builder.append(replaceTag(question.category)).append(" - ").append(question.questionNo).append("<br>")
                    .append(question.comprehension.length() > 1?"<b><u>Comprehension: </u></b>":"").append("<br>")
                    .append(replaceTag(question.comprehension)).append("<br>")
                    .append(replaceTag(question.question)).append("<br>")
                    .append(getOptionRadio(replaceTag(question.options),question.answer,question.givenAnswer)).append("<br>")
                    .append("<hr>")
                    .append("<b>Solution:</b>")
                    .append(replaceTag(question.solution))
                    .append("</div>");

        }
        builder.append(getScript())
                .append("</body></html>");
        return builder.toString();
    }

    private String getCss(){
        return "<style>" +
                "*{" +
                "word-wrap: break-word;" +
                "}" +
                ".option{" +
                "padding:20px;" +
                "margin-top:8px;" +
                "" +
                "}" +
                ".wrong{" +
                "background-color: #EE0000;" +
                "color:#EEEEEE" +
                "}" +
                ".right{" +
                "background-color:#00FF00;" +
                "}" +
                ".que-ans-box{" +
                "border: 2px solid #AAAAAA;" +
                "padding: 8px;" +
                "margin-bottom:8px;" +
                "}" +
                "table{" +
                "border: 0px solid #FFFFFF;"+
                "}" +
                "tr:nth-child(even){" +

                "background-color: #f2f2f2;" +
                "}" +
                "th,td{" +
                "border: 0px solid #FFFFFF;"+
//                "border-bottom: 1px solid #ddd" +
                "}" +
                "tr:first-child{" +
                "background-color:#0984e3;" +
                "color:#fff" +
                "}" +
                "img{" +
                "max-width: 100%;" +
                "}" +
                "</style>";
    }
    private String getScript(){
        return "<script> var t = document.getElementsByTagName(\'table\');\n" +
                "                for(var i = 0; i < t.length; i++){\n" +
                "                t[i].removeAttribute(\"style\");" +
                "                var parent = t[i].parentElement;\n" +
                "                var divN = document.createElement(\"div\");\n" +
                "                divN.setAttribute(\"style\",\"overflow-x:auto;\");\n" +
                "                divN.appendChild(t[i]);\n" +
                "                parent.appendChild(divN);\n" +
                "                }</script>" +
                "<script src=\"File:///android_asset/selection.js\"></script>" +
                "<script src=\"File:///android_asset/tex-mml-chtml.js\" id=\"MathJax-script\" async></script>";
    }

    private String getOptionRadio(String options,int answer,int givenAnswer) {

        String[] s = options.split("__option_concat__");
        for(int i = 0; i < s.length; i++){
            s[i] = replaceTag(s[i]);
        }
        String[] checked = new String[4];
        checked[0] = checked[1] = checked[2] = checked[3] = "";

        checked[answer-1] = "right";

        if (givenAnswer != -1 && answer != givenAnswer){
            checked[givenAnswer-1] = "wrong";
        }

        return "<div class=\""+checked[0]+" option\" >"+s[0]+"</div>"+
                "<div class=\""+checked[1]+" option\" >"+s[1]+"</div>"+
                "<div class=\""+checked[2]+" option\" >"+s[2]+"</div>"+
                "<div class=\""+checked[3]+" option\" >"+s[3]+"</div>";
    }
    private String replaceTag(String str) {
        return str.replaceAll("__o_a_b__", "<")
                .replaceAll("__c_a_b__", ">")
                .replaceAll("__s_q__", "\'")
                .replaceAll("__d_q__", "\"")
                .replaceAll("src=\"//", "src=\"https://");
    }


    @SuppressLint("StaticFieldLeak")
     class DialogTextLoaderAsync extends AsyncTask<Void,Void,Void>{
        String title;
        String extract;
        String topic;
        DialogTextLoaderAsync(String selectedText) {
            topic = selectedText;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String stringUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&generator=prefixsearch" +
                    "&redirects=1&converttitles=1&formatversion=2&exintro=1&explaintext=1&gpssearch="+topic;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(stringUrl);
                URLConnection conn = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONObject queryObject = jsonObject.getJSONObject("query");
                JSONArray pages = queryObject.getJSONArray("pages");
                JSONObject page = pages.getJSONObject(0);
                title = page.getString("title");
                extract = page.getString("extract");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                @SuppressLint("InflateParams")
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.wikipidia_search, null, false);
                TextView titleTv = dialogView.findViewById(R.id.search_header);
                TextView contentTv = dialogView.findViewById(R.id.search_content);

                titleTv.setText(title);
                contentTv.setText(extract);
                dialog.setContentView(dialogView);
                dialog.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
