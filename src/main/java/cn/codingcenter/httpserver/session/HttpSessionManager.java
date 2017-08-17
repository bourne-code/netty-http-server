package cn.codingcenter.httpserver.session;

import java.util.HashMap;

public class HttpSessionManager {
    private static final HashMap<String,HttpSession> sessionMap = new HashMap<>();

    /**
     * 创建一个session并返回sessionId
     */
    public  static String getSessionId(){
        synchronized (sessionMap) {
            HttpSession httpSession = new HttpSession();
            sessionMap.put(httpSession.getSessionId(), httpSession);
            return httpSession.getSessionId();
        }
    }
    /**
     * 判断服务器是否包含该客户端的session信息
     */
    public static boolean hasSessionId(String sessiondId){
        synchronized (sessionMap) {
            return sessionMap.containsKey(sessiondId);
        }
    }
}
