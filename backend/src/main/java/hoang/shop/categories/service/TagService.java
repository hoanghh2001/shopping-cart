package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.AdminTagResponse;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.common.enums.TagStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TagService {
    //admin
    AdminTagResponse create(TagCreateRequest createRequest);
    AdminTagResponse update(Long id,TagUpdateRequest updateRequest);
    AdminTagResponse getTagById(Long id);
    AdminTagResponse getTagBySlug(String slug);
    Slice<AdminTagResponse> findTags(String keyword,TagStatus status, Pageable pageable);
    void deleteById(List<Long> ids);
    void restoreById(List<Long> ids);

    TagResponse getActiveTagById(Long id);
    TagResponse getActiveTagBySlug(String slug);
    List<TagResponse> findActiveTags();

}
