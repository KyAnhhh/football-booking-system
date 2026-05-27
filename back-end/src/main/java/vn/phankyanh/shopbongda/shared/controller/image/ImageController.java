package vn.phankyanh.shopbongda.shared.controller.image;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.shared.service.image.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
        throw new RuntimeException("File không được để trống");
            }

            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Chỉ được upload file ảnh");
            }

        String url = imageService.uploadImage(file);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(url);
        apiResponse.setMessage("Upload thành công");
        return apiResponse;
    }
}
