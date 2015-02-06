package com.gshai.filescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Comparator {
    public void compare(String srcDir, String desDir) throws Exception {
        ScanEntry srcEntry = new FileScanEntry(srcDir);
        ScanEntry desEntry = new FileScanEntry(desDir);
        compare(srcEntry, desEntry);
    }

    private void compare(ScanEntry srcEntry, ScanEntry desEntry) throws Exception {
        if (!filter(srcEntry)) {
            return;
        }
        if (!srcEntry.getName().equals(desEntry.getName())) {
            logNotMatch(srcEntry, desEntry);
            return;
        }
        if (srcEntry.isLeaf()) {
            if (srcEntry.getPath().endsWith(".jar") || srcEntry.getPath().endsWith(".zip")) {
                compare(new ZipScanEntry(srcEntry), new ZipScanEntry(desEntry));
            } else {
                testMatch(srcEntry, desEntry);
            }
        } else {
            Map<String, ScanEntry> srcChildRen = srcEntry.getChildrenMap();
            Map<String, ScanEntry> desChildRen = desEntry.getChildrenMap();
            if (srcChildRen.size() != desChildRen.size()) {
                logNotMatch(srcEntry, desEntry);
            }
            for (Map.Entry<String, ScanEntry> srcChildEntry : srcChildRen.entrySet()) {
                ScanEntry srcChild = srcChildEntry.getValue();
                ScanEntry desChild = desChildRen.get(srcChildEntry.getKey());
                if (desChild == null) {
                    logNotMatch(srcChild, null);
                    continue;
                }
                compare(srcChild, desChild);
            }
        }
    }

    private void testMatch(ScanEntry srcEntry, ScanEntry desEntry) throws IOException {
        List<String> srcLines = getLines(srcEntry);
        List<String> desLines = getLines(desEntry);
        if (srcLines.size() != desLines.size()) {
            logNotMatch(srcEntry, desEntry);
        }
        for (int i = 0; i < srcLines.size(); i++) {
            String srcLine = srcLines.get(i);
            String desLine = desLines.get(i);
            if (!srcLine.equals(desLine)) {
                logNotMatch(srcEntry, desEntry);
            }
        }
    }

    private List<String> getLines(ScanEntry entry) throws IOException {
        InputStream inputStream = entry.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                inputStream));
        List<String> result = new ArrayList<String>();
        String line = in.readLine();
        for (; line != null; line = in.readLine()) {
            result.add(line);
        }
        inputStream.close();
        return result;
    }

    private void logNotMatch(ScanEntry srcEntry, ScanEntry desEntry) {
        if (srcEntry.getName().equals("pom.properties")) {
            return;
        }
        System.out.println(String.format("==>%s \n   %s   are not equals", srcEntry.getPath(), desEntry == null ? null : desEntry.getPath()));
    }

    public boolean filter(ScanEntry entry) {
        return true;
    }
}
