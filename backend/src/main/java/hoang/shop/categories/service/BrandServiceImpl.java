package hoang.shop.categories.service;


import hoang.shop.categories.dto.response.AdminBrandResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.mapper.BrandMapper;
import hoang.shop.categories.model.Brand;
import hoang.shop.categories.repository.BrandRepository;
import hoang.shop.common.enums.BrandStatus;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public AdminBrandResponse create(BrandCreateRequest createRequest) {
        if (brandRepository.existsByName(createRequest.name()))
            throw new DuplicateResourceException("{error.brand.name.exists}");
        if (brandRepository.existsBySlug(createRequest.slug()))
            throw new DuplicateResourceException("{error.brand.slug.exists}");
        Brand brand = brandMapper.toEntity(createRequest);
        brand = brandRepository.save(brand);
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    public AdminBrandResponse update(Long id, BrandUpdateRequest updateRequest) {
        if (brandRepository.existsByNameAndIdNot(updateRequest.name(),id))
            throw new DuplicateResourceException("{error.brand.name.exists}");
        if (brandRepository.existsBySlugAndIdNot(updateRequest.slug(),id))
            throw new DuplicateResourceException("{error.brand.slug.exists}");
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()->new NotFoundException("{error.brand.id.notFound}"));
        brandMapper.merge(updateRequest,brand);
        brand = brandRepository.save(brand);
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminBrandResponse findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.brand.id.notFound}"));
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)

    public AdminBrandResponse findByName(String name) {
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(()-> new NotFoundException("{error.brand.name.notFound}"));
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminBrandResponse findBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(()-> new NotFoundException("{error.brand.slug.notFound}"));
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<AdminBrandResponse> findByStatus(BrandStatus status, Pageable pageable) {
        Slice<Brand> brands = brandRepository.findByStatus(status,pageable);
        return brands.map(brandMapper::toAdminResponse);

    }

    @Override
    public AdminBrandResponse softDelete(Long brandId) {
        Brand brand = brandRepository.findByIdAndStatus(brandId,BrandStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.brand.id.notFound}"));
        brand.setStatus(BrandStatus.DELETED);
        return brandMapper.toAdminResponse(brand);
    }

    @Override
    public AdminBrandResponse restore(Long brandId) {
        Brand brand = brandRepository.findByIdAndStatus(brandId,BrandStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.brand.id.notFound}"));
        brand.setStatus(BrandStatus.ACTIVE);
        return brandMapper.toAdminResponse(brand);

    }

    @Override
    public Slice<BrandResponse> findActiveBrands(Pageable pageable) {
        Slice<Brand> brands = brandRepository.findAllByStatus(BrandStatus.ACTIVE,pageable);
        return brands.map(brandMapper::toResponse);
    }

    @Override
    public BrandResponse getActiveBrandById(Long brandId) {
        Brand brand = brandRepository.findByIdAndStatus(brandId,BrandStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.brand.id.notFound}"));
        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse getActiveBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlugAndStatus(slug,BrandStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.brand.slug.notFound}"));
        return brandMapper.toResponse(brand);
    }
}
