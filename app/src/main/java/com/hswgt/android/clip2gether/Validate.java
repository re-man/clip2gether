package com.hswgt.android.clip2gether;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas on 25.06.2015.
 */
public class Validate {

    /**
     * Validierungsfuntkion für eine EMail
     *
     * Bedinungen:
     * siehe http://www.webmasterpro.de/coding/article/regex-email.html
     *
     * Beispiel für validierte Wörter:
     * - info@clip2gether.com
     * - abuse@sub.domain.neuetdl
     *
     * Ist die RegEx Prüfung erfolgreich, ist der Rückgabewert true, ansonsten false
     * @param email String
     * @return boolean
     */
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * Validierungsfuntkion für ein Password
     *
     * Bedinungen:
     * Länger als 6 Zeichen
     *
     * Beispiel für validierte Wörter:
     * - 123456
     * - abc123
     * - A1b%$s1a98Ak!-A1
     *
     * Ist die RegEx Prüfung erfolgreich, ist der Rückgabewert true, ansonsten false
      * @param pass String
     * @return boolean
     */
    public static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }



    /**
     * Validierungsfuntkion für einen Benutzername
     *
     * Bedinungen:
     * Mindestens 3 Zeichen (Maximal 15) bestehend aus folgenden Möglichkeiten A-Z, a-z, 0-9, , - ,_
     *
     * Beispiel für validierte Wörter:
     * - SportFreak92
     * - ben
     *
     * Ist die RegEx Prüfung erfolgreich, ist der Rückgabewert true, ansonsten false
     * @param username String
     * @return boolean
     */
    public static boolean isValidUsername(String username) {
        String USERNAME_PATTERN = "^[A-Za-z0-9][A-Za-z0-9 -_]{2,14}$";

        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    /**
     * Validierungsfuntkion für eine Bezeichnung
     *
     * Bedinungen:
     * Mindestens 3 Zeichen (Maximal 51) bestehend aus folgenden Möglichkeiten A-Z, a-z, 0-9, Ä, ä, Ü, ü, Ö, ö, #, ., , ,* ,_ ,-
     *
     * Beispiel für validierte Wörter:
     * - Example Example
     * - WeitersBeispiel1234#
     *
     * Ist die RegEx Prüfung erfolgreich, ist der Rückgabewert true, ansonsten false
     * @param description String
     * @return boolean
     */
    public static boolean isValidDescription(String description) {
        String DESCRIPTION_PATTERN = "^[A-Za-z0-9ÄäÜüÖö#.,*_-][A-Za-z0-9#ÄäÜüÖö#.,*_ -]{2,50}$";
        Pattern pattern = Pattern.compile(DESCRIPTION_PATTERN);
        Matcher matcher = pattern.matcher(description);
        return matcher.matches();
    }


}
