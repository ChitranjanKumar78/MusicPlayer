package com.chitranjank.co.musicplayer;

import android.net.Uri;

public class Songs {
    String title;
    Uri uri;

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    public Songs(String title, Uri uri) {
        this.title = title;
        this.uri = uri;
    }
}
