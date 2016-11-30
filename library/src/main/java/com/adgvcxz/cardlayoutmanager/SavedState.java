package com.adgvcxz.cardlayoutmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/30.
 */

public class SavedState implements Parcelable {

    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        @Override
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };

    int mTopPosition = 0;

    public SavedState() {

    }

    SavedState(Parcel in) {
        mTopPosition = in.readInt();
    }

    public SavedState(SavedState other) {
        mTopPosition = other.mTopPosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTopPosition);
    }
}
