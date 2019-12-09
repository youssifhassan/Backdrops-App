package com.example.android.backdropsapp.Models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MovieResult implements Serializable {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    private final static long serialVersionUID = 463478823370456371L;

    public MovieResult() {
    }

    public MovieResult(Integer page, Integer totalResults, Integer totalPages, List<Result> results) {
        this.page = page;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.results = results;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public ArrayList<MovieResult> ParseMoviesResponse(Response<MovieResult> jsonResponse) {

        ArrayList<MovieResult> movieResultArrayList = new ArrayList<>();

        for (int i = 0; i < jsonResponse.body().getResults().size(); i++){

            int page = jsonResponse.body().getPage();
            int totalResults = jsonResponse.body().getTotalResults();
            int totalPages = jsonResponse.body().getTotalPages();
            Result result = new Result();
            List<Result> resultList = result.ParseResult(jsonResponse);

            MovieResult movieResult = new MovieResult(page,totalResults,totalPages,resultList);
            movieResultArrayList.add(movieResult);

        }

        return movieResultArrayList;
    }

}
