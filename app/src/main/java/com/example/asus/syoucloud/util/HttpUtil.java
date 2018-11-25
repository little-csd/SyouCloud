package com.example.asus.syoucloud.util;

import android.util.Log;

import com.example.asus.syoucloud.GSON.LyricDetail;
import com.example.asus.syoucloud.GSON.LyricResult;
import com.example.asus.syoucloud.musicManager.Lyric;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpUtil {

    private static final String TAG = "HttpUtil";

    public static void sendHttpUtil(final String address, final HttpCallBackListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.setDoInput(true);
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    response.append(line);
                if (listener != null) listener.onFinish(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) listener.onError();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    public static String parseJsonWithGSON(String response) {
        Log.i(TAG, "parseJsonWithJSONObject: " + response);
        try {
            Gson gson = new Gson();
            LyricResult result = gson.fromJson(response, LyricResult.class);
            List<LyricDetail> mList = result.getResult();
            if (mList.size() == 0) return null;
            String lyricAddress = mList.get(0).getLrc();
            Log.i(TAG, "parseJsonWithJSONObject: " + lyricAddress);
            return lyricAddress;
        } catch (Exception e) {
            Log.i(TAG, "parseJsonWithJSONObject: error");
            return null;
        }
    }

    public interface HttpCallBackListener {
        void onFinish(String response);

        void onError();
    }
}
