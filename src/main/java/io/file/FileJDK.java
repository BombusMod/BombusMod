package io.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FileJDK extends FileIO {

    public FileJDK(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new FileOutputStream(fileName, false);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new FileInputStream(fileName);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void delete() throws IOException {
        new File(fileName).delete();
    }

    @Override
    public void rename(String newName) throws IOException {
        new File(fileName).renameTo(new File(newName));
    }

    @Override
    public long fileSize() {
        return new File(fileName).length();
    }

    @Override
    protected List<File> rootDirs() {
        return Arrays.asList(new File("/").listFiles());
    }

    @Override
    protected Vector dirs(final boolean directoriesOnly) {
        return new Vector(Arrays.asList(new File(fileName).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (directoriesOnly) {
                    return file.isDirectory();
                } else {
                    return true;
                }
            }
        })));
    }

    @Override
    public OutputStream appendOutputStream() throws IOException {
        return new FileOutputStream(fileName, true);
    }
}
