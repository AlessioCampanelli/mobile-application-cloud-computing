package com.example.timbroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.timbroapp.Result;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private Button Login;

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

        Call<Result> call = RetrofitClient.getInstance().getLoginService().login(username, password);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                    if(response.isSuccessful()) {
                        String access_token = response.body().getAccess_token();
                        loadingDialog.dismissDialog();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "Error in executing login", Toast.LENGTH_LONG).show();
            }
        });

    }
}