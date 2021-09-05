package com.example.timbroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DetailFragment extends Fragment {

    private List<Stamping> stampings;

    public TimbriViewModel model;

    private Button timbroCheckIn;
    private Button timbroCheckOut;
    private Button downloadPDF;

    private TextView titleView;
    private TextView addressView;
    private TextView startTimeView;
    private TextView endTimeView;
    private TextView startStampedTimeView;
    private TextView endStampedTimeView;
    private TextView gpsView;

    private LoadingDialog loadingDialog = new LoadingDialog(getActivity());

    private Stamping currentStamping;
    private StampType typeChoosed;

    FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int current_index_stamping;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailFragment() {
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
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        stampings = Singleton.getInstance().getStampings();

        timbroCheckIn = (Button)view.findViewById(R.id.checkIn);
        timbroCheckOut = (Button)view.findViewById(R.id.checkOut);
        downloadPDF = (Button)view.findViewById(R.id.buttonDownload);

        titleView = (TextView) view.findViewById(R.id.textviewTitle);
        addressView = (TextView) view.findViewById(R.id.textviewAddress);
        startTimeView = (TextView) view.findViewById(R.id.textviewStartTime);
        endTimeView = (TextView) view.findViewById(R.id.textviewEndTime);
        startStampedTimeView = (TextView) view.findViewById(R.id.textviewStartStampedTime);
        endStampedTimeView = (TextView) view.findViewById(R.id.textviewEndStampedTime);
        gpsView = (TextView) view.findViewById(R.id.textviewGps);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myInt = bundle.getInt("item", 0);
            current_index_stamping = myInt;
        }

        ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.fragment_detail);

        firebaseAuth();
        currentStamping = stampings.get(current_index_stamping);

        updateUI();

        db = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        timbroCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = new LoadingDialog(getActivity());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation(StampType.CHECKIN);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        timbroCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = new LoadingDialog(getActivity());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation(StampType.CHECKOUT);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        downloadPDF.setOnClickListener(new View.OnClickListener() {
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
            getCurrentLocation(typeChoosed);
        } else {
            Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(StampType type) {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        this.typeChoosed = type;

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean gpsPreferencesEnabled = sharedPref.getBoolean("gpsEnabled", false);
        Boolean isLocationManagerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isLocationManagerEnabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        if(gpsPreferencesEnabled) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        gpsView.setText(String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));

                        loadingDialog.startLoadingDialog();
                        updateFirestoreStampings(typeChoosed, location);
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
        }else{
            loadingDialog.startLoadingDialog();
            updateFirestoreStampings(typeChoosed, null);
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

    private void updateUI() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                titleView.setText(currentStamping.getTitle());
                addressView.setText(currentStamping.getAddress());

                if (currentStamping.getStartTime() != null) {
                    Date startTimeDate = new Date(Long.parseLong(currentStamping.getStartTime()) * 1000);
                    String startTimeFormattedDate = formatter.format(startTimeDate);
                    startTimeView.setText(startTimeFormattedDate);
                }

                if (currentStamping.getEndTime() != null) {
                    Date endTimeDate = new Date(Long.parseLong(currentStamping.getEndTime()) * 1000);
                    String endTimeFormattedDate = formatter.format(endTimeDate);
                    endTimeView.setText(endTimeFormattedDate);
                }

                if (currentStamping.getStartStampedTime() != null) {
                    Date startStampedDate = new Date(Long.parseLong(currentStamping.getStartStampedTime())*1000);
                    String startStampedFormattedDate = formatter.format(startStampedDate);
                    startStampedTimeView.invalidate();
                    startStampedTimeView.setText(startStampedFormattedDate);
                }

                if (currentStamping.getEndStampedTime() != null) {
                    Date endStampedDate = new Date(Long.parseLong(currentStamping.getEndStampedTime())*1000);
                    String endStampedFormattedDate = formatter.format(endStampedDate);
                    endStampedTimeView.invalidate();
                    endStampedTimeView.setText(endStampedFormattedDate);
                }
            }
        });
    }

    private void updateFirestoreStampings(StampType type, @Nullable Location location) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DocumentReference doc_ref = db.collection("stampings").document(currentStamping.getIdDoc());
        if(type == StampType.CHECKIN) {
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put("start_stamped_time", timestamp);

            if (location != null) {
                fields.put("latitude", location.getLatitude());
                fields.put("longitude", location.getLongitude());
            }

            doc_ref
                    .update(fields)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismissDialog();
                                }
                            });

                            Log.d(TAG, "Timbratura Check-In effettuata con successo!");
                            Toast.makeText(getContext(), "Timbratura Check-In effettuata con successo!", Toast.LENGTH_LONG).show();

                            model.stampings.observe(getViewLifecycleOwner(), stampings -> {
                                currentStamping = stampings.get(current_index_stamping);
                                updateUI();
                            });
                            model.loadStampings(Singleton.getInstance().getId_user());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismissDialog();
                                }
                            });
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }else if(type == StampType.CHECKOUT) {
            doc_ref
                    .update("end_stamped_time", timestamp)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismissDialog();
                                }
                            });
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(getContext(), "Timbratura Check-Out effettuata con successo!", Toast.LENGTH_LONG).show();

                            model.stampings.observe(getViewLifecycleOwner(), stampings -> {
                                currentStamping = stampings.get(current_index_stamping);
                                updateUI();
                            });
                            model.loadStampings(Singleton.getInstance().getId_user());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismissDialog();
                                }
                            });
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }
}