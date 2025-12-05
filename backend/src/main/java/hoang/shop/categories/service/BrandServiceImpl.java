package hoang.shop.categories.service;


import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.mapper.BrandMapper;
import hoang.shop.categories.model.Brand;
import hoang.shop.categories.repository.BrandRepository;
import hoang.shop.common.enums.status.BrandStatus;
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
    public BrandResponse create(BrandCreateRequest createRequest) {
        if (brandRepository.existsByName(createRequest.name()))
            throw new DuplicateResourceException("{error.brand.name.exists}");
        if (brandRepository.existsBySlug(createRequest.slug()))
            throw new DuplicateResourceException("{error.brand.slug.exists}");
        Brand brand = brandMapper.toEntity(createRequest);
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse update(Long id, BrandUpdateRequest updateRequest) {
        if (brandRepository.existsByNameAndIdNot(updateRequest.name(),id))
            throw new DuplicateResourceException("{error.brand.name.exists}");
        if (brandRepository.existsBySlugAndIdNot(updateRequest.slug(),id))
            throw new DuplicateResourceException("{error.brand.slug.exists}");
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()->new NotFoundException("{error.brand.id.notFound}"));
        brandMapper.merge(updateRequest,brand);
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    public void updateStatusById(Long id, BrandStatus status) {
        int updatedRow = brandRepository.updateStatusById(id,status);
        if (updatedRow == 0)
            throw new NotFoundException("{error.brand.id.notFound}");
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.brand.id.notFound}"));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)

    public BrandResponse findByName(String name) {
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(()-> new NotFoundException("{error.brand.name.notFound}"));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse findBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(()-> new NotFoundException("{error.brand.slug.notFound}"));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<BrandResponse> findByStatus(BrandStatus status, Pageable pageable) {
        Slice<Brand> brands = brandRepository.findByStatus(status,pageable);
        return brands.map(brandMapper::toResponse);

    }
}
