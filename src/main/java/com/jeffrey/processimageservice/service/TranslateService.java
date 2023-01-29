package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.translate.TranslationData;
import com.jeffrey.processimageservice.exception.exception.ProcessImageFailedException;
import java.io.InputStream;

public interface TranslateService {

    TranslationData getData(InputStream imageInputStream) throws ProcessImageFailedException;
}
