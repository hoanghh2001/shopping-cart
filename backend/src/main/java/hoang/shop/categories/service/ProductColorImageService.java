package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductColorImageCreateRequest;
import hoang.shop.categories.dto.request.ProductColorImageUpdateRequest;
import hoang.shop.categories.dto.response.AdminImageResponse;
import hoang.shop.common.enums.ImageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductColorImageService {

    //Admin
    List<AdminImageResponse> search(Long colorId, ImageStatus status, Boolean isMain, Pageable pageable);
    AdminImageResponse getImageById(Long imageId);
    List<AdminImageResponse> uploadColorImage(Long colorId, List<MultipartFile> file, List<ProductColorImageCreateRequest> requestList);
    AdminImageResponse updateImage(Long imageId, MultipartFile file, ProductColorImageUpdateRequest updateRequest);
    void softDeleteImage(Long imageId);
    void restoreImage(Long imageId);
    void deleteImages(Long colorId ,List<Long> ids);

}
