package com.menowattge.nhc;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Per effettuare il Login da user e pass genero un token
 */
public class LoginCredentials {

    //private String name;
    //private String pass;

    private String Email;
    private String Password;

    public LoginCredentials(String Email, String Password) {
        this.Email = Email;
        this.Password = Password;
    }


    //creo un token a partire dalle credenziali
    public static String getAuthToken(String username, String password) {
        byte[] data = new byte[0];
        try {
            data = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

}
