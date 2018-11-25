package com.example.asus.syoucloud.GSON;

import java.util.List;

public class LyricResult {
    int code;
    int count;
    List<LyricDetail> result;

    public int getCode() {
        return code;
    }

    public int getCount() {
        return count;
    }

    public List<LyricDetail> getResult() {
        return result;
    }
}
