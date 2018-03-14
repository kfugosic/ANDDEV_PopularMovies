package com.kfugosic.popularmovies.lists;

import com.kfugosic.popularmovies.models.Review;
import com.kfugosic.popularmovies.models.Trailer;

import java.util.List;

/**
 * Created by Kristijan on 14-Mar-18.
 */

public class TrailersReviewsTuple {
    private List<Trailer> trailerList;
    private List<Review> reviewList;

    public TrailersReviewsTuple() {
    }

    public TrailersReviewsTuple(List<Trailer> trailerList, List<Review> reviewList) {
        this.trailerList = trailerList;
        this.reviewList = reviewList;
    }

    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

}
