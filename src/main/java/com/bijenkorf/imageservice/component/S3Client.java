package com.bijenkorf.imageservice.component;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bijenkorf.imageservice.util.CommonUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Client {

    private AmazonS3 amazonS3;
    private static final Logger logger = LoggerFactory.getLogger(S3Client.class);

    @Value("${aws.endpointUrl}")
    private String endpointUrl;
    @Value("${aws.accessKey}")
    private String accessKey;
    @Value("${aws.secretKey}")
    private String secretKey;



    private String bucketName;
    @PostConstruct
    private void initialize() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.amazonS3 = new AmazonS3Client(credentials);
    }

    public void uploadFileTos3bucket(String predefinedFileType, String fileName, File file) {
        bucketName = CommonUtils.getBucketName(predefinedFileType, fileName);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
            .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        //construct bucket first
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName + "/", fileName));
        return "Successfully deleted";
    }

    public InputStream getFileAsInputStream(String predefinedFiletype, String fileName) {

        bucketName = CommonUtils.getBucketName(predefinedFiletype, fileName);

        try {

            S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));
            System.out.println("Content-Type: "  + s3object.getObjectMetadata().getContentType());

            InputStream fileInputStream = new BufferedInputStream( s3object.getObjectContent());
            return fileInputStream;
        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            logger.info("IOE Error Message: " + e.getMessage());
        }
        return null;
    }
}
