package cn.codingcenter.httpserver.handler;

import cn.codingcenter.httpserver.cache.FileCache;
import cn.codingcenter.httpserver.constant.ServerConstant;
import cn.codingcenter.httpserver.staticresource.AbstractStaticResourceHandler;
import cn.codingcenter.httpserver.staticresource.StaticApplicationHandler;
import cn.codingcenter.httpserver.staticresource.StaticImageHandler;
import cn.codingcenter.httpserver.staticresource.StaticTextHandler;
import cn.codingcenter.httpserver.utils.URIUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;



public class HttpServerHandler extends ChannelInboundHandlerAdapter {


    private static final FileCache FILE_CACHE = new FileCache(ServerConstant.FILE_CACHE_SIZE);

    private static final Set<String> STATIC_IMAGE_SUFFIX = new TreeSet<>();
    private static final Set<String> STATIC_TEXT_SUFFIX = new TreeSet<>();
    private static final Set<String> STATIC_APPLICATION_SUFFIX = new TreeSet<>();
    static {
        STATIC_TEXT_SUFFIX.addAll(Arrays.asList(new String[]{
                "html", "htm", "css"
        }));
        STATIC_APPLICATION_SUFFIX.addAll(Arrays.asList(new String[]{
                "js", "woff", "woff2"
        }));
        STATIC_IMAGE_SUFFIX.addAll(Arrays.asList(new String[]{
                "jpg", "jpeg", "gif", "ico", "png", "svg"
        }));

        AbstractStaticResourceHandler.setFileCache(FILE_CACHE);
    }

    private AbstractStaticResourceHandler handler;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest) {

            FullHttpRequest request = (FullHttpRequest) msg;
            request.setUri(URIUtils.sanitizeUri(request.uri()));

            if (request.uri().endsWith("/")) {
                request.setUri(request.uri() + "index.html");
            }
            String suffix = URIUtils.getSuffix(request.uri());
            if (STATIC_TEXT_SUFFIX.contains(suffix)) {
                handler =  new StaticTextHandler(request, suffix);
            } else if (STATIC_APPLICATION_SUFFIX.contains(suffix)) {
                handler = new StaticApplicationHandler(request, suffix);
            } else if (STATIC_IMAGE_SUFFIX.contains(suffix)) {
                handler =  new StaticImageHandler(request, suffix);
            }
            handler.handleRequest(ctx);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
