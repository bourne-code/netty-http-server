package cn.codingcenter.httpserver.staticresource;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;

public class StaticImageHandler extends AbstractStaticResourceHandler {

    public StaticImageHandler(FullHttpRequest request, String suffix) {
        super(request, suffix);
    }

    @Override
    public void setResponseContentType() {
        String contentType = "image/" + suffix;
        if(suffix.equals("svg")) {
            contentType = "image/svg+xml";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }
}
