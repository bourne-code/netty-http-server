package cn.codingcenter.httpserver.staticresource;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;

public class StaticApplicationHandler extends AbstractStaticResourceHandler {

    public StaticApplicationHandler(FullHttpRequest request, String suffix) {
        super(request, suffix);
    }

    @Override
    public void setResponseContentType() {
        String contentType = "application/x-javascript";
        if("woff".equals(suffix) || "woff2".equals(suffix)) {
            contentType = "application/x-font-woff";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }
}
