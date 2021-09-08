package com.example.timbroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.Toast;

import com.example.timbroapp.model.Result;
import com.example.timbroapp.network.RetrofitClient;
import com.example.timbroapp.ui.listStampingsActivity.ListaTimbriActivity;
import com.example.timbroapp.ui.view.LoadingDialog;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONObject;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    private Button Login;
    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextPassword;

    String jwt_token;
    String firebase_token;
    String id_user;

    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getExtras() != null) {
            String expirationMessage = getIntent().getExtras().getString("expirationMessage");
            if (expirationMessage != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, expirationMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        Login = (Button)findViewById(R.id.btnLogin);
        textInputEditTextName= findViewById(R.id.edit_text_name);
        textInputEditTextPassword = findViewById(R.id.edit_text_password);

        textInputEditTextName.setText("admin@maacStampings.it");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();

                validate(Objects.requireNonNull(textInputEditTextName.getText()).toString(), textInputEditTextPassword.getText().toString());
            }
        });
    }

    private void validate(String username, String password) {

        Call<Result> call_login = RetrofitClient.getInstance().getLoginService().login(username, password);

        call_login.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call_login, Response<Result> response) {
                    if(response.isSuccessful()) {
                        jwt_token = response.body().getJwt_token();
                        Singleton.getInstance().setJwt_token(jwt_token);
                        firebase_token = response.body().getFirebase_token();
                        Singleton.getInstance().setFirebase_token(firebase_token);
                        id_user = response.body().getId_user();
                        Singleton.getInstance().setId_user(id_user);
                        loadingDialog.dismissDialog();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            }
                        });

                        // go to next activity
                        Intent intent = new Intent(MainActivity.this, ListaTimbriActivity.class);
                        startActivity(intent);
                    } else {
                        try {
                            loadingDialog.dismissDialog();
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error in executing login", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }
}