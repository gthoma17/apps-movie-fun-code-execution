package org.superbiz.moviefun;

import java.io.*;
import java.util.Optional;

public class FileStore implements BlobStore{

    private final File BLOBSTORE_DIR = new File("blobs");


    @Override
    public void put(Blob blob) throws IOException {
        String blobPath = BLOBSTORE_DIR.getCanonicalPath() +"/"+blob.name;
        File targetFile = new File(blobPath);

        byte[] buffer = new byte[blob.inputStream.available()];
        blob.inputStream.read(buffer);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(targetFile);
        outputStream.write(buffer);
    }

    @Override
    public Optional<Blob> get(String name) {
        try {
            String blobPath = BLOBSTORE_DIR.getCanonicalPath()  +"/"+name;
            return (Optional<Blob>) new ObjectInputStream(new FileInputStream(blobPath)).readObject();
        } catch (IOException  e) {
            ; // NOOP
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteAll() {
        for(File file: BLOBSTORE_DIR.listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
