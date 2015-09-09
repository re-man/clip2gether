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
    WebView wbvGallery_webview;
    Dialog dlgGallery_progressDialog;

    @Override
    public void onBackPressed()
    {
        if(wbvGallery_webview.canGoBack()){
            wbvGallery_webview.goBack();
        }else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galerie);
        wbvGallery_webview = (WebView)findViewById(R.id.webview);
        wbvGallery_webview.setVisibility(View.GONE);


        dlgGallery_progressDialog = new Dialog(activityGallery.this);
        dlgGallery_progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgGallery_progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlgGallery_progressDialog.setContentView(R.layout.dialog_gallery);
        dlgGallery_progressDialog.setCanceledOnTouchOutside(false);
        dlgGallery_progressDialog.show();


        wbvGallery_webview.getSettings().setJavaScriptEnabled(true);
        wbvGallery_webview.loadUrl(getResources().getString(R.string.urlConn_webview_viewGallery));

        wbvGallery_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                wbvGallery_webview.setVisibility(View.VISIBLE);
                dlgGallery_progressDialog.hide();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                wbvGallery_webview.setVisibility(View.GONE);
                dlgGallery_progressDialog.show();

            }


        });
    }


}
