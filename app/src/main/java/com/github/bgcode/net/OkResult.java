package com.github.bgcode.net;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FongMi
 * <a href="https://github.com/FongMi/CatVodSpider">CatVodSpider</a>
 */
public class OkResult {

    private final int code;
    private final String body;
    private final Map<String, List<String>> resp;

    public OkResult() {
        this.code = 500;
        this.body = "";
        this.resp = new HashMap<>();
    }

    public OkResult(int code, String body, Map<String, List<String>> resp) {
        this.code = code;
        this.body = body;
        this.resp = resp;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return TextUtils.isEmpty(body) ? "" : body;
    }

    public Map<String, List<String>> getResp() {
        return resp;
    }
}
