package com.github.catvod.utils;

public class M {
    public static String url(String url,String type) {
        if(type.equalsIgnoreCase("list")) {
            url+="?ac=list";
        }else if(type.equalsIgnoreCase("detail")) {
            url+="?ac=detail";
        }else if(type.equalsIgnoreCase("search")) {
            url+="?ac=list&wd=";
        }
        return url;
    }

}
