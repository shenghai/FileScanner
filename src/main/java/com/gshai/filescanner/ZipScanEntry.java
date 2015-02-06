package com.gshai.filescanner;

//import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipScanEntry implements ScanEntry {
    private final String path;
    private final String name;
    private final String relativePath;
    private ZipInputStream in;

    public ZipScanEntry(ScanEntry entry) throws IOException {
        this(null, entry);
    }

    public ZipScanEntry(ScanEntry parent, ScanEntry entry) throws IOException {
        this.path = entry.getPath();
        this.name = entry.getName();
        in = new ZipInputStream(entry.getInputStream());
        if (parent != null && parent.getRelativePath() != null) {
            this.relativePath = parent.getRelativePath() + "/" + this.name;
        } else {
            this.relativePath = this.name;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public ScanEntry next() throws IOException {
        ZipEntry zipEntry = in.getNextEntry();
        if (zipEntry == null) {
            return null;
        }
        String path = this.path + "!/" + zipEntry.getName();
        String relativePath = this.relativePath + "!/" + zipEntry.getName();
        return new InputStreamScanEntry(relativePath, path, in);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public Map<String, ScanEntry> getChildrenMap() throws IOException {
//        ZipEntry zipEntry = in.getNextEntry();
//        Map<String, ScanEntry> result = new HashMap<String, ScanEntry>();
//        while (zipEntry != null) {
//            String path = this.path + "!/" + zipEntry.getName();
//            InputStreamScanEntry inputStreamScanEntry = new InputStreamScanEntry(path, new ByteArrayInputStream(IOUtils.toByteArray(in)));
//            result.put(inputStreamScanEntry.getName(), inputStreamScanEntry);
//            zipEntry = in.getNextEntry();
//        }
//        return result;
        return null;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getPath() {
        return path;
    }

}
