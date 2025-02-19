package com.ambrygen.adx.config;

import com.ambrygen.adx.annotation.S3BucketName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * This class handles the basic configurations needed to connect the app to amazon s3 service.
 */
@Configuration
public class S3Config {
    @Value("${amazon.s3.region}")
    private String region;
    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    // == beans methods ==
    @Bean
    public S3Client s3Client() {
        Region region = Region.of(this.region);
        return S3Client.builder().region(region).build();
    }

    @Bean
    @S3BucketName
    public String s3BucketName() {
        return this.bucketName;
    }
}
