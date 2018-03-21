package com.kfugosic.popularmovies.lists;

import android.os.Parcel;
import android.os.Parcelable;

import com.kfugosic.popularmovies.models.Review;
import com.kfugosic.popularmovies.models.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailersReviewsTuple implements Parcelable {

    private List<Trailer> trailerList;
    private List<Review> reviewList;

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

    protected TrailersReviewsTuple(Parcel in) {
        if (in.readByte() == 0x01) {
            trailerList = new ArrayList<Trailer>();
            in.readList(trailerList, Trailer.class.getClassLoader());
        } else {
            trailerList = null;
        }
        if (in.readByte() == 0x01) {
            reviewList = new ArrayList<Review>();
            in.readList(reviewList, Review.class.getClassLoader());
        } else {
            reviewList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (trailerList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(trailerList);
        }
        if (reviewList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviewList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TrailersReviewsTuple> CREATOR = new Parcelable.Creator<TrailersReviewsTuple>() {
        @Override
        public TrailersReviewsTuple createFromParcel(Parcel in) {
            return new TrailersReviewsTuple(in);
        }

        @Override
        public TrailersReviewsTuple[] newArray(int size) {
            return new TrailersReviewsTuple[size];
        }
    };

}
