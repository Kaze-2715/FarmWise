package com.farmwise.common.exception;

import com.farmwise.common.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponse> handleBizException(BizException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
                    MethodArgumentNotValidException exception) {
            String message = exception
                            .getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .findFirst()
                            .map(FieldError::getDefaultMessage)
                            .orElse("请求参数不合法");

            return ResponseEntity
                            .badRequest()
                            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize() {
        return ResponseEntity
        .status(HttpStatus.CONTENT_TOO_LARGE)
                        .body(new ErrorResponse("上传文件不能超过 5 MB"));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(
            HandlerMethodValidationException exception) {
        String message = exception
                .getAllErrors()
                .stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("请求参数不合法");

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable() {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("请求体格式错误"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception) {
        String message = "请求参数 " + exception.getName() + " 类型错误";

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
            MissingServletRequestParameterException exception) {
        String message = "缺少请求参数 " + exception.getParameterName();

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse("请求的资源不存在"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse("请求方法不支持"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(
            Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse webError) {
            HttpStatusCode status = webError.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("Spring Web 服务器异常", exception);
            }

            String message = status.is5xxServerError()
                    ? "服务器内部错误"
                    : "请求处理失败";

            return ResponseEntity
                    .status(status)
                    .body(new ErrorResponse(message));
        }

        log.error("未处理的服务器异常", exception);

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse("服务器内部错误"));
    }
}
