package com.example.android.backdropsapp.Fragments;

import android.app.WallpaperManager;
import android.content.Intent;
        import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.android.backdropsapp.HomeActivity;
        import com.example.android.backdropsapp.Models.MovieResult;
        import com.example.android.backdropsapp.R;
        import com.squareup.picasso.Picasso;
        import com.squareup.picasso.Target;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.ArrayList;

public class ExploreDetailsFragment extends Fragment implements HomeActivity.OnBackPressedListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View view = inflater.inflate(R.layout.explore_details_fragment, null);
        receivedSelectedMovieData(view);
        return view;
    }

    private void receivedSelectedMovieData(View view) {
        ArrayList<MovieResult> transactionList = (ArrayList<MovieResult>) getArguments().getSerializable("MovieDetails");
        int position = getArguments().getInt("Position");

        setMainView(view, position, transactionList);
    }

    private void setMainView(View view, final int position, final ArrayList<MovieResult> transactionList) {
        ImageView poster = view.findViewById(R.id.poster);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185//" + transactionList.get(position).getResults().get(position).getPosterPath()).into(poster);

        TextView movieName = view.findViewById(R.id.movie_name);
        movieName.setText(transactionList.get(position).getResults().get(position).getTitle());

        TextView movieDate = view.findViewById(R.id.movie_date);
        movieDate.setText(transactionList.get(position).getResults().get(position).getReleaseDate());

        TextView originalLanguage = view.findViewById(R.id.original_language);
        originalLanguage.setText(transactionList.get(position).getResults().get(position).getOverview());

        ImageView save = view.findViewById(R.id.save_image);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageDownload("http://image.tmdb.org/t/p/w185//" + transactionList.get(position).getResults().get(position).getPosterPath());
            }
        });

        final ImageView setAsWallpaper = view.findViewById(R.id.set_wallpaper);
        setAsWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAsWallpaper("http://image.tmdb.org/t/p/w185//" + transactionList.get(position).getResults().get(position).getPosterPath());
            }
        });
    }

    //save image
    private void imageDownload(final String url) {
        Picasso.with(getContext()).load(url).into(new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "Pictures" + "/" + getFileNameFromUrl(url));
                        try {
                            FileOutputStream stream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            stream.flush();
                            stream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

                Toast.makeText(getContext(), "Image Downloaded", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(getContext(), "Downloaded Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Toast.makeText(getContext(), "Image Downloading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String getFileNameFromUrl(String url) {

        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    //setAsWallpaper
    private void setAsWallpaper(String url) {
        Picasso.with(getContext()).load(url).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(getContext(), "Wallpaper Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Toast.makeText(getContext(), "Loading image failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Toast.makeText(getContext(), "Downloading image", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getActivity(),"OnBackpress Click",Toast.LENGTH_LONG).show();
        Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        return true;
    }
}