package com.gshai.filescanner;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

public class CommonUtils {
    public static Printer printStreamToPrinter(final PrintStream printStream) {
        return new Printer() {
            private PrintStream ps = printStream;
            public void println(String msg) {
                ps.println(msg);
            }
        };
    }
    public static boolean isZipFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        if (!new File(filePath).isFile()) {
            return false;
        }
        return filePath.endsWith(".jar") || filePath.endsWith(".zip") || filePath.endsWith("rar")
                || filePath.endsWith("war") || filePath.endsWith("ear");
    }

    public static String[] splitFirstTwo(String source, String key) {
        if (source == null) {
            return null;
        }
        int index = source.indexOf(key);
        if (index == -1) {
            return new String[]{source, null};
        }
        String a = source.substring(0, index);
        String b = source.substring(index + key.length());
        return new String[]{a, b};
    }

    public static String[] splitLastTwo(String source, String key) {
        if (source == null) {
            return null;
        }
        int index = source.lastIndexOf(key);
        if (index == -1) {
            return new String[]{source, null};
        }
        String a = source.substring(0, index);
        String b = source.substring(index + key.length());
        return new String[]{a, b};
    }

    public static String[] splitTwoWithKey1Key2ByLast(String source, String key1, String key2) {
        if (source == null) {
            return null;
        }
        String left;
        String right;
        int index = findLastKeyIndex(source, key1, key1, null);

        if (index > -1) {
            left = source.substring(0, index);
            right = source.substring(index + key1.length());
        } else {
            index = findLastKeyIndex(source, key2, null, key1);
            if (index > -1) {
                left = source.substring(0, index);
                right = source.substring(index + key2.length());
            } else {
                left = source;
                right = null;
            }
        }
        return new String[]{removeEscape(left, key1, key1), removeEscape(right, key1, key1)};
    }

    public static String[] splitTwoWithKey1Key2ByFirst(String source, String key1, String key2) {
        if (source == null) {
            return null;
        }
        String left;
        String right;
        int index = findFirstKeyIndex(source, key1, key1, null);

        if (index > -1) {
            left = source.substring(0, index);
            right = source.substring(index + key1.length());
        } else {
            index = findFirstKeyIndex(source, key2, null, key1);
            if (index > -1) {
                left = source.substring(0, index);
                right = source.substring(index + key2.length());
            } else {
                left = source;
                right = null;
            }
        }
        return new String[]{removeEscape(left, key1, key1), removeEscape(right, key1, key1)};
    }

    private static String removeEscape(String source, String key, String escape) {
        if (source == null) {
            return null;
        }
        return source.replace(escape + key, key);
    }

    private static int findLastKeyIndex(String source, String key, String escape, String ignore) {
        if (escape == null && ignore == null) {
            return source.lastIndexOf(key);
        }
        String tempSource = source;
        if (escape != null) {
            char[] temp = new char[escape.length() + key.length()];
            Arrays.fill(temp, '_');
            tempSource = tempSource.replace(escape + key, new String(temp));
        }
        if (ignore != null) {
            char[] temp = new char[ignore.length()];
            Arrays.fill(temp, '_');
            tempSource = tempSource.replace(ignore, new String(temp));
        }
        return tempSource.lastIndexOf(key);
    }

    private static int findFirstKeyIndex(String source, String key, String escape, String ignore) {
        if (escape == null && ignore == null) {
            return source.indexOf(key);
        }
        String tempSource = source;
        if (escape != null) {
            char[] temp = new char[escape.length() + key.length()];
            Arrays.fill(temp, '_');
            tempSource = tempSource.replace(escape + key, new String(temp));
        }
        if (ignore != null) {
            char[] temp = new char[ignore.length()];
            Arrays.fill(temp, '_');
            tempSource = tempSource.replace(ignore, new String(temp));
        }
        return tempSource.indexOf(key);
    }
}
