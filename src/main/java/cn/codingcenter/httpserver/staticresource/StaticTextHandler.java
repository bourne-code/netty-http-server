package cn.codingcenter.httpserver.staticresource;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;

public class StaticTextHandler extends AbstractStaticResourceHandler {

    public StaticTextHandler(FullHttpRequest request, String suffix) {
        super(request, suffix);
    }

    @Override
    public void setResponseContentType() {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/" + suffix + ";charset=utf-8");
    }
}
