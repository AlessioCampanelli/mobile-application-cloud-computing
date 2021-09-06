package com.example.timbroapp.ui.homefragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.ViewGroup;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.widget.AdapterView.*;

import com.example.timbroapp.ui.view.LoadingDialog;
import com.example.timbroapp.R;
import com.example.timbroapp.Singleton;
import com.example.timbroapp.ui.listatimbriactivity.TimbriViewModel;
import com.example.timbroapp.model.Stamping;
import com.example.timbroapp.ui.detailfragment.DetailFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Stamping> stampings;
    private SwipeRefreshLayout pullToRefresh;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LoadingDialog loadingDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String id_user = Singleton.getInstance().getId_user();
        TimbriViewModel model = new ViewModelProvider(this).get(TimbriViewModel.class);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        model.loadStampings(id_user);

        listView = (RecyclerView)view.findViewById(R.id.rv_timbri);
        TimbriAdapter adapter = new TimbriAdapter(model.stampings.getValue(), new TimbriAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                DetailFragment detailFragment = new DetailFragment().newInstance(position);
                Bundle bundle = new Bundle();
                bundle.putInt("item", position);
                detailFragment.setArguments(bundle);
                detailFragment.model = model;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailFragment).commit();
            }
        });
        listView.setAdapter(adapter);

        model.stampings.observe(getViewLifecycleOwner(), stampings -> {
            // update UI
            pullToRefresh.setRefreshing(false);

            String[] items = new String[stampings.toArray().length];
            for(int i=0; i<stampings.toArray().length; i++) {
                items[i] = stampings.get(i).getTitle();
            }

            adapter.clear();
            adapter.addAll(stampings);




            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                }
            });
        });

        model.onError.observe(getViewLifecycleOwner(), stampings -> {
            // update UI
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                }
            });
            pullToRefresh.setRefreshing(false);
            Log.d(TAG, "Error on retrieving stampings");
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingDialog.startLoadingDialog();
                model.loadStampings(id_user);
            }
        });

        loadingDialog = new LoadingDialog(getActivity());
        // loadingDialog.startLoadingDialog();
    }

    RecyclerView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<Stamping> stampings = Singleton.getInstance().getStampings();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inflate the layout for this fragment
        return view;
    }
}