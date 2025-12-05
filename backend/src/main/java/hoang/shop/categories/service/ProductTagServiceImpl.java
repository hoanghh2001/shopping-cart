package hoang.shop.categories.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.mapper.ProductMapper;
import hoang.shop.categories.mapper.TagMapper;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.Tag;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.categories.repository.ProductTagRepository;
import hoang.shop.categories.repository.TagRepository;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductTagServiceImpl implements ProductTagService{
    private final ProductTagRepository productTagRepository;
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ProductMapper productMapper;
    @Override
    public ProductResponse attachTagsToProduct(Long productId, List<Long> tagIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        if (tagIds == null || tagIds.isEmpty())
            throw new BadRequestException("error.tag.ids.required");
        List<Long> distinctIds = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctIds.isEmpty()) throw new BadRequestException("error.tag.ids.required");

        List<Tag> tagList = tagRepository.findAllById(distinctIds);
        if (tagIds.size() != distinctIds.size()){
            Set<Long> missing = new HashSet<>(distinctIds);
            for (Tag t : tagList) missing.remove(t.getId());
            throw new NotFoundException("{error.tag.id.not-found}"+missing);
        }
        for (Long id : distinctIds){
            productTagRepository.insertIfNotExists(product.getId(),id);
        }
        return productMapper.toResponse(product);

    }

    @Override
    public ProductResponse detachTagsFromProduct(Long productId, List<Long> tagIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        if (tagIds == null || tagIds.isEmpty())
            throw new BadRequestException("error.tag.ids.required");
        List<Long> distinctIds = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctIds.isEmpty()) throw new BadRequestException("error.tag.ids.required");
        List<Tag> tagList = tagRepository.findAllById(distinctIds);
        if (tagIds.size() != distinctIds.size()){
            Set<Long> missing = new HashSet<>(distinctIds);
            for (Tag t : tagList) missing.remove(t.getId());
            throw new NotFoundException("{error.tag.id.not-found}"+missing);
        }
        productTagRepository.deleteByProductAndTagIds(productId, distinctIds);
        return productMapper.toResponse(product);
    }

    @Override
    public Slice<TagResponse> findTagsByProductId(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId))
            throw new NotFoundException("{error.product.id.not-found}");
        Slice<Tag> tagList = productTagRepository.findTagsByProductId(productId, pageable);
        return tagList.map(tagMapper::toResponse);
    }

    @Override
    public Slice<ProductResponse> findProductsByTagId(Long tagId,Pageable pageable) {
        if (!productRepository.existsById(tagId))
            throw new NotFoundException("{error.product.id.not-found}");
        Slice<Product> productList = productTagRepository.findProductsByTagId(tagId,pageable);
        return productList.map(productMapper::toResponse);
    }
}

