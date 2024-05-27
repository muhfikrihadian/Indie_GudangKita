package com.muhfikrih.gudangkita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muhfikrih.gudangkita.helpers.DataHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvRegister)
    TextView tvRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        //Berfungsi untuk memvalidasi apakah pengguna ini sebelumnya sudah login atau belum
        //Jika sudah, maka aplikasi akan meredirect langsung ke halaman beranda.
        DataHelper dataHelper = new DataHelper(LoginActivity.this);
        if (dataHelper.isLoggedIn() == true) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @OnClick({R.id.btnLogin, R.id.tvRegister})
    void onClick(View v) {
        if (v == btnLogin) {
            signIn();
        } else if (v == tvRegister) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void signIn() {
        if (validateForm()) {
            //Ketika validasi form Edit Text sukses

            //Berfungsi untuk menampilkan dialog loading
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            //Berfungsi untuk mendapatkan data email dan password yang dimasukan user.
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            //Berfungsi untuk memeriksa data akun yang diinput oleh pengguna
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.e("RespLogin", String.valueOf(task.isSuccessful()));
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //Jika akun ditemukan
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        //Jika akun tidak ditemukan
                        Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            return;
        }
    }

    private boolean validateForm() {
        //Berfungsi untuk memvalidasi isi dari Edit Text.
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Required");
            result = false;
        } else {
            etPassword.setError(null);
        }
        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        //Berfungsi untuk menyimpan data Email ke Shared Preference
        String email = etEmail.getText().toString();
        DataHelper dataHelper = new DataHelper(LoginActivity.this);
        SharedPreferences preferences = dataHelper.getPrefs();
        preferences.edit().putString("Email", email).apply();

        //Berfungsi untuk berpindah ke halaman utama setelah berhasil mendaftarkan pengguna.
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}