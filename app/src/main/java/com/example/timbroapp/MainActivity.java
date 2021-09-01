package com.example.timbroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private Button Login;

    String jwt_token;
    String firebase_token;
    String id_user;

    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
                validate(Name.getText().toString(), Password.getText().toString());
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
                        firebase_token = response.body().getFirebase_token();
                        id_user = response.body().getId_user();
                        loadingDialog.dismissDialog();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                        Call<ResultStampings> call_stampings = RetrofitClient.getInstance().getLoginService().list_stampings(id_user, "Bearer "+jwt_token);

                        // call list stampings
                        call_stampings.enqueue(new Callback<ResultStampings>() {
                            @Override
                            public void onResponse(Call<ResultStampings> call, Response<ResultStampings> response) {
                                if(response.isSuccessful()) {
                                    Singleton.getInstance().setStampings(response.body().getStampings());
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
                            public void onFailure(Call<ResultStampings> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Error in retrieving list of stampings", Toast.LENGTH_LONG).show();
                            }
                        });
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
                    Toast.makeText(MainActivity.this, "Error in executing login", Toast.LENGTH_LONG).show();
            }
        });

    }
}