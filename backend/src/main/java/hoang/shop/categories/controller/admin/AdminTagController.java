package hoang.shop.categories.controller.admin;


import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.service.TagService;
import hoang.shop.common.enums.status.TagStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class    TagController {
    private final TagService service;
    @PostMapping
    public ResponseEntity<TagResponse> create(@RequestBody TagCreateRequest createRequest) {
        TagResponse response = service.create(createRequest);
        URI location = URI.create("api/tags/"+response.id());
        return ResponseEntity.created(location).body(response);
    }
    @PatchMapping("{id}")
    public ResponseEntity<TagResponse> update(@PathVariable Long id, @RequestBody TagUpdateRequest updateRequest) {
        TagResponse response = service.update(id, updateRequest);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products")
    public ResponseEntity<List<TagResponse>> findTagWithProduct(@RequestParam Long productId) {
        List<TagResponse> body = service.findTagWithProduct(productId);
        return ResponseEntity.ok(body);
    }
    @GetMapping("{tagId}")
    public ResponseEntity<TagResponse> findById(@PathVariable Long tagId) {
        TagResponse response = service.findById(tagId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/slug")
    public ResponseEntity<TagResponse> findBySlug(@RequestParam String slug) {
        TagResponse response = service.findBySlug(slug);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<Slice<TagResponse>> findByStatus(@RequestParam(required = false) TagStatus status, Pageable pageable) {
        Slice<TagResponse> page = service.findByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/name")
    public ResponseEntity<Slice<TagResponse>> findByName(@RequestParam String name, Pageable pageable) {
        Slice<TagResponse> page = service.findByName(name, pageable);
        return ResponseEntity.ok(page);
    }
    @PatchMapping("/delete")
    public ResponseEntity<Integer> deleteById(@RequestBody IdListRequest ids) {
        return ResponseEntity.ok(service.deleteById(ids));
    }
    @PatchMapping("/restore")
    public ResponseEntity<Integer> restoreById(@RequestBody IdListRequest ids) {
        return ResponseEntity.ok(service.restoreById(ids));
    }
}
