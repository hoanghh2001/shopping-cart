package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductColorImageCreateRequest;
import hoang.shop.categories.dto.request.ProductColorImageUpdateRequest;
import hoang.shop.categories.dto.response.AdminImageResponse;
import hoang.shop.categories.mapper.ImageMapper;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.categories.repository.ProductColorImageRepository;
import hoang.shop.categories.repository.ProductColorRepository;
import hoang.shop.common.enums.ImageStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class ProductColorImageServiceImpl implements ProductColorImageService {
    private final ProductColorImageRepository imageRepository;
    private final ProductColorRepository colorRepository;
    private final ImageMapper imageMapper;
    private final FileStorageService fileStorageService;


    @Override
    public List<AdminImageResponse> search(Long colorId, ImageStatus status, Boolean isMain, Pageable pageable) {
        if (colorId !=null && !colorRepository.existsById(colorId))
            throw new NotFoundException("error.product-variant.color-id.not-found");
        List<ProductColorImage> images = imageRepository.search(colorId,status,isMain,pageable);
        return images.stream().map(imageMapper::toAdminResponse).toList();
    }

    @Override
    public AdminImageResponse getImageById(Long imageId) {
        ProductColorImage image = imageRepository.findById(imageId)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.not-found"));
        return imageMapper.toAdminResponse(image);
    }

    @Override
    public List<AdminImageResponse> uploadColorImage(
            Long colorId,
            List<MultipartFile> files,
            List<ProductColorImageCreateRequest> requestList
    ) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("{error.image.files.empty}");
        }

        if (requestList == null || requestList.size() != files.size()) {
            throw new BadRequestException("{error.image.metadata.size-mismatch}");
        }

        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(() -> new NotFoundException("{error.product-color.id.not-found}"));
        List<ProductColorImage> images = color.getImages();
        int currentIndex = color.getImages().size();
        for (int i = 0; i < files.size(); i++) {
            currentIndex ++;
            MultipartFile file = files.get(i);
            ProductColorImageCreateRequest req = requestList.get(i);
            String imageUrl = fileStorageService.saveColorImage(
                    color.getProduct().getId(),
                    color.getId(),
                    file
            );
            ProductColorImage image = imageMapper.toEntity(req);
            image.setSortOrder(currentIndex);
            image.setColor(color);
            image.setImageUrl(imageUrl);

            images.add(image);
        }
        imageRepository.saveAll(images);
        return images.stream()
                .map(imageMapper::toAdminResponse)
                .toList();
    }

    @Override
    public AdminImageResponse updateImage(Long imageId, MultipartFile file, ProductColorImageUpdateRequest updateRequest) {
        ProductColorImage image = imageRepository.findById(imageId)
                .orElseThrow(()->new NotFoundException("error.product-color-image.id.not-found"));
        imageMapper.merge(updateRequest,image);
        String imageUrl = fileStorageService.saveColorImage(
                image.getColor().getProduct().getId(),
                image.getColor().getId(),
                file
        );

        return imageMapper.toAdminResponse(image);
    }

    @Override
    public void softDeleteImage(Long imageId) {
        ProductColorImage image = imageRepository.findById(imageId)
                .orElseThrow(()->new NotFoundException("error.product-color-image.id.not-found"));
        image.setStatus(ImageStatus.DELETED);

    }

    @Override
    public void restoreImage(Long imageId) {
        ProductColorImage image = imageRepository.findById(imageId)
                .orElseThrow(()->new NotFoundException("error.product-color-image.id.not-found"));
        image.setStatus(ImageStatus.ACTIVE);
    }

    @Override
    public void deleteImages(Long colorId,List<Long> ids) {
        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(()->new NotFoundException("error.product-color.id.not-found"));
        List<ProductColorImage> deleteImages = imageRepository.findByIdInAndColor_IdAndStatus(ids,colorId,ImageStatus.ACTIVE);
        List<ProductColorImage> images = color.getImages();
        if (deleteImages.size() != ids.size()) {
            throw new BadRequestException("error.product-color-image.ids.invalid");
        }
        deleteImages.forEach(color::removeImage);
    }
}
