package cn.codingcenter.httpserver.staticresource;

import cn.codingcenter.httpserver.cache.FileCache;
import cn.codingcenter.httpserver.constant.ServerConstant;
import cn.codingcenter.httpserver.datastru.FileCounter;
import cn.codingcenter.httpserver.session.HttpSession;
import cn.codingcenter.httpserver.session.HttpSessionManager;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractStaticResourceHandler {


    protected static FileCache FILE_CACHE = new FileCache();

    protected FullHttpRequest request;
    protected FullHttpResponse response;
    protected String suffix;

    protected AbstractStaticResourceHandler(FullHttpRequest request, String suffix) {
        this.request = request;
        this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);;
        this.suffix = suffix;
    }

    public abstract void setResponseContentType();

    protected void setSessonId() {
        if(!hasSessionId()){
            ServerCookieEncoder encoder = ServerCookieEncoder.STRICT;
            String encodedCookie = encoder.encode(HttpSession.SESSIONID, HttpSessionManager.getSessionId());
            response.headers().set(HttpHeaderNames.SET_COOKIE, encodedCookie);
        }
    }

    protected boolean hasSessionId() {
        try {
            String cookieStr = request.headers().get("Cookie");
            if(cookieStr == null) return false;
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            Iterator<Cookie> it = cookies.iterator();

            while(it.hasNext()){
                Cookie cookie = it.next();
                if(cookie.name().equals(HttpSession.SESSIONID)){
                    if(HttpSessionManager.hasSessionId(cookie.value())){
                        response.headers().set(HttpHeaderNames.SET_COOKIE, HttpSession.SESSIONID + "=" + cookie.value());
                        return true;
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public void handleRequest(ChannelHandlerContext ctx) {
        try {
            StringBuffer buffer = new StringBuffer(ServerConstant.WEB_ROOT);
            String uri = request.uri();
            buffer.append(uri);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            setSessonId();
            setResponseContentType();


            File file = new File(buffer.toString());
            if(!file.exists() || file.isHidden()) {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                return;
            }
            if(!file.isFile()) {
                response.setStatus(HttpResponseStatus.FORBIDDEN);
                return;
            }

            String fileETAG = getFileModifiedTime(file);

            FileCounter counter = null;
            if((counter = FILE_CACHE.get(buffer.toString())) != null) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, counter.getFileLength());
                if(fileETAG.equals(request.headers().get(HttpHeaderNames.IF_NONE_MATCH))) {
                    response.setStatus(HttpResponseStatus.NOT_MODIFIED);
                    response.headers().set(HttpHeaderNames.ETAG, fileETAG);
                    counter.increase();
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    return;
                } else {
                    FILE_CACHE.remove(counter);
                }
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = randomAccessFile.length();
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            byte[] bytes = new byte[(int) fileLength];

            randomAccessFile.read(bytes);
            response.content().writeBytes(bytes);
            FILE_CACHE.put(buffer.toString(), bytes);
            response.headers().set(HttpHeaderNames.ETAG, fileETAG);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (FileNotFoundException e) {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            response.content().clear();
            e.printStackTrace();
        } catch (IOException e) {
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.content().clear();
            e.printStackTrace();
        }
    }


    private String getFileModifiedTime(File file) {
        return "\"" + file.lastModified() + "\"";
    }

    public static void setFileCache(FileCache fileCache) {
        FILE_CACHE = fileCache;
    }
}
