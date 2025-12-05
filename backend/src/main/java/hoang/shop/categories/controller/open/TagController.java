package hoang.shop.categories.controller.open;

import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.model.ProductTag;
import hoang.shop.categories.service.ProductTagService;
import hoang.shop.categories.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> findActiveTags() {
        List<TagResponse> tags = tagService.findActiveTags();
        return ResponseEntity.ok(tags);
    }
    @GetMapping("/{slug}")
    public TagResponse getActiveTagBySlug(@PathVariable String slug) {
        return tagService.getActiveTagBySlug(slug);
    }
}
