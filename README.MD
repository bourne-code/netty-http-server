# 基于Netty实现的Http服务器
之前用Java NIO实现过一个简单的Http服务器，不过用原生NIO实现起来比较复杂，
而且有许多大大小小的问题，所以之后使用比较流行的开源网络通信框架Netty对服务器进行了改进。

## 编译和运行环境
依赖JDK1.8，因为程序在某些地方使用了jdk1.8中的Lambda表达式

## 文件缓冲
用java提供的读写锁实现了一个简单的文件缓冲池，对文件的访问量进行统计。
池容量不足时，将访问量最少的文件从池中移除

    public void put(String filename, byte[] fileContent) {
        writeLock.lock();
        currentByteCount += fileContent.length;
        if(currentByteCount < MAX_CACHE_BYTES_COUNT) {
            cache.put(filename, new FileCounter(fileContent));
        } else {
            FileCounter minElem = null;
            while (currentByteCount > MAX_CACHE_BYTES_COUNT) {
                minElem = index.getMinElem();
                cache.remove(minElem);
                index.delete(minElem);
                currentByteCount -= minElem.getFileLength();
            }
            cache.put(filename, new FileCounter(fileContent));
        }

        writeLock.unlock();
    }

## 请求响应
根据请求的不同文件类型，交给不同的处理者进行处理

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        request.setUri(URIUtils.sanitizeUri(request.uri()));
        if(request.uri().endsWith("/")) {
            request.setUri(request.uri() + "index.html");
        }
        String suffix = URIUtils.getSuffix(request.uri());
        if(STATIC_TEXT_SUFFIX.contains(suffix)) {
            new StaticTextHandler(request, suffix, ctx).handleRequest();
        } else if(STATIC_APPLICATION_SUFFIX.contains(suffix)) {
            new StaticApplicationHandler(request, suffix, ctx).handleRequest();
        } else if (STATIC_IMAGE_SUFFIX.contains(suffix)) {
            new StaticImageHandler(request, suffix, ctx).handleRequest();
        }
    }
## 服务器设置
ServerConstant类定义了部分服务器设置，这些设置使用配置文件的方式加载，需要在创建一个项目的同级目录conf，在conf中创建一个server.xml文件，程序在运行时会解析配置文件并加载配置

    public final class ServerConstant {
        public static final String WEB_ROOT = "F:/webapps";
        public static final String REMOTE_ADDR = "128.0.9.107";
        public static final String LOCAL_ADDR = "127.0.0.1";
        public static final int REMOTE_PORT = 8088;
        public static final int LOCAL_PORT = 8088;
        public static final int FILE_CACHE_SIZE = 50 * 1024 * 1024;
    }
    
sever.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <properties>
        <property>
            <name>remote.addr</name>
            <value>128.0.9.107</value>
        </property>
        <property>
            <name>remote.port</name>
            <value>80</value>
        </property>
        <property>
            <name>file.cache.size</name>
            <value>52428800</value>
        </property>
        <property>
            <name>local.addr</name>
            <value>127.0.0.1</value>
        </property>
        <property>
            <name>local.port</name>
            <value>80</value>
        </property>
        <property>
            <name>web.root.path</name>
            <value>F:/webapps</value>
        </property>
    </properties>

## 支持的文件类型

这个服务器只能支持静态资源的访问，有兴趣的道友也可以添加动态资源的访问。
支持的文件类型在HttpServerHandler里面定义，可以进行扩展

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
