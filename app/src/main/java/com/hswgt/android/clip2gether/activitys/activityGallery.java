package com.hswgt.android.clip2gether.activitys;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hswgt.android.clip2gether.R;

/**
 * Created by Andreas on 11.06.2015.
 */
public class activityGallery extends Activity{
    WebView webview;
    Dialog dialog;

    @Override
    public void onBackPressed()
    {
        if(webview.canGoBack()){
            webview.goBack();
        }else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galerie);
        webview = (WebView)findViewById(R.id.webview);
        webview.setVisibility(View.GONE);


        dialog = new Dialog(activityGallery.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_gallery);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(getResources().getString(R.string.urlConn_webview_viewGallery));



        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.setVisibility(View.VISIBLE);
                dialog.hide();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webview.setVisibility(View.GONE);
                dialog.show();

            }



        });
    }


}
