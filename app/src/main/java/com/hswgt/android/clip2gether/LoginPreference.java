package com.hswgt.android.clip2gether;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Andreas on 23.06.2015.
 */
public class LoginPreference {

    private Context context;
    private SharedPreferences settings;

    /**
     * Erstelle ein neues Objekt um auf die SharedPreferences zuzugreifen, es wird auf das Default Preferences zugegriefen
     * @param context Context 
     */
    public LoginPreference(Context context) {
        this.context = context;
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Setze die Werte auf NULL, es wird somit die Daten gelöscht
     *
     * @return boolean
     */
    public boolean removeLogin() {

        SharedPreferences.Editor editor;
        editor = settings.edit();

        // der gespeicherte Username auf NULL setzen
        editor.putString("LOGIN_USERNAME", "");

        // das gespeicherte Passwort auf NULL setzen
        editor.putString("LOGIN_PASSWORD", "");

        // das gespeicherte Login Datum auf das aktuelle Datum setzen
        editor.putString("LOGIN_DATE", String.valueOf(System.currentTimeMillis()));

        // die Information ob ein Autologin versucht werden soll auf false setzen
        editor.putBoolean("LOGIN_AUTO", false);

        // änderungen commiten
        editor.commit();
        return true;
    }

    /**
     * Setze die Werte der Preference mit den Werten der @Params
     *
     * @param username String
     * @param password String
     * @param autologin boolean
     * @return boolean
     */
    public boolean setLogin(String username, String password, boolean autologin) {

        SharedPreferences.Editor editor;
        editor = settings.edit();

        // der gespeicherte Username  auf @param username ändern
        editor.putString("LOGIN_USERNAME", username);

        // der gespeicherte Username auf @param password ändern
        editor.putString("LOGIN_PASSWORD", password);

        // das gespeicherte Login Datum auf das aktuelle Datum ändern
        editor.putString("LOGIN_DATE", String.valueOf(System.currentTimeMillis()));

        // die Information ob ein Autologin versucht werden soll auf @param autoLogin ändern
        editor.putBoolean("LOGIN_AUTO", autologin);

        // änderungen commiten
        editor.commit();
        return true;
    }

    /**
     * Gib den gespeicherten username zurück, ansonsten nichts
     *
     * @return String
     */
    public String getUsername() {

        return settings.getString("LOGIN_USERNAME", null);
    }

    /**
     * Gib den gespeicherten username zurück, ansonsten nichts
     * @return String
     */
    public String getPassword() {

        return settings.getString("LOGIN_PASSWORD", null);
    }

    /**
     * Gib das erste Login Date zurück, damit kann geprüft werden ob die Benutzerdaten nicht mehr gültig sind (für Autologin)
     * @return long
     */
    public long getDate() {
        return Long.parseLong(settings.getString("LOGIN_DATE", "0"));
    }

    /**
     * Gibt die Information zurück ( boolean ) ob der Nutzer ein Autologin wünscht
     * @return boolean
     */
    public boolean isAutologin() {
        return settings.getBoolean("LOGIN_AUTO", false);
    }

}
