package com.hswgt.android.clip2gether;

// import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Andreas on 22.06.2015.
 */
public class AESCrypt {

    /**
     * Die Funktion encrypt gibt einen verschlüsselten @return String zurück.
     * Verschlüsselt wird einen @param input String mit der AES Verschlüsselungstechnik mit dem Schlüssel @param key
     *
     * @param input String
     * @param key String
     * @return String
     */
    public static String encrypt(String input, String key){
        return "";

//        byte[] crypted = null;
//        try{
//            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, skey);
//            crypted = cipher.doFinal(input.getBytes());
//        }catch(Exception e){
//            System.out.println(e.toString());
//        }
//
//        return new String(Base64.encodeBase64(crypted));
    }

    /**
     * Die Funktion decrypt gibt einen entschhlüsselten @return String zurück.
     * Entschlüsselt wird einen @param input String mit der AES Entschlüsselungstechnik mit dem Schlüssel @param key
     *
     * @param input String
     * @param key String
     * @return String
     */
    public static String decrypt(String input, String key){
        return "";

//        byte[] output = null;
//        try{
//            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skey);
//            output = cipher.doFinal(Base64.decodeBase64(input));
//        }catch(Exception e){
//            System.out.println(e.toString());
//        }
//         return new String(output);
    }
}
