package com.hswgt.android.clip2gether.activitys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hswgt.android.clip2gether.HTTPRequest;
import com.hswgt.android.clip2gether.R;
import com.hswgt.android.clip2gether.LoginPreference;
import com.hswgt.android.clip2gether.Validate;

public class activityLogin extends Activity {
    // Die Variable intActivityLogin_LOGIN_NOT_ALLOWED wird benutzt, um zu prüfen ob man sich noch einlogen DARF ( wenn der cancel button gedrückt wird )
    static public int intActivityLogin_LOGIN_NOT_ALLOWED = 0;
    static public Dialog dlgActivityLogin_loginDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView txtviewActivityLogin_registerScreen = (TextView) findViewById(R.id.link_to_register);


        Button btnActivityLogin_c2g = (Button) findViewById(R.id.btnActivityLogin_Clip2Gether);

        CheckBox chbxActivityLogin_autologin = (CheckBox) findViewById(R.id.checkBox);


        // Erstelle ein LoginPreference Object womit gespeicherte userdaten abgerufen werden können, für einen möglichen AutoLogin
        LoginPreference lpActivityLogin_LoginPreference = new LoginPreference(getApplicationContext());


        // Setze die Logindaten vom letzten mal
        ((EditText) findViewById(R.id.email)).setText(lpActivityLogin_LoginPreference.getUsername());
        ((EditText) findViewById(R.id.password)).setText(lpActivityLogin_LoginPreference.getPassword());


        // Setzte das Ergebniss, ob wir einen AutoLogin versuchen wollen in eine Variable
        final Boolean BOOL_AUTOLOGIN = lpActivityLogin_LoginPreference.isAutologin();
        if (BOOL_AUTOLOGIN) {
            chbxActivityLogin_autologin.setChecked(true);
            // Prüfen ob alle Bedingungen für einen Autologin erfüllt sind
            // - Username != ""
            // - Password != ""
            // - Letzter Login weniger als 5 Tage ( 60 Sekunden * 60 Minuten * 24 Stunden * 5 Tage ) her
            if (lpActivityLogin_LoginPreference.getUsername() != "" && lpActivityLogin_LoginPreference.getPassword() != "" && ((System.currentTimeMillis() - lpActivityLogin_LoginPreference.getDate()) / 1000 < 60 * 60 * 24 * 5)) {
                login(lpActivityLogin_LoginPreference.getUsername(), lpActivityLogin_LoginPreference.getPassword(), BOOL_AUTOLOGIN);
            }
        } else {
            chbxActivityLogin_autologin.setChecked(false);
        }


        // Link zu dem Register Screen - wenn der Benutzer auf den Text klickt
        txtviewActivityLogin_registerScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentActivityLogin_LinkRegister = new Intent(getApplicationContext(), activityRegister.class);
                intentActivityLogin_LinkRegister.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentActivityLogin_LinkRegister);
            }
        });


        // wenn der Login Button gedrückt wird, veruschen wir mit den Eingaben des Benutzers ein Login
        btnActivityLogin_c2g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strActivityLogin_Username = ((EditText) findViewById(R.id.email)).getText().toString();
                String strActivityLogin_Password = ((EditText) findViewById(R.id.password)).getText().toString();


                if (Validate.isValidUsername(strActivityLogin_Username)) {

                    if (Validate.isValidPassword(strActivityLogin_Password)) {
                        login(strActivityLogin_Username, strActivityLogin_Password, ((CheckBox) findViewById(R.id.checkBox)).isChecked());
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.validError_password), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.validError_username), Toast.LENGTH_LONG).show();
                }


            }

        });
    }


    /**
     *
     * @param username
     * @param password
     * @param autologin
     */
    public void login(String username, String password, Boolean autologin) {
        dlgActivityLogin_loginDialog = new Dialog(activityLogin.this);
        dlgActivityLogin_loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgActivityLogin_loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dlgActivityLogin_loginDialog.setContentView(R.layout.dialog_autologin);
        dlgActivityLogin_loginDialog.setCanceledOnTouchOutside(true);
        dlgActivityLogin_loginDialog.show();

        Button dbutton = (Button) dlgActivityLogin_loginDialog.findViewById(R.id.dbutton);

        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intActivityLogin_LOGIN_NOT_ALLOWED = 1;
                new LoginPreference(getApplicationContext()).removeLogin();
                dlgActivityLogin_loginDialog.dismiss();
                CheckBox chbxActivityLogin_autologin = (CheckBox) findViewById(R.id.checkBox);
                chbxActivityLogin_autologin.setChecked(false);
            }
        });

        tryLogin(username, password, autologin);
    }

    /**
     *
     * @param username
     * @param password
     * @param autologin
     */
    public void tryLogin(String username, String password, Boolean autologin) {

        final String strActivityLogin_Username = username;
        final String strActivityLogin_Password = password;

        // Wir prüfen die Logindaten - dazu benötigen wir einen eigenen Thread
        Thread thrdActivityLogin_loginThread = new Thread(new Runnable() {

            public void run() {
                try {

                    long lng_ActivityLogin_thread_vorThreadZeit = System.currentTimeMillis();

                    String urlParameters = "username=" + strActivityLogin_Username + "&password=" + strActivityLogin_Password + "&commkey=ASdno124KA123ASD230ASDm0";

                    HTTPRequest httpreqActivityLogin_LoginConnection = new HTTPRequest(getResources().getString(R.string.urlConn_loginURL));
                    httpreqActivityLogin_LoginConnection.postRequest(urlParameters);


                    // Wir sagen das der Login Mindestens 3000 dauern solll / damit die Benutzer zeit für Cancel haben.
                    // Damit wir auch Mind. 3000 milli Sek haben, müssen wir vor Request die Zeit stoppen, die Zeit nach dem Request ebenfalls stoppen.
                    // Die Differenz von beiden werten von 3000 Milli Sek abziehen, ist was übrig... so machen wir noch eine Pause... sonst nicht

                    long lng_ActivityLogin_thread_nachThreadZeit = System.currentTimeMillis();
                    long lng_ActivityLogin_thread_sleepTime = 3000 - (lng_ActivityLogin_thread_nachThreadZeit - lng_ActivityLogin_thread_vorThreadZeit);
                    if (lng_ActivityLogin_thread_sleepTime > 0) {
                        Thread.sleep(lng_ActivityLogin_thread_sleepTime);
                    }


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
                        // 100 - Login OK
                        // 101 - LOGIN NICHT OK > Logindaten fehlerhaft
                        // 105 - Verbindung nicht erlaubt
                        switch (strActivityLogin_LoginResponse) {

                            case "100":


                                if (intActivityLogin_LOGIN_NOT_ALLOWED == 0) {

                                    CheckBox chbxActivityLogin_autologin = (CheckBox) findViewById(R.id.checkBox);

                                    if (chbxActivityLogin_autologin.isChecked()) {
                                        new LoginPreference(getApplicationContext()).setLogin(strActivityLogin_Username, strActivityLogin_Password, true);
                                    } else {
                                        new LoginPreference(getApplicationContext()).setLogin(strActivityLogin_Username, strActivityLogin_Password, false);
                                    }

                                    Intent intentActivityLogin_camera = new Intent(getApplicationContext(), activityCamera.class);
                                    intentActivityLogin_camera.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intentActivityLogin_camera.putExtra("username", strActivityLogin_Username);
                                    dlgActivityLogin_loginDialog.hide();
                                    startActivity(intentActivityLogin_camera);

                                }
                                intActivityLogin_LOGIN_NOT_ALLOWED = 0;

                                break;

                            case "101":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.loginActivity_error_loginFailed),
                                        Toast.LENGTH_LONG).show();
                                break;

                            case "105":
                                Toast.makeText(
                                        getBaseContext(),
                                        getResources().getString(R.string.loginActivity_error_connectionError),
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
                                getResources().getString(R.string.loginActivity_error_connectionError),
                                Toast.LENGTH_LONG).show();
                    }
                    dlgActivityLogin_loginDialog.hide();
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
                    R.string.loginActivity_error_offlineNotification,
                    Toast.LENGTH_LONG).show();
            dlgActivityLogin_loginDialog.hide();
        }
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}
