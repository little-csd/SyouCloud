package com.example.Gson.Music;

import java.util.List;

public class MusicResult {
    private String result;
    private int code;
    private List<MusicResultItem> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<MusicResultItem> getData() {
        return data;
    }

    public void setData(List<MusicResultItem> data) {
        this.data = data;
    }
}
