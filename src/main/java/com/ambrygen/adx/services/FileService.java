package com.ambrygen.adx.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This service is responsible for the basic operations managing file metadata to database
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
//    private final MarathiPhraseFileRepository marathiPhraseFileRepository;
//    private final MarathiPhraseRepository marathiPhraseRepository;
//    private final TagService tagService;

    @Value("${amazon.s3.adx.folder-name}")
    private String folderName;

    public File getFileFromMultiPartFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        return fileObj;
    }

    public String getFileName(String fileNameWithoutFolderName) {
        return folderName + "/" + fileNameWithoutFolderName;
    }

//    public MarathiPhraseFileDTO addPhraseFile(String s3FileUrl, List<String> labels, String marathiPhraseId, String fileType, String fileName) {
//        MarathiPhraseFile marathiPhraseFile = new MarathiPhraseFile();
//        marathiPhraseFile.setFilePath(s3FileUrl);
//        marathiPhraseFile.setFileName(fileName);
//        marathiPhraseFile.setFileType(fileType);
//
//        Optional<MarathiPhrase> marathiPhraseOptional =
//                marathiPhraseRepository.findById(marathiPhraseId);
//        MarathiPhrase marathiPhrase = marathiPhraseOptional.get();
//        marathiPhraseFile.setMarathiPhrase(marathiPhrase);
//        MarathiPhraseFile newlyCreatedMPFile = marathiPhraseFileRepository.save(marathiPhraseFile);
//        List<Tag> tags = tagService.createTagForMarathiPhraseFile(newlyCreatedMPFile.getId(), labels);
//        MarathiPhraseFileDTO marathiPhraseFileDTO =
//                getMarathiPhraseFileDTO(newlyCreatedMPFile, tags);
//        return marathiPhraseFileDTO;
//    }

//    private MarathiPhraseFileDTO getMarathiPhraseFileDTO(MarathiPhraseFile newlyCreatedMPFile, List<Tag> tags) {
//        MarathiPhraseFileDTO marathiPhraseFileDTO = new MarathiPhraseFileDTO();
//        marathiPhraseFileDTO.setId(newlyCreatedMPFile.getId());
//        marathiPhraseFileDTO.setFilePath(newlyCreatedMPFile.getFilePath());
//        marathiPhraseFileDTO.setFileName(folderName,newlyCreatedMPFile.getFileName());
//        marathiPhraseFileDTO.setFileType(newlyCreatedMPFile.getFileType());
//        marathiPhraseFileDTO.setCreatedDate(newlyCreatedMPFile.getCreatedDate());
//        List<TagDTO> tagDTOs = tagService.getTagDTOs(tags);
//        marathiPhraseFileDTO.setTags(tagDTOs);
//        List<String> labels = new ArrayList<>();
//        tagDTOs.forEach(tagDTO -> {
//            labels.add(tagDTO.getTitle());
//        });
//        marathiPhraseFileDTO.setLabels(labels);
//        return marathiPhraseFileDTO;
//    }

    public String getMediaType(File file) {
        final Tika tika = new Tika();
        String fileTypeDefault = "";

        try {
            fileTypeDefault = tika.detect(file);
        } catch (IOException e) {
            System.out.println("*** getFleeTypeByTika2 - Error while detecting file type from File ***");
            System.out.println("*** getFileTypeViaTika2 - Error message: " + e.getMessage());
            e.printStackTrace();
        }


        return fileTypeDefault;
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

    public String getFileNameWithoutFolderName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
}
