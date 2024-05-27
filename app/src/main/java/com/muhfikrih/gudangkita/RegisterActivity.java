package com.muhfikrih.gudangkita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class RegisterActivity extends AppCompatActivity {
    //Berfungsi untuk binding view agar kita tidak perlu mendeklarasikan ulang view yang akan digunakan.
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.cbRememberMe)
    CheckBox cbRememberMe;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tvLogin)
    TextView tvLogin;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        setup();
    }

    //Berfungsi untuk menangani setiap view yang di-klik oleh pengguna
    @OnClick({R.id.cbRememberMe, R.id.btnRegister, R.id.tvLogin})
    void onClick(View v) {
        if (v == cbRememberMe) {

        } else if (v == btnRegister) {
            signUp();
        } else if (v == tvLogin) {
            finish();
        }
    }

    void setup() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    void signUp() {
        if (validateForm()) {
            //Ketika validasi form Edit Text sukses

            //Berfungsi untuk menampilkan dialog loading
            ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            //Berfungsi untuk mendapatkan data email dan password yang dimasukan user.
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            //Berfungsi untuk mendaftarkan data email dan password pengguna ke Firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        //Jika registrasi berhasil
                        Log.e("Regis", "Success");
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        //Jika registrasi gagal
                        Toast.makeText(RegisterActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //Ketika validasi form Edit Text gagal
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
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError("Required");
            result = false;
        } else {
            etUsername.setError(null);
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
        //Berfungsi untuk mendaftarkan data email dan password pengguna ke Database.
        writeNewUser(user.getUid(), etUsername.getText().toString(), user.getEmail());

        //Berfungsi untuk menyimpan data Email ke Shared Preference
        String email = etEmail.getText().toString();
        DataHelper dataHelper = new DataHelper(RegisterActivity.this);
        SharedPreferences preferences = dataHelper.getPrefs();
        preferences.edit().putString("Email", email).apply();

        //Berfungsi untuk berpindah ke halaman utama setelah berhasil mendaftarkan pengguna.
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
        Toast.makeText(RegisterActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("user").child(userId).setValue(user);
    }
}