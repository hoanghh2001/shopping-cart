package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductColorCreateRequest;
import hoang.shop.categories.dto.request.ProductColorUpdateRequest;
import hoang.shop.categories.dto.response.AdminColorResponse;
import hoang.shop.categories.dto.response.ColorResponse;
import hoang.shop.categories.mapper.ColorMapper;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.categories.repository.ProductColorRepository;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.common.enums.ProductColorStatus;
import hoang.shop.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductColorServiceImpl implements ProductColorService{

    private final ProductColorRepository colorRepository;
    private final ColorMapper colorMapper;
    private final ProductRepository productRepository;

    @Override
    public List<AdminColorResponse> getColorByProductId(Long productId) {
        List<ProductColor> colors = colorRepository.findByProduct_Id(productId);
        return colors.stream().map(colorMapper::toAdminResponse).toList();
    }

    @Override
    public AdminColorResponse getColorById(Long colorId) {
        return null;
    }

    @Override
    public AdminColorResponse addColor(Long productId, ProductColorCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("error.product.id.not-found"));
        ProductColor color =  colorMapper.toEntity(request);
        product.getColors().add(color);
        color.setProduct(product);
        return colorMapper.toAdminResponse(color);
    }

    @Override
    public AdminColorResponse updateColor(Long colorId, ProductColorUpdateRequest request) {
        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(()-> new NotFoundException("error.product-color.id.not-found"));
        colorMapper.merge(request,color);
        return colorMapper.toAdminResponse(color);
    }

    @Override
    public AdminColorResponse deleteColor(Long colorId) {
        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(()-> new NotFoundException("error.product-color.id.not-found"));
        color.setStatus(ProductColorStatus.DELETED);
        return colorMapper.toAdminResponse(color);
    }

    @Override
    public AdminColorResponse restoreColor(Long colorId) {
        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(()-> new NotFoundException("error.product-color.id.not-found"));
        color.setStatus(ProductColorStatus.ACTIVE);
        return colorMapper.toAdminResponse(color);
    }

    @Override
    public List<ColorResponse> findActiveColorsByProductSlug(String productSlug) {
        if(!productRepository.existsBySlug(productSlug))
                throw new NotFoundException("error.product.slug.not-found");
        List<ProductColor> colors = colorRepository.findByProduct_SlugAndStatus(productSlug,ProductColorStatus.ACTIVE);
        return colors.stream().map(colorMapper::toResponse).toList();
    }
}
