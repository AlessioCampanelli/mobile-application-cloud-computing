package com.example.timbroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import kotlinx.coroutines.Dispatchers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailFragment extends Fragment {

    private Button TimbroCheckIn;
    private Button TimbroCheckOut;
    private Button DownloadPDF;
    private List<Stamping> stampings;

    FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int n_timbro;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public detailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static detailFragment newInstance(String param1, String param2) {
        detailFragment fragment = new detailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Stamping stmp = stampings.get(n_timbro);

        TimbriViewModel model = new ViewModelProvider(this).get(TimbriViewModel.class);

        model.stampings.observe(getViewLifecycleOwner(), stampings -> {
            // update UI
            Log.d(TAG, "Stampings retrieved correctly!");
        });

        model.onError.observe(getViewLifecycleOwner(), stampings -> {
            // update UI
            Log.d(TAG, "Error on retrieving stampings");
        });

        model.loadStampings(stmp.getIdUser());

    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TimbroCheckIn = (Button)view.findViewById(R.id.checkIn);
        TimbroCheckOut= (Button)view.findViewById(R.id.checkOut);
        DownloadPDF = (Button)view.findViewById(R.id.buttonDownload);
        stampings = Singleton.getInstance().getStampings();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myInt = bundle.getInt("item", 0);
            n_timbro = myInt;
            //TextView detailTextView = (TextView)view.findViewById(R.id.detailTextView);
            //detailTextView.setText("Timbro " + myInt);
        }

        ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.fragment_detail);
        //layout.addView(new DrawView(getActivity()));


        firebaseAuth();
        Stamping stmp = stampings.get(n_timbro);
        db = FirebaseFirestore.getInstance();
        DocumentReference doc_ref = db.collection("stampings").document(stmp.getIdDoc());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        TimbroCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                doc_ref
                        .update("start_stamped_time", timestamp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        TimbroCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Timbratura Check-Out effettuata con successo!", Toast.LENGTH_LONG).show();

               /* Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                doc_ref
                        .update("end_stamped_time", timestamp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });*/

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        DownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPDF();
            }

            public void downloadPDF() {
                /*viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val devices = deviceRepo.meshDevicesSync

                        val maxDevices = deviceRepo.getMaxDevicesForPlant(ignoreDeviceType = true)
                        val app = getApplication<Application>()

                        if(devices.size >= maxDevices){
                            errorMessage.postValue(app.getString(R.string.error_max_devices_reached))
                            return@launch
                        }

                        isDeviceToAdd.postValue(true)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }*/
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        //tvLatitude.setText(String.valueOf(location.getLatitude()));
                        //tvLongitude.setText(String.valueOf(location.getLongitude()));

                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                //tvLatitude.setText(String.valueOf(location1.getLatitude()));
                                //tvLongitude.setText(String.valueOf(location1.getLongitude()));
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void firebaseAuth() {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String firebase_token = Singleton.getInstance().getFirebase_token();
        Log.d(TAG, "Firebase_token "+ firebase_token);

        mAuth.signInWithCustomToken(firebase_token)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //db = FirebaseFirestore.getInstance();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}