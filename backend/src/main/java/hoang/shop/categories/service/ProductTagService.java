package hoang.shop.categories.service;

import hoang.shop.categories.dto.response.AdminListItemProductResponse;

import java.util.List;

public interface ProductTagService {

    AdminListItemProductResponse attachTagsToProduct(Long productId, List<Long> tagIds);

    AdminListItemProductResponse detachTagsFromProduct(Long productId, List<Long> tagIds);






}
