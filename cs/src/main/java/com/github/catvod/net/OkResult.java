package com.github.catvod.net;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return Optional.ofNullable(body).orElse("");

    }

    public Map<String, List<String>> getResp() {
        return resp;
    }
}
