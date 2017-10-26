package org.superbiz.moviefun;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.*;
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
        if(s3.doesObjectExist(bucketName, name)){
            S3Object s3Blob = s3.getObject(bucketName, name);
            s3Blob.getObjectMetadata().getContentType();
            return Optional.of(
                    new Blob(name, s3Blob.getObjectContent(), s3Blob.getObjectMetadata().getContentType())
            );
        }
        else{
            return null;
        }
    }

    @Override
    public void deleteAll() {
        s3.deleteBucket(bucketName);
        s3.createBucket(bucketName);
    }
}