package com.example.Gson;

import java.util.List;

public class LyricInfo {
    private int count;
    private int code;
    private List<LyricResult> result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<LyricResult> getResult() {
        return result;
    }

    public void setResult(List<LyricResult> result) {
        this.result = result;
    }
}
