package hoang.shop.order.spec;

import hoang.shop.common.enums.OrderStatus;
import hoang.shop.common.enums.PaymentStatus;
import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderSpec {

    public static Specification<Order> build(OrderSearchCondition condition){
        if (condition == null) return null;
        return Specification
                .where(userIdEq(condition.userId()))
                .and(orderStatusEq(condition.orderStatus()))
                .and(paymentStatusEq(condition.paymentStatus()))
                .and(fromDate(condition.fromDate()))
                .and(toDate(condition.toDate()))
                .and(minTotal(condition.minTotal()))
                .and(maxTotal(condition.maxTotal()))
                .and(keywordLike(condition.keyword()));


    }
    private static Specification<Order> userIdEq(Long userId){
        return (root, query, cb) ->
                userId==null ? null : cb.equal(root.get("user").get("id"),userId);
    }
    private static Specification<Order> orderStatusEq(OrderStatus status){
        return (root, query, cb) ->
                status==null ? null : cb.equal(root.get("orderStatus") ,status);
    }
    private static Specification<Order> paymentStatusEq(PaymentStatus status){
        return (root, query, cb) ->
                status==null ? null : cb.equal(root.get("paymentStatus"),status);

    }private static Specification<Order> fromDate(Instant from){
        return (root, query, cb) ->
                from==null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"),from);
    }
    private static Specification<Order> toDate(Instant to){
        return (root, query, cb) ->
                to==null ? null : cb.lessThanOrEqualTo(root.get("createdAt"),to);
    }
    private static Specification<Order> minTotal(BigDecimal min){
        return (root, query, cb) ->
                min==null ? null : cb.greaterThanOrEqualTo(root.get("grandTotal"),min);
    }
    private static Specification<Order> maxTotal(BigDecimal max) {
        return (root, query, cb) ->
                max == null ? null : cb.greaterThanOrEqualTo(root.get("grandTotal"), max);
    }
    private static Specification<Order> keywordLike(String  keyword) {
        return (root, query, cb) ->{
            if (keyword == null||keyword.isBlank()) return null;
            String pattern = "%" + keyword.trim() + "%";
            return cb.or(
                    cb.like(root.get("receiverNameSnapshot"),pattern),
                    cb.like(root.get("receiverPhoneSnapshot"),pattern),
                    cb.like(root.get("userFullNameSnapshot"),pattern)
            );
        };

    }
}
