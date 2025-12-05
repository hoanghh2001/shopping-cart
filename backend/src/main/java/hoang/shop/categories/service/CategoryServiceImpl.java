package hoang.shop.categories.service;

import com.github.slugify.Slugify;
import hoang.shop.categories.dto.response.AdminCategoryResponse;
import hoang.shop.categories.dto.response.CategoryDetailResponse;
import hoang.shop.identity.service.CurrentUserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.common.IdListRequest;
import hoang.shop.categories.mapper.CategoryMapper;
import hoang.shop.categories.model.Category;
import hoang.shop.categories.repository.CategoryRepository;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.common.enums.CategoryStatus;
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
    public AdminCategoryResponse create(CategoryCreateRequest createRequest) {
        if (categoryRepository.existsByName(createRequest.name())){
            throw new BadRequestException("{error.category.name.exists}");
        }else if (categoryRepository.existsBySlug(createRequest.name())){
            throw new BadRequestException("{error.category.slug.exists}");
        }
        String slug = Slugify.builder().build().slugify(createRequest.name());
        Category category = categoryMapper.toEntity(createRequest);
        category.setSlug(slug);
        category = categoryRepository.save(category);
        return categoryMapper.toAdminResponse(category);
    }

    @Override
    public AdminCategoryResponse update(Long categoryId, CategoryUpdateRequest updateRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("{error.category.id.not-found}"));


        if (updateRequest.parentId() != null) {
            if (updateRequest.parentId().equals(categoryId)) {
                throw new BadRequestException("{error.category.parent.bad-request}");
            }
            if (!categoryRepository.existsById(updateRequest.parentId())) {
                throw new NotFoundException("{error.category.parent-id.not-found}");
            }
            category.setParent(categoryRepository.getReferenceById(updateRequest.parentId()));
        }
        categoryMapper.merge(updateRequest,category);
        return categoryMapper.toAdminResponse(categoryRepository.saveAndFlush(category));
    }
    @Override
    public AdminCategoryResponse replaceParent(Long id, Long parentId ){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        if (Objects.equals(category,parent))
            throw new BadRequestException("{error.category.parent.bad-request}");
        Category oldParent = category.getParent();
        if (oldParent != null) {
            oldParent.removeChildren(category);
        }
        parent.addChildren(category);
        return categoryMapper.toAdminResponse(categoryRepository.save(category));
    }

    @Override
    public AdminCategoryResponse unsetParent(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
        Category oldParent = category.getParent();
        if (oldParent != null){
            oldParent.removeChildren(category);
        }
        return categoryMapper.toAdminResponse(categoryRepository.save(category));
    }
    @Override
    @Transactional(readOnly = true)
    public AdminCategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.category.id.not-found}"));
        return categoryMapper.toAdminResponse(category);
    }

    @Override
    public AdminCategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name);
        return categoryMapper.toAdminResponse(category);
    }

    @Override
    public AdminCategoryResponse findBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        return categoryMapper.toAdminResponse(category);
    }

    @Override
    public Slice<AdminCategoryResponse> findAllByStatus(CategoryStatus status, Pageable pageable) {
        Slice<Category> categoryPage = categoryRepository.findAllByStatus(status,pageable);
        return categoryPage.map(categoryMapper::toAdminResponse);
    }

    @Override
    public boolean updateStatusById(Long id, CategoryStatus categoryStatus) {
        categoryRepository.findById(id).orElseThrow(()-> new NotFoundException("{error.category.id.not-found}"));
        int count = categoryRepository.updateStatusById(id,categoryStatus);
        if (count == 0){
            throw new BadRequestException("{error.category.status.bad-request}");
        }
        return true;
    }

    @Override
    public Slice<AdminCategoryResponse> findByProductId(Long id, Pageable pageable) {
        Slice<Category> slice = categoryRepository.findByProductId(id,pageable);
        return slice.map(categoryMapper::toAdminResponse);
    }

    @Override
    @Transactional
    public AdminCategoryResponse assignProducts(Long categoryId, IdListRequest req) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));

        List<Long> ids = req == null || req.ids() == null ? List.of() : req.ids();
        if (ids.isEmpty()) return categoryMapper.toAdminResponse(category);

        if (productRepository.countByIdIn(ids) != ids.size())
            throw new BadRequestException("{error.product.id.bad-request}");

        Category categoryReference = em.getReference(Category.class, categoryId);
        productRepository.bulkAssignToCategory(categoryReference, ids);

        Category fresh = categoryRepository.findWithProductsById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id-not-found}"));
        return categoryMapper.toAdminResponse(fresh);
    }

    @Override
    public AdminCategoryResponse unsetProducts(Long categoryId, IdListRequest req) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));

        List<Long> ids = req == null || req.ids() == null ? List.of() : req.ids();
        if (ids.isEmpty()) return categoryMapper.toAdminResponse(category);

        if (productRepository.countByIdIn(ids) != ids.size())
            throw new BadRequestException("{error.product.id.bad-request}");

        productRepository.bulkRemoveFromCategory(categoryId, ids);
        Category fresh = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new NotFoundException("{error.category.id.not-found}"));
        return categoryMapper.toAdminResponse(fresh);
    }

    @Override
    public CategoryDetailResponse getActiveBrandBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndStatus(slug,CategoryStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.category.slug.not-found}"));
        return categoryMapper.toDetailResponse(category);
    }

    @Override
    public CategoryDetailResponse getActiveBrandById(Long brandId) {

        Category category = categoryRepository.findByIdAndStatus(brandId,CategoryStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.category.id.not-found}"));
        return categoryMapper.toDetailResponse(category);
    }

    @Override
    public Slice<CategoryDetailResponse> findActiveBrands(Pageable pageable) {
        Slice<Category> categories = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE,pageable);
        return categories.map(categoryMapper::toDetailResponse);
    }
}
