package com.paper.testpaper;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

class AppJavascriptInterface {
    private Context context;
    private String checked = "-1";
    private String selectedText = "";
    AppJavascriptInterface(Context context){
        this.context = context;
    }
    @JavascriptInterface
    public void setChecked(String num){
        checked = num;
    }
    public int getChecked(){
        return Integer.parseInt(checked);
    }
    public void clearChecked(){
        checked = "-1";
    }

    @JavascriptInterface
    public void setSelectedText(String text){
        selectedText = text;
    }
    public String getSelectedText(){
        return selectedText;
    }
}
