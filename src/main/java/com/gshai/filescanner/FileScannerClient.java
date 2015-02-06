package com.gshai.filescanner;

import java.io.PrintStream;

/**
 * Created by Shenghai.Geng on 2015/2/6.
 */
public class FileScannerClient {
    public static void findFilesByName(String beginPath, final String fileName, final PrintStream printStream) {
        findFilesByName(beginPath, fileName, printStream, null);
    }

    public static void findFilesByName(String beginPath, final String fileName, final PrintStream printStream, String nameFilter) {
        Scanner scanner = new Scanner() {
            @Override
            public void testMatch(ScanEntry entry) throws Exception {
                if (entry.getName().equals(fileName)) {
                    getPrintStream().println(entry.getPath());
                }
            }
        };
        scanner.addNameFilter(nameFilter);
        long start = System.currentTimeMillis();
        scanner.scan(beginPath);
        long end = System.currentTimeMillis();
        printStream.println("Scanner takes " + (end - start) + " ms to search " + scanner.getTotalCount() + " files");
    }

    public static void findFilesByContext(String beginPath, final String context, final PrintStream printStream) {
        findFilesByContext(beginPath, context, printStream, null);
    }

    public static void findFilesByContext(String beginPath, final String context, final PrintStream printStream, String nameFilter) {
        Scanner scanner = new Scanner() {
            @Override
            public void testMatch(ScanEntry entry) throws Exception {
                if (checkMultiLineEqual(entry, context)) {
                    getPrintStream().println(entry.getPath());
                }
            }
        };
        scanner.addNameFilter(nameFilter);
        scanner.setPrintStream(printStream);
        long start = System.currentTimeMillis();
        scanner.scan(beginPath);
        long end = System.currentTimeMillis();
        printStream.println("Scanner takes " + (end - start) + " ms to search " + scanner.getTotalCount() + " files");
    }
}
