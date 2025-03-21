/*
 * @Author: catvod
 * @Date: 2025-03-10 12:25:31
 * @LastEditTime: 2025-03-10 14:03:27
 * @LastEditors: catvod
 * @Description: 描述
 * @FilePath: /catvod/app/src/main/java/com/github/catvod/spider/Proxy.java
 * 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
 */
package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.live.TxtSubscribe;
import com.github.catvod.net.OkHttp;
import com.github.catvod.parser.MixDemo;
import com.github.catvod.parser.MixWeb;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class Proxy extends Spider {

    public static int localPort = -1;

    static void adjustLocalPort() {
        if (localPort > 0)
            return;
        int port = 9978;
        while (port < 10000) {
            String resp = OkHttp.string("http://127.0.0.1:" + port + "/proxy?do=ck", null);
            if (resp.equals("ok")) {
                SpiderDebug.log("Found local server port " + port);
                localPort = port;
                break;
            }
            port++;
        }
    }

    public static String localProxyUrl() {
        adjustLocalPort();
        return "http://127.0.0.1:" + Proxy.localPort + "/proxy";
    }

    public static Object[] proxy(Map<String, String> params) {
        try {
            String what = params.get("do");
            if (what.equals("live")) {
                String type = params.get("type");
                if (type.equals("txt")) {
                    String ext = params.get("ext");
                    if (!ext.startsWith("http")) {
                        ext = new String(Base64.decode(ext, Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
                    }
                    return TxtSubscribe.load(ext);
                }
            } else if (what.equals("ck")) {
                Object[] result = new Object[3];
                result[0] = 200;
                result[1] = "text/plain; charset=utf-8";
                ByteArrayInputStream baos = new ByteArrayInputStream("ok".getBytes("UTF-8"));
                result[2] = baos;
                return result;
            } else if (what.equals("MixDemo")) {
                return MixDemo.loadHtml(params.get("flag"), params.get("url"));
            } else if (what.equals("MixWeb")) {
                return MixWeb.loadHtml(params.get("flag"), params.get("url"));
            } 
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }
}
