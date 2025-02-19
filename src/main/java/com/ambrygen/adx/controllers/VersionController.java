package com.ambrygen.adx.controllers;

import com.ambrygen.adx.dto.VersionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {
    @GetMapping(value = "/version")
    public ResponseEntity<VersionInfo> getVersion() {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("1.0.0");
        versionInfo.setDescription("ADX REST APIs");
        versionInfo.setName("ADX REST APIs");
        return new ResponseEntity(versionInfo, HttpStatus.OK);
    }
}
