package fun.jhs.zip;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public interface ZipUtils {

    /* * * * * * * *     Compression     * * * * * * * */

    static Path compress(Path src) throws IOException {
        Path target = targetZip(src);
        try (OutputStream os = Files.newOutputStream(target);
             ZipOutputStream zos = new ZipOutputStream(os)) {
            if (src.toFile().isFile()) {
                compressSingleFile(zos, src, "");
            } else {
                compressDirectory(zos, src, "");
            }
        }
        return target;
    }

    static Path targetZip(Path src) {
        String srcName = src.toFile().getName();
        if (srcName.lastIndexOf('.') != -1) {
            srcName = srcName.substring(0, srcName.lastIndexOf('.'));
        }
        return src.getParent().resolve(srcName + ".zip");
    }

    static void compressSingleFile(ZipOutputStream zos, Path path, String prefix) throws IOException {
        zos.putNextEntry(new ZipEntry(prefix + path.toFile().getName()));
        IOUtils.copy(Files.newInputStream(path), zos);
        zos.closeEntry();
    }

    static void compressDirectory(ZipOutputStream zos, Path dirPath, String prefix) throws IOException {
        File[] files = dirPath.toFile().listFiles();
        if (0 == files.length) {
            zos.putNextEntry(new ZipEntry(prefix + dirPath.toFile().getName() + "/"));
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                compressSingleFile(zos, file.toPath(), prefix + dirPath.toFile().getName() + "/");
            } else {
                compressDirectory(zos, file.toPath(), prefix + dirPath.toFile().getName() + "/");
            }
        }
    }

    /* * * * * * * *     Decompression     * * * * * * * */

    static Path decompress(Path zipPath) throws IOException {
        Path dir = targetDir(zipPath);
        try(InputStream is = Files.newInputStream(zipPath);
            ZipInputStream zis = new ZipInputStream(is);) {
            Files.createDirectories(dir);
            ZipEntry entry = zis.getNextEntry();
            while(!Objects.isNull(entry)) {
                if(entry.getName().endsWith("/")) {
                    decompressEmptyDir(dir, entry.getName());
                }
                else {
                    decompressFile(dir, entry.getName(), zis);
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }
        return dir;
    }

    static Path targetDir(Path zip) {
        String zipName = zip.toFile().getName();
        if(zipName.endsWith(".zip")) {
            zipName = zipName.substring(0, zipName.length()-4);
        }
        return zip.getParent().resolve(zipName);
    }

    static void decompressEmptyDir(Path dir, String name) throws IOException {
        Path emptyDir = dir.resolve(name.substring(0, name.length() - 1));
        Files.createDirectories(emptyDir);
    }

    static void decompressFile(Path dir, String name, ZipInputStream zis) throws IOException {
        Path file = dir.resolve(name);
        if(!file.getParent().toFile().exists()) {
            Files.createDirectories(file.getParent());
        }
        Files.createFile(file);
        try(OutputStream os = Files.newOutputStream(file);) {
            IOUtils.copy(zis, os);
        }
    }

    /* * * * * * * * * * * Utils * * * * * * * * * * * */

    static void deleteDirectory(Path path) {
        File[] files = path.toFile().listFiles();
        for(File file: files) {
            if(file.isFile()) {
                file.delete();
            }
            else {
                deleteDirectory(file.toPath());
            }
        }
        path.toFile().delete();
    }
}
