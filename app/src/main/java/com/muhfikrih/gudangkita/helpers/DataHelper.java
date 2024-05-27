package com.muhfikrih.gudangkita.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHelper {
    String key = "GudangKita";//Key yang berfungsi menjadi identitas penyimpanan lokal aplikasi kalian.
    Context context;

    public DataHelper(Context context) {
        this.context = context;
    }

    //Mendapatkan Shared Preference
    public SharedPreferences getPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return prefs;
    }

    //Mendapatkan status apakah pengguna sudah login atau belum
    public Boolean isLoggedIn() {
        String username = getPrefs().getString("Email", "");
        if (username.equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    //Mendapatkan data email akun pengguna
    public String getEmail() {
        String username = getPrefs().getString("Email", "");
        return username;
    }

    //Menghapus seluruh data yang disimpan
    public void deletePref() {
        context.getSharedPreferences(key, 0).edit().clear().commit();
    }
}
