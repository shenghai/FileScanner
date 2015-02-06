package com.gshai.filescanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamScanEntry implements ScanEntry {
    private String path;
    private String name;
    private String relativePath;
    private InputStream in;

    public InputStreamScanEntry(String path, InputStream newIn) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.in = newIn;
    }

    public InputStreamScanEntry(String relativePath, String path, InputStream newIn) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.in = newIn;
        this.relativePath = relativePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public Map<String, ScanEntry> getChildrenMap() {
        return new HashMap<String, ScanEntry>();
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public void close() {
//        if (in != null) {
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public ScanEntry next() throws IOException {
        return null;
    }

    @Override
    public String getPath() {
        return path;
    }

}
