package hoang.shop.categories.service;

import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.dto.response.TagResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
public interface ProductTagService {

    ProductResponse attachTagsToProduct(Long productId, List<Long> tagIds);

    ProductResponse detachTagsFromProduct(Long productId, List<Long> tagIds);

    Slice<TagResponse> findTagsByProductId(Long productId, Pageable pageable);

    Slice<ProductResponse> findProductsByTagId(Long tagId,Pageable pageable);
}
