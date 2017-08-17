package cn.codingcenter.httpserver.cache;

import cn.codingcenter.httpserver.datastru.FileCounter;
import cn.codingcenter.httpserver.datastru.RedBlackTree;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCache {


    private RedBlackTree<FileCounter> index = new RedBlackTree<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private static final long MAX_CACHE_BYTES_COUNT = 100 * 1024 * 1024;

    private long cacheByteCount;

    private Map<String, FileCounter> cache = new HashMap<>();

    private int currentByteCount;

    public FileCache() {
        this(MAX_CACHE_BYTES_COUNT);
    }

    public FileCache(long cacheByteCount) {
        this.cacheByteCount = cacheByteCount;
    }



    public FileCounter put(String filename, byte[] fileContent) {
        writeLock.lock();
        FileCounter counter = null;
        currentByteCount += fileContent.length;
        if(currentByteCount < MAX_CACHE_BYTES_COUNT) {
            counter = new FileCounter(fileContent);
            cache.put(filename, counter);
        } else {
            FileCounter minElem = null;
            while (currentByteCount > MAX_CACHE_BYTES_COUNT) {
                minElem = index.getMinElem();
                System.out.println(minElem);
                cache.remove(minElem);
                index.delete(minElem);
                currentByteCount -= minElem.getFileLength();
                minElem = null; // for gc
            }
            counter = new FileCounter(fileContent);
            cache.put(filename, counter);
        }

        writeLock.unlock();
        return counter;
    }

    public FileCounter get(String filename) {
        readLock.lock();
        FileCounter counter = cache.get(filename);
        readLock.unlock();
        return counter;
    }

    public void remove(FileCounter counter) {
        writeLock.lock();
        cache.remove(counter);
        index.delete(counter);
        counter = null;
        writeLock.unlock();
    }
}
