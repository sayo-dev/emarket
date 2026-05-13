package org.example.e_market.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map upload(MultipartFile file) throws IOException;
    void delete(String publicId) throws IOException;
}
