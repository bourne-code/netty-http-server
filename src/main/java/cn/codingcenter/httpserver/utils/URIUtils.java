package cn.codingcenter.httpserver.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URIUtils {
    private static final Pattern pattern = Pattern.compile("\\S*[?]\\S*");
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    public static String sanitizeUri(String uri) throws UnsupportedEncodingException {
        uri = uri.split("\\?")[0];
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new UnsupportedEncodingException();
            }
        }
        return uri;
    }

    public static String getSuffix(String uri) {
        Matcher matcher = pattern.matcher(uri);

        String[] spUrl = uri.split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if(matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            String[] t = spEndUrl[0].split("\\.");
            return t[t.length - 1];
        }
        String[] t = endUrl.split("\\.");
        return t[t.length - 1];
    }
}
