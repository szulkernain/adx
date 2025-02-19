package com.ambrygen.adx.controllers;

import com.ambrygen.adx.errors.InvalidFileTypeException;
import com.ambrygen.adx.services.FileService;
import com.ambrygen.adx.services.S3BucketStorageService;
import com.ambrygen.adx.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3FileController {
    private static final Logger logger = LoggerFactory.getLogger(S3FileController.class);
    List<String> ALLOWED_FILE_TYPES = Stream.of("audio", "video", "image")
            .collect(Collectors.toList());
    private final S3BucketStorageService s3BucketStorageService;
    private final FileService fileService;
    private final VehicleService vehicleService;

    @PostMapping("/uservehicles/{userVehicleId}/upload")
    //@PreAuthorize("hasRole('ADMIN')")
    public String uploadFile(@RequestParam(value = "imageFile") MultipartFile file,
                             @PathVariable String userVehicleId) {
        File fileObj = fileService.getFileFromMultiPartFile(file);
        String mediaType = fileService.getMediaType(fileObj);
        String fileType = getFileTypePrefix(mediaType);
        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new InvalidFileTypeException("Invalid media type: " + mediaType);
        }
        String fileNameWithoutFolderName = fileService.getFileNameWithoutFolderName(file);
        String fileName = fileService.getFileName(fileNameWithoutFolderName);
        logger.info("S3 File Name: " + fileName);
        //Upload the file to AWS S3
        String s3FileUrl = s3BucketStorageService.uploadFile(fileObj, fileType, fileName);
        logger.info("S3 File URL: " + s3FileUrl);
        // Once the file has been uploaded from amazon s3 we need to delete it from our computer
        fileObj.delete();
        //Add image file name information to the user_vehicles table.
        vehicleService.updateImageFileName(userVehicleId, fileNameWithoutFolderName);
        return "File upload successful";
    }

    private String getFileTypePrefix(String mediaType) {
        String[] fileType = mediaType.split("/");
        return fileType[0];
    }

    @GetMapping("/download/{fileName}")
    public @ResponseBody byte[] downloadImage(@PathVariable String fileName) {
        logger.debug("File to download...............: " + fileName );
        byte[] data = s3BucketStorageService.downloadFile(fileName);
        return data;
    }

    @GetMapping("/download/documents/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadDocuments(@PathVariable String fileName) {
        byte[] data = s3BucketStorageService.downloadFile(fileName);
        byte[] encoded = Base64.getEncoder().encode(data);
        ByteArrayResource resource = new ByteArrayResource(encoded);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "image/jpeg")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(s3BucketStorageService.deleteFile(fileName), HttpStatus.OK);
    }
}

