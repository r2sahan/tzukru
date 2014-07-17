/**
 * @author TzuKru
 */

package com.tzukru.dev.util;

import android.os.Parcel;
import android.os.Parcelable;

public class TwitterParcelable implements Parcelable{

    String username;
    int logged;

    TwitterParcelable(String username, int logged) {
        this.username = username;
        this.logged = logged;
    }

    TwitterParcelable(Parcel in) {
        this.username = in.readString();
        this.logged = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeInt(logged);
    }

    public int describeContents() {
        return 0;
    }


    static final Parcelable.Creator<TwitterParcelable> CREATOR = new Parcelable.Creator<TwitterParcelable>() {

        public TwitterParcelable createFromParcel(Parcel in) {
            return new TwitterParcelable(in);
        }

        public TwitterParcelable[] newArray(int size) {
            return new TwitterParcelable[size];
        }
    };
}
