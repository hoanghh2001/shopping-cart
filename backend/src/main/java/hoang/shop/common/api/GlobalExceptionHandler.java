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


    private String resolveMessage(String keyOrText) {
        if (keyOrText == null) return null;
        String cleanKey = keyOrText.replaceAll("[{}]", "");
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(cleanKey, null, cleanKey, locale);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }

    private ProblemDetail problem(HttpStatus status, String title, Exception ex, HttpServletRequest req) {
        return problem(status, title, resolveMessage(ex.getMessage()), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> {
                            String msg = fe.getDefaultMessage();
                            return msg == null ? "Invalid" : resolveMessage(msg);
                        },
                        (a, b) -> a
                ));

        String title = resolveMessage("error.validation.title");
        if ("error.validation.title".equals(title)) title = "Validation failed";
        String detail = resolveMessage("error.validation.detail");
        if ("error.validation.detail".equals(detail)) detail = "One or more fields are invalid.";

        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, title, detail, req);
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, resolveMessage("error.not-found.title"), ex, req);
    }

    @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class, IllegalStateException.class })
    public ProblemDetail handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        return problem(HttpStatus.BAD_REQUEST, resolveMessage("error.bad-request.title"), ex, req);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, resolveMessage("error.duplicate.title"), ex, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        String detail = resolveMessage("error.conflict.detail");
        if ("error.conflict.detail".equals(detail)) {
            detail = "Resource state conflicts with existing data (unique constraint or FK).";
        }
        return problem(HttpStatus.CONFLICT, resolveMessage("error.conflict.title"), detail, req);
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
