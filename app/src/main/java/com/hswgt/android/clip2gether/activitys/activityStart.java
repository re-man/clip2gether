package com.hswgt.android.clip2gether.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hswgt.android.clip2gether.R;

public class activityStart extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    /**
     * Die onclick events, welche in der XML Datei (activitystart.xml) definiert wurden, werden abgefragt
     * @param v View
     */
    public void onClickHandler(View v) {
        switch (v.getId()) {

            // Share Screen - Standard Share Intent nutzen
            case R.id.btnShareThisApp:
                Intent intentActivityStart_ShareScreen = new Intent();
                intentActivityStart_ShareScreen.setAction(Intent.ACTION_SEND);

                // Der Text aus dem string.xml Datei - startActivity_shareAppText
                // Die ShareURL aus dem string.xml Datei - urlConn_shareApp
                intentActivityStart_ShareScreen.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.startActivity_shareAppText) + "\r\n\r\n"+ getResources().getString(R.string.urlConn_shareApp) );
                intentActivityStart_ShareScreen.setType("text/plain");
                startActivity(intentActivityStart_ShareScreen);
                break;

            // Galerie anzeigen
            case R.id.btnViewGalerie:
                Intent intentActivityStart_ViewGalerie = new Intent(getApplicationContext(), activityGallery.class);

                // Dieses Intent nicht in der History speichern
                intentActivityStart_ViewGalerie.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentActivityStart_ViewGalerie);
                break;

            // Login Methode waehlen - Login Intent aufrufen
            case R.id.btnSelectLoginMethod:
                Intent intentActivityStart_Login = new Intent(getApplicationContext(), activityLogin.class);

                // Dieses Intent nicht in der History speichern
                intentActivityStart_Login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentActivityStart_Login);
                break;
        }
    }
}