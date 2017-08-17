package cn.codingcenter.httpserver.session;

import java.util.UUID;

public class HttpSession {
    public static final String SESSIONID = "SESSIONID";

    private String sessionId;

    public HttpSession() {
        sessionId = UUID.randomUUID().toString();
    }

    public String getSessionId() {
        return sessionId;
    }
}
