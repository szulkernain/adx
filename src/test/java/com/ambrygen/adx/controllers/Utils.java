package com.ambrygen.adx.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String replaceTokensWithValues(String text, HashMap<String, String> replacements) {
        Pattern pattern = Pattern.compile("\\[(.+?)\\]");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null)
                builder.append(matcher.group(0));
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        return builder.toString();
    }

    public static String readFileContents(String fileName) {
        String data = null;
        try {
            // read a file
//            Resource resource = new ClassPathResource("classpath:" + fileName);
            String path = "src/test/resources" + fileName;

            File file = new File(path);
            // get inputStream object
//            InputStream inputStream = resource.getInputStream();
            InputStream inputStream = new FileInputStream(file);

            // convert inputStream into a byte array
            byte[] dataAsBytes = FileCopyUtils.copyToByteArray(inputStream);

            // convert the byte array into a String
            data = new String(dataAsBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Could not read file", e);
        }
        return data;
    }
}
