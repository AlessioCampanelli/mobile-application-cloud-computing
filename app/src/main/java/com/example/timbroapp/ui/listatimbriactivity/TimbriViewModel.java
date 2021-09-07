package com.example.timbroapp.ui.listatimbriactivity;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.timbroapp.Singleton;
import com.example.timbroapp.model.ResultStampings;
import com.example.timbroapp.model.Stamping;
import com.example.timbroapp.network.RetrofitClient;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimbriViewModel extends ViewModel {
    public MutableLiveData<List<Stamping>> stampings = new MutableLiveData<>();
    public MutableLiveData<String> onError = new MutableLiveData<>();

    public void loadStampings(String id_user) {
        // Do an asynchronous operation to fetch users.
        String jwt_token = Singleton.getInstance().getJwt_token();
        Call<ResultStampings> call_stampings = RetrofitClient.getInstance().getLoginService().list_stampings(id_user, "Bearer " + jwt_token);

        // call list stampings
        call_stampings.enqueue(new Callback<ResultStampings>() {
            @Override
            public void onResponse(Call<ResultStampings> call, Response<ResultStampings> response) {
                if (response.isSuccessful()) {
                    Singleton.getInstance().setStampings(response.body().getStampings());
                    stampings.postValue(response.body().getStampings());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        onError.postValue(jObjError.getString("message"));
                    } catch (Exception e) {
                        //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        onError.postValue(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultStampings> call, Throwable t) {
                Log.d(TAG, "Failure!");
            }
        });
    }


}

