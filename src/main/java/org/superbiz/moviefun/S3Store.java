package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore {
    private final AmazonS3Client s3;
    private final String bucketName;

    public S3Store(AmazonS3Client s3, String bucketName){
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata blobMetadata = new ObjectMetadata();
        blobMetadata.setContentType(blob.contentType);
        s3.putObject(bucketName,blob.name,blob.inputStream, blobMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!s3.doesObjectExist(bucketName, name)) {
            return Optional.empty();
        }
        try(S3Object s3Blob = s3.getObject(bucketName, name)){
            byte[] bytes = IOUtils.toByteArray(s3Blob.getObjectContent());
            return Optional.of(
                    new Blob(name, s3Blob.getObjectContent(), s3Blob.getObjectMetadata().getContentType())
            );
        }
    }

    @Override
    public void deleteAll() {
        List<S3ObjectSummary> summaries = s3
                .listObjects(bucketName)
                .getObjectSummaries();

        for (S3ObjectSummary summary : summaries) {
            s3.deleteObject(bucketName, summary.getKey());
        }
    }
}