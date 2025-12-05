package hoang.shop.categories.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.mapper.TagMapper;
import hoang.shop.categories.model.Tag;
import hoang.shop.categories.repository.TagRepository;
import hoang.shop.common.enums.status.TagStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.ConflictException;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{
    private final TagRepository tagRepository;
    private final TagMapper mapper;
    @Override
    public TagResponse create(TagCreateRequest createRequest) {
        if (tagRepository.existsBySlug(createRequest.slug()))
            throw new DuplicateResourceException("{error.tag.slug.exists}");
        if (tagRepository.existsByName(createRequest.name()))
            throw new DuplicateResourceException("{error.tag.name.exists}");
        Tag tag = mapper.toEntity(createRequest);
        tag = tagRepository.saveAndFlush(tag);
        return mapper.toResponse(tag);
    }

    @Override
    public TagResponse update(Long id,TagUpdateRequest updateRequest) {
        if (tagRepository.existsBySlugAndIdNot(updateRequest.slug(),id))
            throw new DuplicateResourceException("{error.tag.slug.exists}");
        if (tagRepository.existsByNameAndIdNot(updateRequest.name(),id))
            throw new DuplicateResourceException("{error.tag.name.exists}");
        Tag tag = tagRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.exists}"));
        mapper.merge(updateRequest,tag);
        tag = tagRepository.save(tag);
        return mapper.toResponse(tag);
    }

    @Override
    public TagResponse findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.exists}"));
        return mapper.toResponse(tag);
    }

    @Override
    public TagResponse findBySlug(String slug) {
        Tag tag = tagRepository.findBySlugAndStatus(slug, TagStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.exists}"));
        return mapper.toResponse(tag);
    }

    @Override
    public Slice<TagResponse> findByName(String name, Pageable pageable) {
        Slice<Tag> page = tagRepository.findByNameAndStatus(name,TagStatus.ACTIVE,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Slice<TagResponse> findByStatus(TagStatus status, Pageable pageable) {
        Slice<Tag> page = tagRepository.findByStatus(status,pageable);

        return page.map(mapper::toResponse);
    }

    @Override
    public Integer deleteById(IdListRequest request) {
        List<Long> ids = request.ids();
        if (ids == null || ids.isEmpty()) throw new BadRequestException("{error.ids.empty}");

        int ok = tagRepository.countByIdInAndStatus(ids, TagStatus.ACTIVE);
        if (ok != ids.size()) throw new BadRequestException("{error.tag.bulk-delete.invalid-ids}");

        int rows = tagRepository.updateStatusByIdIn(ids, TagStatus.DELETED);
            if (rows != ids.size()) throw new ConflictException("{error.tag.bulk-delete.race-condition}");
        return rows;
    }

    @Override
    public Integer restoreById(IdListRequest request) {
        List<Long> ids = request.ids();
        if (ids == null || ids.isEmpty()) throw new BadRequestException("{error.ids.empty}");

        int ok = tagRepository.countByIdInAndStatus(ids, TagStatus.DELETED);
        if (ok != ids.size()) throw new BadRequestException("{error.tag.bulk-delete.invalid-ids}");

        int rows = tagRepository.updateStatusByIdIn(ids, TagStatus.ACTIVE);
        if (rows != ids.size()) throw new ConflictException("{error.tag.bulk-delete.race-condition}");
        return rows;
    }
}
