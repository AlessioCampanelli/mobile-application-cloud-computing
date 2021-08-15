package com.example.timbroapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                //loadingDialog.startLoadingDialog();
                //validate(Name.getText().toString(), Password.getText().toString());
                Intent intent = new Intent(MainActivity.this, ListaTimbriActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validate(String username, String password) {

        Call<ResponseBody> call = RetrofitClient.getInstance().getLoginService().login(username, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    loadingDialog.dismissDialog();
                    String s = response.body().string();
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    // go to next activity
                    Intent intent = new Intent(MainActivity.this, ListaTimbriActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error in executing login", Toast.LENGTH_LONG).show();
            }
        });

    }
}