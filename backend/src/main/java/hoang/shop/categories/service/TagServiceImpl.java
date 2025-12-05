package hoang.shop.categories.service;

import com.github.slugify.Slugify;
import hoang.shop.categories.dto.response.AdminTagResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.mapper.TagMapper;
import hoang.shop.categories.model.Tag;
import hoang.shop.categories.repository.TagRepository;
import hoang.shop.common.enums.TagStatus;
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
    public AdminTagResponse create(TagCreateRequest createRequest) {
        if (tagRepository.existsBySlug(createRequest.slug()))
            throw new DuplicateResourceException("{error.tag.slug.exists}");
        if (tagRepository.existsByName(createRequest.name()))
            throw new DuplicateResourceException("{error.tag.name.exists}");
        String slug = Slugify.builder().build().slugify(createRequest.name());
        Tag tag = mapper.toEntity(createRequest);
        tag.setSlug(slug);
        Tag saved = tagRepository.save(tag);
        return mapper.toAdminResponse(saved);
    }

    @Override
    public AdminTagResponse update(Long id,TagUpdateRequest updateRequest) {
        if (tagRepository.existsBySlugAndIdNot(updateRequest.slug(),id))
            throw new DuplicateResourceException("{error.tag.slug.exists}");
        if (tagRepository.existsByNameAndIdNot(updateRequest.name(),id))
            throw new DuplicateResourceException("{error.tag.name.exists}");
        Tag tag = tagRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.exists}"));
        mapper.merge(updateRequest,tag);
        tag = tagRepository.save(tag);
        return mapper.toAdminResponse(tag);
    }

    @Override
    public AdminTagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.not-found}"));
        return mapper.toAdminResponse(tag);
    }

    @Override
    public AdminTagResponse getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(()-> new NotFoundException("{error.tag.slug.not-found}"));
        return mapper.toAdminResponse(tag);
    }

    @Override
    public Slice<AdminTagResponse> findTags(String keyword, TagStatus status, Pageable pageable) {
        Slice<Tag> tags = tagRepository.findByKeywordAndStatus(keyword,status,pageable);
        return tags.map(mapper::toAdminResponse);
    }

    @Override
    public void deleteById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) throw new BadRequestException("{error.ids.empty}");

        int ok = tagRepository.countByIdInAndStatus(ids, TagStatus.ACTIVE);
        if (ok != ids.size()) throw new BadRequestException("{error.tag.bulk-delete.invalid-ids}");

        int rows = tagRepository.updateStatusByIdIn(ids, TagStatus.DELETED);
            if (rows != ids.size()) throw new ConflictException("{error.tag.bulk-delete.race-condition}");
    }

    @Override
    public void restoreById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) throw new BadRequestException("{error.ids.empty}");

        int ok = tagRepository.countByIdInAndStatus(ids, TagStatus.DELETED);
        if (ok != ids.size()) throw new BadRequestException("{error.tag.bulk-delete.invalid-ids}");

        int rows = tagRepository.updateStatusByIdIn(ids, TagStatus.ACTIVE);
        if (rows != ids.size()) throw new ConflictException("{error.tag.bulk-delete.race-condition}");
    }
    @Override
    public List<TagResponse> findActiveTags() {
        List<Tag> tags = tagRepository.findAllByStatus(TagStatus.ACTIVE);
        return tags.stream().map(mapper::toResponse).toList();
    }
    @Override
    public TagResponse getActiveTagById(Long tagId) {
        Tag tag = tagRepository.findByIdAndStatus(tagId,TagStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.not-found}"));
        return mapper.toResponse(tag);
    }

    @Override
    public TagResponse getActiveTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlugAndStatus(slug,TagStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.tag.id.not-found}"));
        return mapper.toResponse(tag);
    }

}



