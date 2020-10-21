package com.ticket_pipeline.simple_utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * данный класс перебирает все ресурсы заканчивающиеся на fileSuffix, даже если они будут в Jar файле.
 */
public class ClassPathResourcesUtil {
    private ClassPathResourcesUtil() {
    }

    public static String getFileContent(String resourceRelatedFilePath) {
        return getContent(getFileInputStream(resourceRelatedFilePath));
    }

    public static InputStream getFileInputStream(String resourceRelatedFilePath) {
        return getDirectoryInputStreams(resourceRelatedFilePath, null).stream().findFirst().orElse(null);
    }

    public static List<InputStream> getDirectoryInputStreams(String resourceRelatedFilePath) {
        return getDirectoryInputStreams(resourceRelatedFilePath, null);
    }

    public static List<String> getDirectoryContent(String resourceRelatedFilePath, String fileSuffix) {
        return getDirectoryInputStreams(resourceRelatedFilePath, fileSuffix).stream()
                .map(ClassPathResourcesUtil::getContent)
                .collect(Collectors.toList());
    }

    public static List<String> getDirectoryContent(String resourceRelatedFilePath) {
        return getDirectoryInputStreams(resourceRelatedFilePath).stream()
                .map(ClassPathResourcesUtil::getContent)
                .collect(Collectors.toList());
    }

    public static List<InputStream> getDirectoryInputStreams(String resourceRelatedFilePath, String fileSuffix) {
        try {
            if (fileSuffix == null) {
                fileSuffix = resourceRelatedFilePath;
            }
            fileSuffix = normalizeString(fileSuffix);
            Enumeration<URL> urlEnumeration = ClassPathResourcesUtil.class.getClassLoader().getResources(resourceRelatedFilePath);
            List<InputStream> result = new ArrayList<>();
            while (urlEnumeration.hasMoreElements()) {
                result.addAll(getContentString(urlEnumeration.nextElement(), fileSuffix));
            }
            return result;
        } catch (Exception e) {
            throw new ResourceResolveException("Can't get content from recourse: "
                    + resourceRelatedFilePath + " cause: " + e.getMessage(), e);
        }
    }

    private static List<InputStream> getContentString(URL url, String fileSuffix) throws IOException, URISyntaxException {
        switch (url.getProtocol()) {
            case "jar":
                return getJarFileContentString(url, fileSuffix);
            case "file":
                return getFileContentString(url, fileSuffix);
            default:
                return Collections.emptyList();
        }
    }

    private static List<InputStream> getJarFileContentString(URL url, String fileSuffix) throws IOException {
        JarURLConnection urlcon = (JarURLConnection) (url.openConnection());
        List<InputStream> contents = new ArrayList<>();
        try (JarFile jarFile = urlcon.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (normalizeString(entry.getName()).endsWith(fileSuffix)) {
                    JarEntry fileEntry = jarFile.getJarEntry(entry.getName());
                    contents.add(new InputStreamWrapper(jarFile.getInputStream(fileEntry)));
                }
            }
        }
        return contents;
    }

    private static List<InputStream> getFileContentString(URL url, String fileSuffix) throws IOException, URISyntaxException {
        List<File> files = files(new File(url.toURI()));
        List<InputStream> contents = new ArrayList<>();
        for (File file : files) {
            if (normalizeString(file.getPath()).endsWith(fileSuffix)) {
                contents.add(new InputStreamWrapper(new FileInputStream(file)));
            }
        }
        return contents;
    }

    private static List<File> files(File f) {
        if (f == null || !f.exists()) {
            return Collections.emptyList();
        }
        if (!f.isDirectory()) {
            return Collections.singletonList(f);
        } else {
            if (f.listFiles() == null) {
                return Collections.emptyList();
            }
            List<File> result = new ArrayList<>();
            for (File innerFile : Objects.requireNonNull(f.listFiles())) {
                result.addAll(files(innerFile));
            }
            return result;
        }
    }

    private static String getContent(InputStream is) {
        if (is == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            List<String> content = new ArrayList<>();
            String s;
            while ((s = reader.readLine()) != null) {
                content.add(s);
            }
            return String.join(System.lineSeparator(), content);
        } catch (Exception e) {
            throw new ResourceResolveException("Can't get content from recourse:  cause: " + e.getMessage(), e);
        }
    }

    private static String normalizeString(String s) {
        return s.replace("/", "").replace("\\", "");
    }

    public static List<String> getDirectoryFiles(String resourceRelatedFilePath) {
        try {
            Enumeration<URL> urlEnumeration = ClassPathResourcesUtil.class.getClassLoader().getResources(resourceRelatedFilePath);
            List<String> result = new ArrayList<>();
            while (urlEnumeration.hasMoreElements()) {
                result.addAll(getDirectoryFiles(urlEnumeration.nextElement()));
            }
            return result;
        } catch (Exception e) {
            throw new ResourceResolveException("Can't get content from recourse: "
                    + resourceRelatedFilePath + " cause: " + e.getMessage(), e);
        }
    }

    private static List<String> getDirectoryFiles(URL url) throws IOException, URISyntaxException {
        switch (url.getProtocol()) {
            case "jar":
                return getJarFileDirectoryFiles(url);
            case "file":
                return getFileDirectoryFiles(url);
            default:
                return Collections.emptyList();
        }
    }

    private static List<String> getJarFileDirectoryFiles(URL url) throws IOException {
        JarURLConnection urlcon = (JarURLConnection) (url.openConnection());
        List<String> contents = new ArrayList<>();
        try (JarFile jarFile = urlcon.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                contents.add(entry.getName());
            }
        }
        return contents;
    }

    private static List<String> getFileDirectoryFiles(URL url) throws URISyntaxException {
        List<File> files = files(new File(url.toURI()));
        List<String> contents = new ArrayList<>();
        for (File file : files) {
            contents.add(file.getPath());
        }
        return contents;
    }

    private static class InputStreamWrapper extends InputStream {
        private final ByteArrayInputStream inputStream;

        InputStreamWrapper(InputStream inputStream) {
            try (BufferedInputStream is = new BufferedInputStream(inputStream)) {
                byte[] content = new byte[is.available()];
                is.read(content);
                this.inputStream = new ByteArrayInputStream(content);
            } catch (IOException e) {
                throw new ResourceResolveException("Can't read InputStream cause: " + e.getMessage(), e);
            }
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return inputStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public long skip(long n) throws IOException {
            return inputStream.skip(n);
        }

        @Override
        public synchronized void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }
    }

    public static class ResourceResolveException extends RuntimeException {
        private static final long serialVersionUID = 5891604495522746035L;

        public ResourceResolveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
