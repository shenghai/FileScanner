package com.gshai.filescanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileScanEntry implements ScanEntry {
    private final File file;
    private int index = 0;
    private File[] children;

    private String name;
    private String path;
    private String relativePath;
    private FileInputStream fileInputStream;


    public FileScanEntry(String baseDir) throws IOException {
        this(new File(baseDir));
    }

    public FileScanEntry(File file) throws IOException {
        this(null, file);
    }

    public FileScanEntry(ScanEntry parent, File file) {
        this.file = file;
        this.name = file.getName();
        this.path = file.getPath();
        if (parent != null && parent.getRelativePath() != null) {
            this.relativePath = parent.getRelativePath() + "/" + this.name;
        } else {
            this.relativePath = this.name;
        }
    }

    public FileScanEntry(String baseDir, String relativePath) {
        this.file = new File(baseDir);
        this.name = file.getName();
        this.path = file.getPath();
        this.relativePath = relativePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isLeaf() {
        return file.isFile();
    }

//	@Override
//	public List<ScanEntry> listChildren() throws IOException {
//		File[] files = file.listFiles();
//		List<ScanEntry> result = new ArrayList<ScanEntry>();
//		for (File file : files) {
//			result.add(new FileScanEntry(file));
//		}
//		return result;
//	}

    @Override
    public InputStream getInputStream() throws IOException {
        if (fileInputStream == null) {
            fileInputStream = new FileInputStream(file);
        }
        return fileInputStream;
    }

    @Override
    public Map<String, ScanEntry> getChildrenMap() throws IOException {
        if (children == null) {
            children = file.listFiles();
        }
        Map<String, ScanEntry> result = new HashMap<String, ScanEntry>();
        for (File child : children) {
            result.put(child.getName(), new FileScanEntry(this, child));
        }
        return result;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public ScanEntry next() throws IOException {
        if (children == null) {
            children = file.listFiles();
        }
        if (children == null || index >= children.length) {
            return null;
        }
        return new FileScanEntry(this, children[index++]);
    }


    @Override
    public void close() {
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
