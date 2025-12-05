package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductColorCreateRequest;
import hoang.shop.categories.dto.request.ProductColorUpdateRequest;
import hoang.shop.categories.dto.response.AdminColorResponse;
import hoang.shop.categories.dto.response.ColorResponse;

import java.util.List;

public interface ProductColorService {
    List<AdminColorResponse> getColorByProductId(Long productId);

    AdminColorResponse getColorById(Long colorId);

    AdminColorResponse addColor(Long productId, ProductColorCreateRequest request);

    AdminColorResponse updateColor(Long colorId, ProductColorUpdateRequest request);

    AdminColorResponse deleteColor (Long colorId);

    AdminColorResponse restoreColor (Long colorId);


    List<ColorResponse> findActiveColorsByProductSlug(String productSlug);


}


