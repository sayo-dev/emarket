package org.example.e_market.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private static final Map FOLDER = ObjectUtils.asMap("folder", "emarket");

    @Override
    public Map upload(MultipartFile file) throws IOException {
        var uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), FOLDER);
        log.debug("Cloudinary upload result type, {}", uploadResult.getClass());
        log.debug("Cloudinary upload result, {}", uploadResult);
        return uploadResult;
    }

    @Override
    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, FOLDER);
    }
}
