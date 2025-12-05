package hoang.shop.categories.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.response.CategoryResponse;
import hoang.shop.categories.mapper.CategoryMapper;
import hoang.shop.categories.model.Category;
import hoang.shop.categories.repository.CategoryRepository;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.common.enums.status.CategoryStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    @PersistenceContext
    EntityManager em;


    @Override
    public CategoryResponse create(CategoryCreateRequest createRequest) {
        if (categoryRepository.existsByName(createRequest.name())){
            throw new BadRequestException("{error.category.name.exists}");
        }else if (categoryRepository.existsBySlug(createRequest.name())){
            throw new BadRequestException("{error.category.slug.exists}");
        }
        Category category = categoryMapper.toEntity(createRequest);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(Long categoryId, CategoryUpdateRequest updateRequest) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("{error.category.id.notFound}"));


        if (updateRequest.parentId() != null) {
            if (updateRequest.parentId().equals(categoryId)) {
                throw new BadRequestException("{error.category.parent.self}");
            }
            if (!categoryRepository.existsById(updateRequest.parentId())) {
                throw new NotFoundException("{error.category.parent-id.not-found}");
            }
            category.setParent(categoryRepository.getReferenceById(updateRequest.parentId()));
        }
        categoryMapper.merge(updateRequest,category);
        return categoryMapper.toResponse(categoryRepository.saveAndFlush(category));
    }
    @Override
    public CategoryResponse replaceParent(Long id,Long parentId ){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        if (Objects.equals(category,parent))
            throw new BadRequestException("{error.category.parent.self}");
        Category oldParent = category.getParent();
        if (oldParent != null) {
            oldParent.removeChildren(category);
        }
        parent.addChildren(category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse unsetParent(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        Category oldParent = category.getParent();
        if (oldParent != null){
            oldParent.removeChildren(category);
        }
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.category.id.notFound}"));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse findBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        return categoryMapper.toResponse(category);
    }

    @Override
    public Slice<CategoryResponse> findAllByStatus(CategoryStatus status, Pageable pageable) {
        Slice<Category> categoryPage = categoryRepository.findAllByStatus(status,pageable);
        return categoryPage.map(categoryMapper::toResponse);
    }

    @Override
    public boolean updateStatusById(Long id, CategoryStatus categoryStatus) {
        categoryRepository.findById(id).orElseThrow(()-> new NotFoundException("{error.category.id.notFound}"));
        int count = categoryRepository.updateStatusById(id,categoryStatus);
        if (count == 0){
            throw new BadRequestException("{error.category.status.badRequest}");
        }
        return true;
    }

    @Override
    public Slice<CategoryResponse> findByProductId(Long id, Pageable pageable) {
        Slice<Category> slice = categoryRepository.findByProductId(id,pageable);
        return slice.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse replaceProducts(Long categoryId, IdListRequest req) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));

        List<Long> ids = req == null || req.ids() == null ? List.of() : req.ids();
        if (ids.isEmpty()) return categoryMapper.toResponse(category);

        if (productRepository.countByIdIn(ids) != ids.size())
            throw new BadRequestException("{error.product.id.bad-request}");

        Category ref = em.getReference(Category.class, categoryId);
        // ref プロダクト追加したいカテゴリー
        // ids list product id
        productRepository.bulkAssignToCategory(ref, ids);

        Category fresh = categoryRepository.findWithProductsById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id-not-found}"));
        return categoryMapper.toResponse(fresh);
    }

    @Override
    public CategoryResponse unsetProducts(Long categoryId, IdListRequest req) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));

        List<Long> ids = req == null || req.ids() == null ? List.of() : req.ids();
        if (ids.isEmpty()) return categoryMapper.toResponse(category);

        if (productRepository.countByIdIn(ids) != ids.size())
            throw new BadRequestException("{error.product.id.bad-request}");

        productRepository.bulkRemoveFromCategory(categoryId, ids);
        Category fresh = categoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("{error.category.id.not-found}"));
        return categoryMapper.toResponse(fresh);
    }
}
