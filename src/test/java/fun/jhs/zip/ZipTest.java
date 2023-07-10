package fun.jhs.zip;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipTest {
    @Test
    public Path zipSingleFile() throws IOException {
        Path path = Paths.get("d:\\test-files\\zip-test\\zip-single-file");
        if(!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        Path file = Files.createTempFile(path, "test.temp_", ".txt");
        try (OutputStream outputStream = Files.newOutputStream(file);) {
            outputStream.write("This is a file for test.".getBytes());
        }
        Path compress = ZipUtils.compress(file);
        file.toFile().delete();
        System.out.println(compress);
        return compress;
    }

    @Test
    public Path zipDirectory() throws IOException {
        Path path = Paths.get("d:\\test-files\\zip-test\\zip-directory");
        if(!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        Path directory = Files.createTempDirectory(path, "temp_");
        Path file1Path = Files.createTempFile(directory, "temp_", ".txt");
        Path file2Path = Files.createTempFile(directory, "temp_", ".txt");
        try(OutputStream os1 = Files.newOutputStream(file1Path);
            OutputStream os2 = Files.newOutputStream(file2Path);) {
            os1.write("File 1 For Test.".getBytes());
            os2.write("File 2 For Test.".getBytes());
        }
        Path compress = ZipUtils.compress(directory);
        ZipUtils.deleteDirectory(directory);
        System.out.println(compress);
        return compress;
    }
    @Test
    public Path zipDeepDirectory() throws IOException {
        Path path = Paths.get("d:\\test-files\\zip-test\\zip-directory");
        if(!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        Path root = Files.createTempDirectory(path, "temp_");
        Path filePath = Files.createTempFile(root, "temp_", ".txt");
        Path dirPath = Files.createTempDirectory(root, "temp_");
        Path fileInDirPath = Files.createTempFile(dirPath, "temp_", ".txt");
        try(OutputStream os1 = Files.newOutputStream(filePath);
            OutputStream os2 = Files.newOutputStream(fileInDirPath);) {
            os1.write("File 1 For Test.".getBytes());
            os2.write("File 2 For Test.".getBytes());
        }
        Path compress = ZipUtils.compress(root);
        ZipUtils.deleteDirectory(root);
        System.out.println(compress);
        return compress;
    }
    @Test
    public Path zipEmptyDirectory() throws IOException {
        Path path = Paths.get("d:\\test-files\\zip-test\\zip-directory");
        if(!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        Path root = Files.createTempDirectory(path, "temp_");
        Path dir = Files.createTempDirectory(root, "temp_");
        Path compress = ZipUtils.compress(root);
        ZipUtils.deleteDirectory(root);
        System.out.println(compress);
        return compress;
    }
    @Test
    public void seeInputFromZipInputStream() throws IOException {
        Path path = zipDeepDirectory();
        try(InputStream is = Files.newInputStream(path);
            ZipInputStream zis = new ZipInputStream(is);) {
            System.out.println("Available: "+zis.available());
            ZipEntry nextEntry = zis.getNextEntry();
            while(!Objects.isNull(nextEntry)) {
                System.out.println("Next Entry: "+nextEntry.getName());
                nextEntry = zis.getNextEntry();
            }
        }
    }
    @Test
    public void seeInputFromZipInputStreamWithEmptyDirectory() throws IOException {
        Path path = zipEmptyDirectory();
        try(InputStream is = Files.newInputStream(path);
            ZipInputStream zis = new ZipInputStream(is);) {
            ZipEntry zipEntry = zis.getNextEntry();
            while(!Objects.isNull(zipEntry)) {
                System.out.println(zipEntry.getName());
                zipEntry = zis.getNextEntry();
            }
        }
    }

    @Test
    public void decompressZipWithSingleFile() throws IOException {
        Path path = zipSingleFile();
        Path decompress = ZipUtils.decompress(path);
        System.out.println(decompress);
    }

    @Test
    public void decompressZipFromDirectory() throws IOException {
        Path path = zipDirectory();
        Path decompress = ZipUtils.decompress(path);
        System.out.println(decompress);
    }

    @Test
    public void decompressZipFromDeepDirectory() throws IOException {
        Path path = zipDeepDirectory();
        Path decompress = ZipUtils.decompress(path);
        System.out.println(decompress);
    }

    @Test
    public void deleteDirectory() throws IOException {
        Path path = zipDeepDirectory();
        System.out.println(path);
    }

    @Test
    public void decompressZipFromEmptyDirectory() throws IOException {
        Path path = zipEmptyDirectory();
        Path decompress = ZipUtils.decompress(path);
        System.out.println(decompress);
    }
}
