package com.cocoiland.pricehistory.handler;

import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.exceptions.ESException;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestControllerAdvice
public class GlobalExceptionHandler extends DefaultHandlerExceptionResolver {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult()
//                .getFieldErrors()
//                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//
//        return errors;
//    }

//    /**
//     * Handle errors related to elastic search exceptions
//     *
//     * @param ex exceptions from ES operation
//     * @return ResponseEntity
//     */
//    @ExceptionHandler({ESException.class})
//    public ResponseEntity<Object> handleElasticSearchException(ESException ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("message", Constants.ES_ERROR_OCCURRED);
//        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(JsonParseException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Map<String, String> handleJsonParseException(JsonParseException e) {
//        Map<String, String> body = new HashMap<>();
//        body.put("message", "Invalid request, Please check input values");
//        return body;
//    }
}
