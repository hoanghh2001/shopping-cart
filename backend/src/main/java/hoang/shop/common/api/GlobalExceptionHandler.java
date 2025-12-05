package hoang.shop.common.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // ===== Helpers ===========================================================

    /** Resolve message key like "{user.lastName.required}" -> localized text. */
    private String resolveMessage(String keyOrText) {
        if (keyOrText == null) return null;
        String cleanKey = keyOrText.replaceAll("[{}]", ""); // bỏ ngoặc nếu có
        Locale locale = LocaleContextHolder.getLocale();
        // Nếu không tìm thấy key -> trả lại key để debug, không ném NoSuchMessageException
        return messageSource.getMessage(cleanKey, null, cleanKey, locale);
    }

    /** Build ProblemDetail từ detail (string) — set đầy đủ RFC 7807 fields. */
    private ProblemDetail problem(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("instance", req.getRequestURI());
        return pd;
    }

    /** Build ProblemDetail từ Exception — tự i18n message key trong ex.getMessage(). */
    private ProblemDetail problem(HttpStatus status, String title, Exception ex, HttpServletRequest req) {
        return problem(status, title, resolveMessage(ex.getMessage()), req);
    }

    // ===== Handlers ==========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        // Dịch message cho từng field nếu là dạng key {…}
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> {
                            String msg = fe.getDefaultMessage();
                            return msg == null ? "Invalid" : resolveMessage(msg);
                        },
                        (a, b) -> a // nếu trùng field, giữ message đầu
                ));

        // Title + detail cũng qua i18n key (tùy bạn có đặt trong messages.properties hay không)
        String title = resolveMessage("error.validation.title");  // nếu không có key -> "error.validation.title"
        if ("error.validation.title".equals(title)) title = "Validation failed";
        String detail = resolveMessage("error.validation.detail");
        if ("error.validation.detail".equals(detail)) detail = "One or more fields are invalid.";

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, title, detail, req);
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, resolveMessage("error.notfound.title"), ex, req);
        // nếu thiếu key "error.notfound.title" -> trả chính key; bạn có thể thêm vào messages.properties:
        // error.notfound.title=Not Found
    }

    @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class, IllegalStateException.class })
    public ProblemDetail handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        return problem(HttpStatus.BAD_REQUEST, resolveMessage("error.badrequest.title"), ex, req);
        // messages.properties:
        // error.badrequest.title=Bad Request
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, resolveMessage("error.duplicate.title"), ex, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        // Detail dùng key chung để bạn có thể dịch:
        String detail = resolveMessage("error.conflict.detail");
        if ("error.conflict.detail".equals(detail)) {
            detail = "Resource state conflicts with existing data (unique constraint or FK).";
        }
        return problem(HttpStatus.CONFLICT, resolveMessage("error.conflict.title"), detail, req);
        // messages.properties:
        // error.conflict.title=Conflict
        // error.conflict.detail=既存データと矛盾しています。（一意制約もしくは外部キー制約）
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnknown(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        String title = resolveMessage("error.internal.title");
        if ("error.internal.title".equals(title)) title = "Internal Server Error";
        String detail = resolveMessage("error.internal.detail");
        if ("error.internal.detail".equals(detail)) detail = "An unexpected error occurred.";
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, req);
    }
}
