package com.cocoiland.pricehistory.handler;

import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.exceptions.ESException;
import com.cocoiland.pricehistory.exceptions.ServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends DefaultHandlerExceptionResolver {

    //TODO: remove error from documentation of exception handlers

    /**
     * Handle errors related to invalid method argument exceptions
     *
     * @param e MethodArgumentNotValidException type exceptions
     * @return Map<String, String> => map containing fields and their error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }
    /**
     * Handle errors related to exceptions in Service
     *
     * @param e exceptions from Service operation
     * @return ResponseEntity
     */
    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<Object> handleServiceException(ServiceException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", Constants.SOMETHING_WENT_WRONG);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle errors related to elastic search exceptions
     *
     * @param e exceptions from ES operation
     * @return ResponseEntity
     */
    @ExceptionHandler({ESException.class})
    public ResponseEntity<Object> handleElasticSearchException(ESException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", Constants.ES_ERROR_OCCURRED);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle errors related to invalid method argument exceptions
     *
     * @param e JsonParseException type exceptions
     * @return Map<String, String> => map containing fields and their error messages
     */
    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleJsonParseException(JsonParseException e) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Invalid request, Please check input values");
        return body;
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleDateTimeParseException(DateTimeParseException e) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Invalid date format found, accepted date format is " + Constants.DATE_FORMAT);
        return body;
    }
}
