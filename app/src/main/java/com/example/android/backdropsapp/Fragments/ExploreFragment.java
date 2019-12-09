package com.example.android.backdropsapp.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.backdropsapp.Adapters.MoviesAdapter;
import com.example.android.backdropsapp.HomeActivity;
import com.example.android.backdropsapp.Models.MovieResult;
import com.example.android.backdropsapp.Models.Result;
import com.example.android.backdropsapp.R;
import com.example.android.backdropsapp.Services.UserService;
import com.example.android.backdropsapp.Utilities.MovieApiInterface;
import com.example.android.backdropsapp.Utilities.OnClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.backdropsapp.Utilities.Constants.API_KEY;
import static com.example.android.backdropsapp.Utilities.Constants.BASE_URL;
import static com.example.android.backdropsapp.Utilities.Constants.CATEGORY;
import static com.example.android.backdropsapp.Utilities.Constants.LANGUAGE;
import static com.example.android.backdropsapp.Utilities.Constants.LOAD_DATA_FROM_API;
import static com.example.android.backdropsapp.Utilities.Constants.PAGE;

public class ExploreFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.explore_fragment, null);
        prepareMainView(view);
        loadPhotosJsonData();
        return view;
    }

    public void prepareMainView(View view){
        recyclerView = view.findViewById(R.id.movies_list);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void loadPhotosJsonData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiInterface movieApiInterface = retrofit.create(MovieApiInterface.class);
        Call<MovieResult> call = movieApiInterface.getMovies(CATEGORY,API_KEY,LANGUAGE,PAGE);

        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {

                final MovieResult movieResult = new MovieResult();
                final ArrayList<MovieResult> movieResultArrayList;
                movieResultArrayList = movieResult.ParseMoviesResponse(response);
                setupRecyclerView(movieResultArrayList);
                postDelayToLoadData();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
            }
        });

    }

    private void setupRecyclerView(final ArrayList<MovieResult> movieResultArrayList){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        MoviesAdapter moviesAdapter = new MoviesAdapter(movieResultArrayList, getContext(), new OnClickListener() {
            @Override
            public void onPosterClick(View view, int position) {
                Toast.makeText(getContext(), movieResultArrayList.get(0).getResults().get(position).getTitle(), Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                bundle.putSerializable("MovieDetails",  movieResultArrayList); // Put anything what you want
                bundle.putInt("Position", position);

                ExploreDetailsFragment exploreDetailsFragment = new ExploreDetailsFragment();
                exploreDetailsFragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.screenArea, exploreDetailsFragment).addToBackStack(HomeActivity.class.getName()).commit();
            }
        });

        recyclerView.setAdapter(moviesAdapter);
    }


    private void postDelayToLoadData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        }, LOAD_DATA_FROM_API);
    }

    private void hideDialog() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
