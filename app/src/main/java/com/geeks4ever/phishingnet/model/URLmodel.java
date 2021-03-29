package com.geeks4ever.phishingnet.model;

public class URLmodel {

    public static final int GOOD_URL = 0;
    public static final int BAD_URL = 1;

    public int status;
    public String url;

    public URLmodel(int status, String url) {
        this.status = status;
        this.url = url;
    }

}
