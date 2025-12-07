package vn.phankyanh.shopbongda.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        String url = imageService.uploadImage(file);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(url);
        apiResponse.setMessage("Upload thành công");
        return apiResponse;
    }
}
