package com.gshai.filescanner;

import org.junit.Test;

public class FileScannerClientTest {
    @Test
    public void findFilesByName() {
        FileScannerClient.findFilesByName("/", "notes.org", System.out);
    }

    @Test
    public void findFilesByName2() {
        FileScannerClient.findFilesByName("/", "notes.org", System.out, "d:exclude:.*");
    }

    @Test
    public void findFilesByContext() {
        FileScannerClient.findFilesByContext("/shenghai", "passwd", System.out);
    }

    @Test
    public void findFilesByContext2() {
        FileScannerClient.findFilesByContext("/shenghai", "passwd", System.out, "f:include:*.org");
    }
}