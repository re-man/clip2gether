package com.hswgt.android.clip2gether.activitys;


import com.hswgt.android.clip2gether.HTTPRequest;
import com.hswgt.android.clip2gether.R;
import com.hswgt.android.clip2gether.LoginPreference;
import com.hswgt.android.clip2gether.Validate;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class activityRegister extends Activity {
    static public Dialog dialog;


    private void tryAuthRegister(String url){

        Dialog dlgActivityLogin_loginDialog;
        dlgActivityLogin_loginDialog = new Dialog(activityRegister.this);
        dlgActivityLogin_loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgActivityLogin_loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlgActivityLogin_loginDialog.setContentView(R.layout.dialog_auth);
        dlgActivityLogin_loginDialog.setCanceledOnTouchOutside(true);
        dlgActivityLogin_loginDialog.show();

        WebView webView = (WebView) dlgActivityLogin_loginDialog.findViewById(R.id.webView);
        webView.loadUrl(url);

        if (getResources().getDisplayMetrics().widthPixels < getResources().getDisplayMetrics().
                heightPixels) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        dlgActivityLogin_loginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        });


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String webUrl = view.getUrl();
                Log.e("asdasd",view.getUrl());
                if (webUrl == getResources().getString(R.string.urlConn_authControllURL)){
                    Intent intentActivityRegister_Login = new Intent(getApplicationContext(), activityLogin.class);
                    intentActivityRegister_Login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intentActivityRegister_Login);
                }
            }



        });


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.activity_register);


        TextView txtviewActivityRegister_loginScreen = (TextView) findViewById(R.id.link_to_login);
        Button btnActivityRegister_c2g = (Button) findViewById(R.id.btnActivityRegister_Clip2Gether);

        // Listening to Login Screen link
        txtviewActivityRegister_loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Switching to Login Screen/closing register screen
                Intent intentActivityRegister_Login = new Intent(getApplicationContext(), activityLogin.class);
                intentActivityRegister_Login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentActivityRegister_Login);
            }
        });

        Button btnActivityLogin_GPplus = (Button) findViewById(R.id.btnGplus);
        Button btnActivityLogin_Fb = (Button) findViewById(R.id.btnFB);


        btnActivityLogin_GPplus.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
              tryAuthRegister(getResources().getString(R.string.urlConn_authRegister_GPlus));
           }
        });

        btnActivityLogin_Fb.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            tryAuthRegister(getResources().getString(R.string.urlConn_authRegister_Facebook));
            }
        });


        btnActivityRegister_c2g.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                String strActivityRegister_Username = ((EditText) findViewById(R.id.reg_fullname)).getText().toString();
                String strActivityRegister_Email = ((EditText) findViewById(R.id.reg_email)).getText().toString();
                String strActivityRegister_Password = ((EditText) findViewById(R.id.reg_password)).getText().toString();

                if (Validate.isValidUsername(strActivityRegister_Username)){
                    if (Validate.isValidPassword(strActivityRegister_Password)){
                        if (Validate.isValidEmail(strActivityRegister_Email)){
                            register(strActivityRegister_Username,strActivityRegister_Email,strActivityRegister_Password);
                        }else{
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.validError_email), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.validError_password), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.validError_username), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void register(String tempUsername, String tempEmail, String tempPassword){

        dialog = new Dialog(activityRegister.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_register);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        // Switching to Login Screen/closing register screen
        final String STRACTIVITYREGISTER_USERNAME =  tempUsername;
        final String STRACTIVITYREGISTER_EMAIL = tempEmail;
        final String STRACTIVITYREGISTER_PASSWORD = tempPassword;

        // Wir pruefen die Logindaten - dazu benoetigen wir einen eigenen Thread
        Thread thrdActivityLogin_loginThread = new Thread(new Runnable() {

            public void run() {
                try {

                    String urlParameters = "email=" + STRACTIVITYREGISTER_EMAIL + "&username=" + STRACTIVITYREGISTER_USERNAME + "&password=" + STRACTIVITYREGISTER_PASSWORD + "&commkey=ASdno124KA123ASD230ASDm0";

                    HTTPRequest httpreqActivityLogin_LoginConnection = new HTTPRequest(getResources().getString(R.string.urlConn_registURL));
                    httpreqActivityLogin_LoginConnection.postRequest(urlParameters);

                    threadMsg(httpreqActivityLogin_LoginConnection.getResult());
                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {

                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {

                    String strActivityLogin_LoginResponse = msg.getData().getString("message");

                    // BOM entfernen falls vorhanden
                    strActivityLogin_LoginResponse = strActivityLogin_LoginResponse.trim().replace("\uFEFF", "");

                    // Wenn wir eine Antwort haben
                    if ((null != strActivityLogin_LoginResponse)) {

                        // Werten wir diese aus, dazu finden sich in der PHP Datei die verschiedenen Statuswerte
                        // TODO auflisten der Status beim Register

                        switch (strActivityLogin_LoginResponse) {

                            case "200":
                                new LoginPreference(getApplicationContext()).setLogin(STRACTIVITYREGISTER_USERNAME, STRACTIVITYREGISTER_PASSWORD,false);

                                Intent intentActivityLogin_camera = new Intent(getApplicationContext(), activityCamera.class);
                                intentActivityLogin_camera.putExtra("username",STRACTIVITYREGISTER_USERNAME);
                                intentActivityLogin_camera.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentActivityLogin_camera);
                                break;

                            case "201":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_error_UsernameInUse),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "202":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_error_noUsername),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "203":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_error_noPassword),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "204":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_error_noEmail),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "205":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_error_EMailInUse),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "206":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.registerActivity_connectionError),
                                        Toast.LENGTH_LONG).show();
                                break;

                            default:
                                // Sonst geben wir die Antwort des Scriptes aus...
                                Toast.makeText(
                                        getBaseContext(),
                                        "Error:" + strActivityLogin_LoginResponse,
                                        Toast.LENGTH_LONG).show();

                        }

                    } else {

                        // ALERT MESSAGE
                        Toast.makeText(
                                getBaseContext(),
                                getResources().getString(R.string.registerActivity_connectionError),
                                Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            };
        });


        if (isConnected()) {
            // Start Thread
            thrdActivityLogin_loginThread.start();

        } else {

            // ALERT MESSAGE
            Toast.makeText(
                    getBaseContext(),
                    getResources().getString(R.string.registerActivity_offlineNotification),
                    Toast.LENGTH_LONG).show();
        }

    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}