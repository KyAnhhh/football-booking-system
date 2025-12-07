package vn.phankyanh.shopbongda.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final Cloudinary cloudinary;
    public String uploadImage(MultipartFile file){
        try {
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                 ObjectUtils.asMap("resource_type", "image"));

            return uploadResult.get("secure_url").toString();  // trả về link ảnh
        } catch (Exception e) {
            throw new RuntimeException("Upload image failed: " + e.getMessage());
        }
    }
}
