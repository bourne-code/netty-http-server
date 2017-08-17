package cn.codingcenter.httpserver.constant;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ServerConstant {

    private static final String WEB_ROOT_NAME = "web.root.path";
    private static final String REMOTE_ADDR_NAME = "remote.addr";
    private static final String REMOTE_PORT_NAME = "remote.port";
    private static final String FILE_CACHE_SIZE_NAME = "file.cache.size";
    private static final String LOCAL_ADDR_NAME = "local.addr";
    private static final String LOCAL_PORT_NAME = "local.port";

    private static final Map<String, String> CONFIG = new HashMap<>(7);


    private static final String CONF_FILE_PAHT = "/conf/server.xml";
    public static final String WEB_ROOT;
    public static final String REMOTE_ADDR;
    public static final String LOCAL_ADDR;
    public static final int REMOTE_PORT;
    public static final int LOCAL_PORT;
    public static final int FILE_CACHE_SIZE;


    static {
        initServer();
        WEB_ROOT = CONFIG.get(WEB_ROOT_NAME);
        REMOTE_ADDR = CONFIG.get(REMOTE_ADDR_NAME);
        REMOTE_PORT = Integer.parseInt(CONFIG.get(REMOTE_PORT_NAME));
        LOCAL_ADDR = CONFIG.get(LOCAL_ADDR_NAME);
        LOCAL_PORT = Integer.parseInt(CONFIG.get(LOCAL_PORT_NAME));
        FILE_CACHE_SIZE = Integer.parseInt(CONFIG.get(FILE_CACHE_SIZE_NAME));
    }

    private static final void initServer() {
        String path = ServerConstant.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path.replaceAll("\\\\", "/");
        int index = path.lastIndexOf("/");
        path = path.substring(0, index);
        index = path.lastIndexOf("/");
        path = path.substring(0, index);
        File myXML = new File(path + CONF_FILE_PAHT);
        SAXReader sr = new SAXReader();
        try {
            Document doc = sr.read(myXML);
            Element root = doc.getRootElement();
            Iterator<Element> property = root.elementIterator("property");
            while (property.hasNext()) {
                Element element = property.next();
                String name = element.elementText("name");
                String value = element.elementText("value");
                CONFIG.put(name, value);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
