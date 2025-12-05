package hoang.shop.categories.spec;

import hoang.shop.categories.dto.request.ProductSearchCondition;
import hoang.shop.categories.dto.request.PublicProductSearchCondition;
import hoang.shop.categories.model.Product;
import hoang.shop.common.enums.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpec {
    public static Specification<Product> build(ProductSearchCondition condition){
        if (condition == null) return null;
        return Specification
                .where(categoryIdEq(condition.categoryId()))
                .and(brandIdEq(condition.brandId()))
                .and(tagIdEq(condition.tagId()))
                .and(minPrice(condition.minPrice()))
                .and(maxPrice(condition.maxPrice()))
                .and(keywordLike(condition.keyword())
                            .and(statusEq(condition.status())));
    }
    public static Specification<Product> buildPublic(PublicProductSearchCondition condition){
        if (condition == null) return null;
        return Specification
                .where(categoryIdEq(condition.categoryId()))
                .and(brandIdEq(condition.brandId()))
                .and(tagIdEq(condition.tagId()))
                .and(minPrice(condition.minPrice()))
                .and(maxPrice(condition.maxPrice()))
                .and(keywordLike(condition.keyword())
                );
    }


    private static Specification<Product> categoryIdEq(Long categoryId){
        return (root, query, cb) ->
                categoryId==null ? null : cb.equal(root.get("category").get("id"),categoryId);
    }
    private static Specification<Product> brandIdEq(Long brandId){
        return (root, query, cb) ->
                brandId==null ? null : cb.equal(root.get("brand").get("id"),brandId);
    }
    private static Specification<Product> tagIdEq(Long tagId){
        return (root, query, cb) ->
                tagId==null ? null : cb.equal(root.get("tag").get("id"),tagId);
    }
    private static Specification<Product> minPrice(BigDecimal price){
        return (root, query, cb) ->
                price==null ? null : cb.greaterThanOrEqualTo(root.get("minPrice"),price);
    }
    private static Specification<Product> maxPrice(BigDecimal price){
        return (root, query, cb) ->
                price==null ? null : cb.lessThanOrEqualTo(root.get("maxPrice"),price);
    }
    private static Specification<Product> statusEq(ProductStatus status){
        return (root, query, cb) ->
                status==null ? null : cb.equal(root.get("status"), status);
    }
    private static Specification<Product> keywordLike(String keyword){
        return (root, query, cb) ->{
            if (keyword == null||keyword.isBlank()) return null;
            String pattern = "%" + keyword.trim() + "%";
            return cb.or(
                    cb.like(root.get("name"),pattern),
                    cb.like(root.get("slug"),pattern));
        };
    }





}
