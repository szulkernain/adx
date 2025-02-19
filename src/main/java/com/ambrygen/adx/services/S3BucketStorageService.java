package com.ambrygen.adx.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This service is responsible for the basic operations upload, download and delete
 * from your amazon s3 bucket.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class S3BucketStorageService {
    private static final Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);
    @Value("${amazon.s3.adx.folder-name}")
    private String folderName;

    // == fields ==
    private final S3Client s3Client;
    private final String bucketName;
    private final FileService fileService;


    @Value("${amazon.s3.region}")
    private String region;

    // == methods ==

    /**
     * This method upload a file to your amazon s3 bucket.
     *
     * @param file the file
     * @return A {@code String} message
     */
    public String uploadFile(File fileObj, String fileType, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(fileName) // uniquely identifies the object in an Amazon S3 bucket.
                .build();
        try {
            PutObjectResponse response =
                    s3Client.putObject(putObjectRequest, RequestBody.fromFile(fileObj));
            return getS3FileUrl(bucketName, fileName);
        } catch (Exception e) {
            logger.error("Could not upload to bucket: " + this.bucketName + " :");
            logger.error("Could not upload filet: " + fileName + " :");
            logger.error("Could not upload file",e);
        }
        return "";
    }


    /**
     * The file URL for S3 is in this format -
     * https://bhashamitra.s3.us-west-1.amazonaws.com/marathimitra/1663523308541_Screen+Shot+2022-07-23+at+6.14.00+PM.png
     *
     * @param bucketName
     * @param fileName
     * @return S3 object URL
     */
    private String getS3FileUrl(String bucketName, String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    /**
     * This method download a file from your amazon s3 bucket.
     *
     * @param fileName the filename
     * @return A {@code byte[]} file
     */
    public byte[] downloadFile(String fileName) {
        String key = folderName + "/" + fileName;
        logger.debug("Bucket name..........:" + this.bucketName);
        logger.debug("Key..........:" + key);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.bucketName)
                .key(key) // uniquely identifies the object in an Amazon S3 bucket.
                .build();
        try {
        ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);


            byte[] content = IoUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            logger.error("Could not download  from bucket: " + this.bucketName + " :");
            logger.error("Could not download filw with key: " + key + " :");
            logger.error("Could not download file",e);
        }

        return null;
    }

    /**
     * This method delete a file from your amazon s3 bucket.
     *
     * @param fileName the filename
     * @return A {@code String} message
     */
    public String deleteFile(String fileName) {
        log.info("s3 delete object: " + fileName);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(this.bucketName)
                .key(fileName) // uniquely identifies the object in an Amazon S3 bucket.
                .build();

        try {
            DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);
            log.info("s3 delete object complete!");
        } catch (AwsServiceException awse) {
            log.error("Problem deleting file", awse);
        }

        return "Your file " + fileName + " was successfully deleted.";
    }

    /**
     * A utility for converting multipart files into files.
     *
     * @param file the multipart file to be converted
     * @return A {@code File}
     */
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
