package com.example.timbroapp.ui.homefragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.timbroapp.R;
import com.example.timbroapp.model.Stamping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TimbriAdapter extends RecyclerView.Adapter<TimbriAdapter.ViewHolder> {

    private List<Stamping> localDataSet = new ArrayList<>();
    private OnClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }


    }

    public TimbriAdapter(List<Stamping> dataSet, OnClickListener listener) {
        if(dataSet != null)
            localDataSet = dataSet;
        this.listener = listener;
    }

    public void clear() {
        localDataSet.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Stamping> data) {
        localDataSet.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timbri_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        TextView title = viewHolder.itemView.findViewById(R.id.timbro);
        TextView checkInDate = viewHolder.itemView.findViewById(R.id.check_in_date);
        TextView checkOutDate = viewHolder.itemView.findViewById(R.id.check_out_date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v,position);
            }
        });

        title.setText(localDataSet.get(position).getTitle());
        Date startTimeDate = new Date(Long.parseLong(localDataSet.get(position).getStartTime()) * 1000);
        String startTimeFormattedDate = formatter.format(startTimeDate);

        Date finalTimeDate = new Date(Long.parseLong(localDataSet.get(position).getEndTime()) * 1000);
        String finalTimeFormattedDate = formatter.format(finalTimeDate);

        checkInDate.setText(startTimeFormattedDate);
        checkOutDate.setText(finalTimeFormattedDate);


    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }
}