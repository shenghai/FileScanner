package com.gshai.filescanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ScanEntry {

	String getName();
	
	String getPath();

	boolean isLeaf();

	public ScanEntry next() throws IOException;

	InputStream getInputStream() throws IOException;

    Map<String,ScanEntry> getChildrenMap() throws IOException;

    String getRelativePath();

    void close();
}
