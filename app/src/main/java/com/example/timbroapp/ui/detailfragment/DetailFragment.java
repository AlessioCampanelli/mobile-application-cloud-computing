package com.example.timbroapp.ui.detailfragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.timbroapp.R;
import com.example.timbroapp.Singleton;
import com.example.timbroapp.StampType;
import com.example.timbroapp.model.Stamping;
import com.example.timbroapp.ui.homefragment.TimbriViewModel;
import com.example.timbroapp.ui.view.DrawView;
import com.example.timbroapp.ui.view.LoadingDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DetailFragment extends Fragment {

    private List<Stamping> stampings;

    public TimbriViewModel model;

    private FloatingActionButton fabCheckIn;
    private FloatingActionButton fabCheckOut;
    private FloatingActionButton fabDownloadPDF;
    private FloatingActionButton fab;
    private LinearLayout llCheckIn;
    private LinearLayout llCheckOut;
    private LinearLayout llDownloadPDF;
    private TextView checkInLabel;
    private TextView checkOutLabel;
    private TextView downloadLabel;

    private TextView titleView;
    private TextView addressView;
    private TextView startTimeView;
    private TextView endTimeView;
    private TextView startStampedTimeView;
    private TextView endStampedTimeView;
    private TextView gpsView;

    private DrawView circle;

    private LoadingDialog loadingDialog = new LoadingDialog(getActivity());

    private Stamping currentStamping;
    private StampType typeChoosed;

    FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    private static final String ITEM = "item";

    private int indexStamping;

    private ActivityResultLauncher<String[]> permissionRequest;

    private int index;
    private boolean isFABOpen = false;
    private DetailFragmentViewModel detailFragmentViewModel;

    public DetailFragment() {
    }


    public static DetailFragment newInstance(int index) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ITEM, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            indexStamping = getArguments().getInt(ITEM, 0);
        }
        permissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        saveStoragePdf();
                    }
                });


        detailFragmentViewModel = ViewModelProviders.of(requireActivity()).get(DetailFragmentViewModel.class);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        stampings = Singleton.getInstance().getStampings();


        fabCheckIn = (FloatingActionButton) view.findViewById(R.id.fab_checkIn);
        fabCheckOut = (FloatingActionButton) view.findViewById(R.id.fab_checkOut);
        fabDownloadPDF = (FloatingActionButton) view.findViewById(R.id.fab_download);
        llCheckIn = (LinearLayout) view.findViewById(R.id.ll_fab_checkIn);
        llCheckOut = (LinearLayout) view.findViewById(R.id.ll_fab_checkout);
        llDownloadPDF = (LinearLayout) view.findViewById(R.id.ll_fab_download);
        checkInLabel = (TextView) view.findViewById(R.id.fab_checkIn_label);
        checkOutLabel = (TextView) view.findViewById(R.id.fab_checkout_label);
        downloadLabel = (TextView) view.findViewById(R.id.fab_download_label);

        titleView = (TextView) view.findViewById(R.id.textviewTitle);
        addressView = (TextView) view.findViewById(R.id.textviewAddress);
        startTimeView = (TextView) view.findViewById(R.id.textviewStartTime);
        endTimeView = (TextView) view.findViewById(R.id.textviewEndTime);
        startStampedTimeView = (TextView) view.findViewById(R.id.textviewStartStampedTime);
        endStampedTimeView = (TextView) view.findViewById(R.id.textviewEndStampedTime);
        gpsView = (TextView) view.findViewById(R.id.textviewGps);
        circle = (DrawView) view.findViewById(R.id.customView);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fabCheckIn.setImageResource(R.drawable.ic_baseline_check_box_24);
        fabCheckOut.setImageResource(R.drawable.ic_baseline_library_add_check_24);
        fab.setImageResource(R.drawable.ic_baseline_add_24);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });


        ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.fragment_detail);

        firebaseAuth();
        currentStamping = stampings.get(indexStamping);

        updateUI();

        db = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fabCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = new LoadingDialog(getActivity());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation(StampType.CHECKIN);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        fabCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog = new LoadingDialog(getActivity());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation(StampType.CHECKOUT);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        fabDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveStoragePdf();
                }else {
                    permissionRequest.launch(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE});
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void saveStoragePdf() {
        switch (currentStamping.getStatusFile()) {
            case UNKNOWN:
            case TO_DOWNLOAD: {

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), currentStamping.getFileName());

                try {
                    detailFragmentViewModel.getPDF(file, currentStamping);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case READY: {
                File file = new File(currentStamping.getFilePath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            }
            case IN_DOWNLOAD: {

                break;
            }
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        llCheckIn.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        llCheckOut.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        llDownloadPDF.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        downloadLabel.setVisibility(View.VISIBLE);
        checkInLabel.setVisibility(View.VISIBLE);
        checkOutLabel.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        llCheckIn.animate().translationY(0);
        llCheckOut.animate().translationY(0);
        llDownloadPDF.animate().translationY(0);
        downloadLabel.setVisibility(View.INVISIBLE);
        checkInLabel.setVisibility(View.INVISIBLE);
        checkOutLabel.setVisibility(View.INVISIBLE);
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

        if (!isLocationManagerEnabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        if (gpsPreferencesEnabled) {
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
        } else {
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
        Log.d(TAG, "Firebase_token " + firebase_token);

        mAuth.signInWithCustomToken(firebase_token)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
                detailFragmentViewModel.checkFileExist(currentStamping);


                detailFragmentViewModel.statusFile.observe(getViewLifecycleOwner(), statusFIle -> {
                    switch (statusFIle) {
                        case READY: {
                            downloadLabel.setText("Apri file: ");
                            fabDownloadPDF.setImageResource(R.drawable.ic_baseline_file_copy_24);
                            break;
                        }
                        case IN_DOWNLOAD: {
                            fabDownloadPDF.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                            break;
                        }
                        case TO_DOWNLOAD:{
                            fabDownloadPDF.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                            break;
                        }
                        case UNKNOWN: {
                            fabDownloadPDF.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                            break;
                        }
                    }
                });

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
                    Date startStampedDate = new Date(Long.parseLong(currentStamping.getStartStampedTime()) * 1000);
                    String startStampedFormattedDate = formatter.format(startStampedDate);
                    startStampedTimeView.invalidate();
                    startStampedTimeView.setText(startStampedFormattedDate);
                }

                if (currentStamping.getEndStampedTime() != null) {
                    Date endStampedDate = new Date(Long.parseLong(currentStamping.getEndStampedTime()) * 1000);
                    String endStampedFormattedDate = formatter.format(endStampedDate);
                    endStampedTimeView.invalidate();
                    endStampedTimeView.setText(endStampedFormattedDate);
                }

                if (currentStamping.getStartStampedTime() != null && currentStamping.getEndStampedTime() != null) {
                    circle.colorCircle(Color.GREEN);
                    fabCheckIn.setEnabled(false);
                    fabCheckOut.setEnabled(false);
                }

                if (currentStamping.getLatitude() != null && currentStamping.getLongitude() != null) {
                    gpsView.setText(currentStamping.getLatitude() + ", " + currentStamping.getLongitude());
                }
            }
        });
    }

    private void updateFirestoreStampings(StampType type, @Nullable Location location) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DocumentReference doc_ref = db.collection("stampings").document(currentStamping.getIdDoc());
        if (type == StampType.CHECKIN) {
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
                                currentStamping = stampings.get(indexStamping);
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
        } else if (type == StampType.CHECKOUT) {
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
                                currentStamping = stampings.get(indexStamping);
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