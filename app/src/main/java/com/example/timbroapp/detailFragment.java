package com.example.timbroapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myInt = bundle.getInt("item", 0);
            myInt = myInt + 1;
            n_timbro = myInt;
            //TextView detailTextView = (TextView)view.findViewById(R.id.detailTextView);
            //detailTextView.setText("Timbro " + myInt);
        }

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fragment_detail);
        frameLayout.addView(new DrawView(getActivity()));

        // Inflate the layout for this fragment
        return view;
    }

    public class DrawView extends View {

        public DrawView(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 50;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            paint.setColor(Color.parseColor("#da4747"));

            // draw circle
            canvas.drawCircle(x - 60, 60, radius, paint);

            // draw text
            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("Timbro " + n_timbro, 30, 60, paint);
        }
    }
}