package hoang.shop.categories.controller.admin;


import hoang.shop.common.enums.TagStatus;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.AdminTagResponse;
import hoang.shop.categories.service.TagService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {
    private final TagService service;
    @PostMapping
    public ResponseEntity<AdminTagResponse> create(@RequestBody TagCreateRequest createRequest) {
        AdminTagResponse tag = service.create(createRequest);
        URI location = URI.create("api/admin/tags/"+tag.id());
        return ResponseEntity.created(location).body(tag);
    }
    @PatchMapping("/{tagId}")
    public ResponseEntity<AdminTagResponse> update(@PathVariable Long tagId, @RequestBody TagUpdateRequest updateRequest) {
        AdminTagResponse tag = service.update(tagId, updateRequest);
        return ResponseEntity.ok(tag);
    }
    @GetMapping("/{tagId}")
    public ResponseEntity<AdminTagResponse> getTagById(@PathVariable Long tagId) {
        AdminTagResponse tag = service.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }
    @GetMapping("/by-slug")
    public ResponseEntity<AdminTagResponse> getTagBySlug(@RequestParam String slug) {
        AdminTagResponse tag = service.getTagBySlug(slug);
        return ResponseEntity.ok(tag);
    }

    @GetMapping
    public ResponseEntity<Slice<AdminTagResponse>> findTags(String keyword, TagStatus status, Pageable pageable) {
        Slice<AdminTagResponse> tags =  service.findTags(keyword, status, pageable);
        return ResponseEntity.ok(tags);
    }


    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteById(@RequestBody List<Long> ids) {
        service.deleteById(ids);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/restore")
    public ResponseEntity<Integer> restoreById(@RequestBody List<Long> ids) {
        service.restoreById(ids);
        return ResponseEntity.noContent().build();
    }

}
