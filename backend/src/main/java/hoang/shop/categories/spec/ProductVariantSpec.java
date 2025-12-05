package hoang.shop.categories.spec;

import hoang.shop.categories.dto.request.ProductVariantSearchCondition;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.enums.ProductVariantStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductVariantSpec {
    public static Specification<ProductVariant> build( ProductVariantSearchCondition condition){
        if (condition == null) return null;
        return Specification
                .where(productIdEq(condition.productId()))
                .and(colorEq(condition.colorId()))
                .and(sizeEq(condition.size()))
                .and(minStock(condition.minStock()))
                .and(maxStock(condition.maxStock()))
                .and(keywordLike(condition.keyword())
                        .and(statusEq(condition.status())));
    }

    private static Specification<ProductVariant> sizeEq(String size) {
        return (root, query, cb) ->
                size==null ? null : cb.equal(root.get("size"),size);
    }

    private static Specification<ProductVariant> productIdEq(Long productId){
        return (root, query, cb) ->
                productId==null ? null : cb.equal(root.get("product").get("id"),productId);
    }

    private static Specification<ProductVariant> colorEq(Long color){
        return (root, query, cb) ->
                color==null ? null : cb.equal(root.get("color"),color);
    }
    private static Specification<ProductVariant> minStock(Integer minStock){
        return (root, query, cb) ->
                minStock==null ? null : cb.greaterThanOrEqualTo(root.get("stock"),minStock);
    }
    private static Specification<ProductVariant> maxStock(Integer maxTock){
        return (root, query, cb) ->
                maxTock==null ? null : cb.lessThanOrEqualTo(root.get("stock"),maxTock);
    }
    private static Specification<ProductVariant> statusEq(ProductVariantStatus status){
        return (root, query, cb) ->
                status==null ? null : cb.equal(root.get("status"), status);
    }
    private static Specification<ProductVariant> keywordLike(String keyword){
        return (root, query, cb) ->{
            if (keyword == null||keyword.isBlank()) return null;
            String pattern = "%" + keyword.trim() + "%";
            return cb.or(
                    cb.like(root.get("name"),pattern),
                    cb.like(root.get("slug"),pattern));
        };
    }



}
