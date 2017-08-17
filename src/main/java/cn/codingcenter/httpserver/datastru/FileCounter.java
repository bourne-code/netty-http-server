package cn.codingcenter.httpserver.datastru;

public class FileCounter implements Comparable<FileCounter> {

    private byte[] fileContent;
    private long counter;
    private long fileLength;


    public FileCounter(byte[] fileContent) {
        this.fileContent = fileContent;
        this.fileLength = fileContent.length;
    }

    public byte[] getFileContent() {
        return fileContent;
    }


    public long getCounter() {
        return counter;
    }

    public void increase() {
        counter++;
    }

    public long getFileLength() {
        return fileLength;
    }

    @Override
    public int compareTo(FileCounter o) {
        if(counter > o.counter)
            return 1;
        else if(counter < o.counter)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return "[ " + counter + " ]";
    }
}
