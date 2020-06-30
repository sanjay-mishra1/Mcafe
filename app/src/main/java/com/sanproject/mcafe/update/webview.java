package com.sanproject.mcafe.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanproject.mcafe.R;

import androidx.annotation.Nullable;

public class webview extends Activity {
    public WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webView=findViewById(R.id.webview);
        webView.setVisibility(View.GONE);
        Intent intent=getIntent();
        final ProgressBar progressBar=findViewById(R.id.progressBar);
        webView.setWebViewClient(new Browse(intent.getStringExtra("URL")));
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                 progressBar.setProgress(newProgress);
            }
        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(intent.getStringExtra("URL"));
    }

    public void onclickback(View view) {
        finish();
    }

    private class Browse extends WebViewClient{
        String url;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            TextView textView=findViewById(R.id.toolbar);
            textView.setText(view.getTitle());
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.evaluateJavascript("javascript:(function(){ " +
                    "document.getElementsByTagName('nav')[0].remove();\n" +
                    "document.getElementsByTagName('footer')[0].remove();" +
                    "return (s);})();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value!=null)
                   webview.this.webView.setVisibility(View.VISIBLE);
                    else {
                        Toast.makeText(webview.this,"Error occurred",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
             return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
