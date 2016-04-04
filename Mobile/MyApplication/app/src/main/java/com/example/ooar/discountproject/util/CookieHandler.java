package com.example.ooar.discountproject.util;

import android.preference.PreferenceActivity;
import android.provider.SyncStateContract;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 4.4.2016.
 */
public class CookieHandler {

    private static final String SET_COOKIE_KEY = "set-cookie";

    public static Map<String, String> readCookies(Response response) {

        List<Header> headerList = response.getHeaders();

        if (headerList.size() == 0) {
            return null;
        }
        Map<String, String> cookie = new HashMap<>();
        for (Header header : headerList) {
            if (header.getName().equals(SET_COOKIE_KEY)) {
                String[] valuesArray = header.getValue().split(";");
                String cookieKey = valuesArray[0].split("=")[0];
                String cookieValue = valuesArray[0].split("=")[1];
                cookie.put(cookieKey, cookieValue);
                break;
            }
        }
        return cookie;
    }

}
