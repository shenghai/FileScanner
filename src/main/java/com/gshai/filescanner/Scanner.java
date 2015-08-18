package com.gshai.filescanner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;

public class Scanner {
    private Set<String> exFilePrefixList = new HashSet<String>();
    private Set<String> exFileSuffixList = new HashSet<String>();
    private Set<String> exFileRegExpList = new HashSet<String>();

    private Set<String> exDirPrefixList = new HashSet<String>();
    private Set<String> exDirSuffixList = new HashSet<String>();
    private Set<String> exDirRegExpList = new HashSet<String>();

    private Set<String> inFilePrefixList = new HashSet<String>();
    private Set<String> inFileSuffixList = new HashSet<String>();
    private Set<String> inFileRegExpList = new HashSet<String>();

    private Set<String> inDirPrefixList = new HashSet<String>();
    private Set<String> inDirSuffixList = new HashSet<String>();
    private Set<String> inDirRegExpList = new HashSet<String>();

    private long totalCount = 0;
    private Printer printer;
    private volatile boolean shouldStop = false;

    public Scanner() {
        //default filter
        exDirPrefixList.addAll(Arrays.asList("."));
        exFilePrefixList.addAll(Arrays.asList("."));
        exFileSuffixList.addAll(Arrays.asList(".jar", ".zip", ".war", ".rar"));
    }

    public void scan(String baseDir) {
        ScanEntry entry = new FileScanEntry(baseDir, null);
        try {
            scan(null, entry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        entry.close();
    }

    private void scan(ScanEntry parent, ScanEntry entry) throws Exception {
        if (shouldStop || !filter(entry)) {
            return;
        }
        totalCount++;
        if (entry.isLeaf()) {
            if (CommonUtils.isZipFile(entry.getPath())) {
                ZipScanEntry entry1 = new ZipScanEntry(parent, entry);
                scan(parent, entry1);
                entry1.close();
            } else {
                testMatch(entry);
            }
        } else {
            ScanEntry child;
            while ((child = entry.next()) != null) {
                scan(entry, child);
                child.close();
            }
        }
    }

    public boolean filter(ScanEntry entry) {
        return excludeFilter(entry) && includeFilter(entry);
    }

    private boolean includeFilter(ScanEntry entry) {
        String name = entry.getName();
        if (entry.isLeaf()) {
            boolean result = true;
            if (!inFilePrefixList.isEmpty()) {
                result = false;
                for (String e : inFilePrefixList) {
                    if (name.startsWith(e)) {
                        result = true;
                        break;
                    }
                }
                if (!result) return false;
            }
            if (!inFileSuffixList.isEmpty()) {
                result = false;
                for (String e : inFileSuffixList) {
                    if (name.endsWith(e)) {
                        result = true;
                        break;
                    }
                }
                if (!result) return false;
            }
            if (!inFileRegExpList.isEmpty()) {
                result = false;
                for (String e : inFileRegExpList) {
                    if (name.matches(e)) {
                        return true;
                    }
                }
            }
            return result;
        } else {
            boolean result = true;
            if (!inDirPrefixList.isEmpty()) {
                result = false;
                for (String e : inDirPrefixList) {
                    if (name.startsWith(e)) {
                        result = true;
                        break;
                    }
                }
                if (!result) return false;
            }
            if (!inDirSuffixList.isEmpty()) {
                result = false;
                for (String e : inDirSuffixList) {
                    if (name.endsWith(e)) {
                        result = true;
                        break;
                    }
                }
                if (!result) return false;
            }
            if (!inDirRegExpList.isEmpty()) {
                result = false;
                for (String e : inDirRegExpList) {
                    if (name.matches(e)) {
                        return true;
                    }
                }
            }
            return result;
        }
    }

    private boolean excludeFilter(ScanEntry entry) {
        String name = entry.getName();
        if (entry.isLeaf()) {
            for (String e : exFilePrefixList) {
                if (name.startsWith(e)) {
                    return false;
                }
            }
            for (String e : exFileSuffixList) {
                if (name.endsWith(e)) {
                    return false;
                }
            }
            for (String e : exFileRegExpList) {
                if (name.matches(e)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String e : exDirPrefixList) {
                if (name.startsWith(e)) {
                    return false;
                }
            }
            for (String e : exDirSuffixList) {
                if (name.endsWith(e)) {
                    return false;
                }
            }
            for (String e : exDirRegExpList) {
                if (name.matches(e)) {
                    return false;
                }
            }
            return true;
        }
    }

    public void testMatch(ScanEntry entry) throws Exception {
    }

    public boolean checkLineEqual(String s, int displayLineNumber, ScanEntry entry)
            throws Exception {
        InputStream inputStream = entry.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                inputStream));
        String line = in.readLine();
        int index = 0;
        SmallQueue queue = new SmallQueue(displayLineNumber);
        int bufferOffset = displayLineNumber / 2;
        boolean findMatched = false;
        for (; line != null; index++, line = in.readLine()) {
            if (line.contains("import")) {
                continue;
            }
            queue.push(line);
            if (line.contains(s)) {
                getPrinter().println(String.format("[%5d]%s", index + 1,
                        entry.getPath()));
                findMatched = true;
            }
            if (findMatched) {
                bufferOffset--;
                if (bufferOffset < 0) {
                    for (String temp : queue) {
                        getPrinter().println(String.format("%6d %s", index++ - displayLineNumber + 2,
                                "\t" + (temp == null ? null : temp.trim())));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkMultiLineEqual(ScanEntry entry, String... s)
            throws Exception {
        if (s.length == 1) {
            return checkLineEqual(s[0], 7, entry);
        }
        InputStream inputStream = entry.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                inputStream));
        String line = in.readLine();
        List<String> check = new ArrayList<String>();
        int index = 0;
        boolean[] result = new boolean[s.length];
        for (; line != null; index++, line = in.readLine()) {
            if (line.contains("import")) {
                continue;
            }
            for (int i = 0; i < s.length; i++) {
                String s1 = s[i];
                if (line.contains(s1)) {
                    check.add(line);
                    result[i] = true;
                }
            }
        }
        boolean finalResult = isAllTrue(result);
        if (finalResult) {
            getPrinter().println(entry.getPath());
            for (String s1 : check) {
                getPrinter().println(s1);
            }
        }
        return finalResult;
    }

    private boolean isAllTrue(boolean[] result) {
        boolean re = true;
        for (boolean b : result) {
            re = re && b;
        }
        return re;
    }

    public long getTotalCount() {
        return totalCount;
    }

    /**
     * example: f:exclude:*.jar|*.zip|.*|abc*;f:include:*.txt|*.java;d:exclude:.*;
     *
     * @param nameFilter name filter
     */
    public void addNameFilter(String nameFilter) {
        if (nameFilter == null) return;
        String[] rules = nameFilter.split("\\;");
        for (String rule : rules) {
            if (rule.startsWith("f:exclude:")) {
                String body = rule.substring(10);
                String[] entityList = body.split("\\|");
                for (String entity : entityList) {
                    if (entity.startsWith("*")) {
                        exFileSuffixList.add(entity.substring(1));
                    } else if (entity.endsWith("*")) {
                        exFilePrefixList.add(entity.substring(0, entity.length() - 1));
                    } else {
                        exFileRegExpList.add(entity);
                    }
                }
            } else if (rule.startsWith("f:include:")) {
                String body = rule.substring(10);
                String[] entityList = body.split("\\|");
                for (String entity : entityList) {
                    if (entity.startsWith("*")) {
                        inFileSuffixList.add(entity.substring(1));
                    } else if (entity.endsWith("*")) {
                        inFilePrefixList.add(entity.substring(0, entity.length() - 1));
                    } else {
                        inFileRegExpList.add(entity);
                    }
                }
            } else if (rule.startsWith("d:exclude:")) {
                String body = rule.substring(10);
                String[] entityList = body.split("\\|");
                for (String entity : entityList) {
                    if (entity.startsWith("*")) {
                        exDirSuffixList.add(entity.substring(1));
                    } else if (entity.endsWith("*")) {
                        exDirPrefixList.add(entity.substring(0, entity.length() - 1));
                    } else {
                        exDirRegExpList.add(entity);
                    }
                }
            } else if (rule.startsWith("d:include:")) {
                String body = rule.substring(10);
                String[] entityList = body.split("\\|");
                for (String entity : entityList) {
                    if (entity.startsWith("*")) {
                        inDirSuffixList.add(entity.substring(1));
                    } else if (entity.endsWith("*")) {
                        inDirPrefixList.add(entity.substring(0, entity.length() - 1));
                    } else {
                        inDirRegExpList.add(entity);
                    }
                }
            } else {
                //do nothing
            }
        }


    }

    public Printer getPrinter() {
        if (printer == null) {
            printer = new Printer(){
                public void println(String msg) {
                    System.out.println(msg);
                }
            };
        }
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public void stopScan() {
        shouldStop = true;
    }
}
