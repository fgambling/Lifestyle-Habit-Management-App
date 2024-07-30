package com.comp90018.a2.location;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LatLong implements Parcelable {
    public final double latitude;
    public final double longitude;
    public LatLong(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static final Parcelable.Creator<LatLong> CREATOR = new Parcelable.Creator<LatLong>() {
        @Override
        public LatLong createFromParcel(Parcel parcel) {
            double latitude = parcel.readDouble();
            double longitude = parcel.readDouble();
            return new LatLong(latitude, longitude);
        }

        @Override
        public LatLong[] newArray(int size) {
            return new LatLong[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    @NonNull
    @Override
    public String toString(){
        return String.format("%s, %s", latitude, longitude);
    }
}
