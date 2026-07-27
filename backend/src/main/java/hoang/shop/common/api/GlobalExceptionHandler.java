package hoang.shop.common.api;

import hoang.shop.common.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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


    // 400
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
        pd.setProperty("code", "VALIDATION_ERROR");
        return pd;
    }

    //400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest req
    ) {
        log.warn("Malformed request body", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(resolveMessage("error.request.malformed.title"));
        pd.setDetail(resolveMessage("error.request.malformed.detail"));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("code", "MALFORMED_JSON");
        return pd;
    }

    //400
    @ExceptionHandler({BadRequestException.class})
    public ProblemDetail handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        log.warn("Illegal request/state", ex);
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, resolveMessage("error.bad-request.title"), ex, req);
        pd.setProperty("code", "BAD_REQUEST");
        return pd;

    }

    // 400 - missing request parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest req
    ) {
        String detail = "Required request parameter '" + ex.getParameterName() + "' is missing.";
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.request.missing-parameter.title"),
                detail,
                req
        );
        pd.setProperty("parameter", ex.getParameterName());
        pd.setProperty("expectedType", ex.getParameterType());
        pd.setProperty("code", "MISSING_PARAMETER");
        return pd;
    }

    // 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req
    ) {
        String expectedType = ex.getRequiredType() == null
                ? "unknown"
                : ex.getRequiredType().getSimpleName();
        String detail = "Request value '" + ex.getName() + "' has invalid type.";
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.request.type-mismatch.title"),
                detail,
                req
        );
        pd.setProperty("parameter", ex.getName());
        pd.setProperty("rejectedValue", ex.getValue());
        pd.setProperty("expectedType", expectedType);
        pd.setProperty("code", "TYPE_MISMATCH");
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest req
    ) {
        ProblemDetail pd = problem(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.constraint-violation.title"),
                "Request parameter validation failed.",
                req
        );

        pd.setProperty("code", "CONSTRAINT_VIOLATION");

        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> resolveMessage(v.getMessage()),
                        (a, b) -> a
                ));

        pd.setProperty("errors", errors);

        return pd;
    }

    //401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest req
    ) {
        ProblemDetail pd = problem(
                HttpStatus.UNAUTHORIZED,
                resolveMessage("error.auth.title"),
                resolveMessage("error.auth.invalid-credentials"),
                req
        );

        pd.setProperty("code", "INVALID_CREDENTIALS");
        return pd;
    }

    //404
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, resolveMessage("error.not-found.title"), ex, req);
        pd.setProperty("code", "NOT_FOUND");
        return pd;
    }

    //409
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        ProblemDetail pb = problem(HttpStatus.CONFLICT, resolveMessage("error.duplicate.title"), ex, req);
        pb.setProperty("code", "DUPLICATE_RESOURCE");
        return pb;
    }

    //409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        String detail = resolveMessage("error.conflict.detail");
        if ("error.conflict.detail".equals(detail)) {
            detail = "Resource state conflicts with existing data (unique constraint or FK).";
        }
        ProblemDetail pd = problem(HttpStatus.CONFLICT, resolveMessage("error.conflict.title"), detail, req);
        pd.setProperty("code", "DATA_INTEGRITY_VIOLATION");
        return pd;
    }

    //500
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnknown(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        String title = resolveMessage("error.internal.title");
        if ("error.internal.title".equals(title)) title = "Internal Server Error";
        String detail = resolveMessage("error.internal.detail");
        if ("error.internal.detail".equals(detail)) detail = "An unexpected error occurred.";
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, req);
        pd.setProperty("code", "INTERNAL_ERROR");
        return pd;
    }

}
