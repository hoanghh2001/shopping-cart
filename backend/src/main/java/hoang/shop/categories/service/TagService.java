package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.common.enums.status.TagStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TagService {

    // create && update
    TagResponse create(TagCreateRequest createRequest);
    TagResponse update(Long id,TagUpdateRequest updateRequest);
    // read && search list
    TagResponse findById(Long id);
    TagResponse findBySlug(String slug);
    Slice<TagResponse> findByName(String name, Pageable pageable);
    Slice<TagResponse> findByStatus(TagStatus status, Pageable pageable);

    // delete && restore
    Integer deleteById(IdListRequest ids);
    Integer restoreById(IdListRequest ids);

}
