package hoang.shop.common.until;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SortUtils {
    private SortUtils() {}

    public static Pageable applySort(String sort, Pageable pageable) {
        Sort sortSpec;
        switch (sort == null ? "" : sort) {
            case "price_asc" -> sortSpec = Sort.by("minPrice").ascending();
            case "price_desc" -> sortSpec = Sort.by("minPrice").descending();
            case "name_asc" -> sortSpec = Sort.by("name").ascending();
            case "name_desc" -> sortSpec = Sort.by("name").descending();
            default -> sortSpec = Sort.by("createdAt").descending(); // newest
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortSpec
        );
    }
}
