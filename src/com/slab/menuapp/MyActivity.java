package com.slab.menuapp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://www.looinsoft.com/slab/menuimg.jsp");
    }

    @Override
    public void onResume() {
        super.onResume();
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://www.looinsoft.com/slab/menuimg.jsp");
    }
}
