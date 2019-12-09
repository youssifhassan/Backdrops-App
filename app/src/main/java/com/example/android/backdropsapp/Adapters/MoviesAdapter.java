package com.example.android.backdropsapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.backdropsapp.Models.MovieResult;
import com.example.android.backdropsapp.R;
import com.example.android.backdropsapp.Utilities.OnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private ArrayList<MovieResult> moviesList;
    private Context context;
    private OnClickListener mOnClickListener;

    public MoviesAdapter(ArrayList<MovieResult> moviesList, Context context, OnClickListener mOnClickListener) {
        this.moviesList = moviesList;
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185//"+moviesList.get(position).getResults().get(position).getPosterPath()).placeholder(R.mipmap.ic_launcher).into(holder.poster);
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onPosterClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;


        ViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.poster);
        }
    }
}