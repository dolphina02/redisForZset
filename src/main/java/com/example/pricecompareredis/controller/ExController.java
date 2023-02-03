package com.example.pricecompareredis.controller;

import com.example.pricecompareredis.vo.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExController {


//    @ExceptionHandler({RuntimeException.class})
//    public ResponseEntity<Object> BadRequestException(final RuntimeException ex) {
//        return ResponseEntity.badRequest().body(ex.getMessage());
//    }

//    @ExceptionHandler({ xxxx.class })
//    public ResponseEntity<Object> EveryException(final Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<Object> NotFoundExceptionResponse(NotFoundException ex) {
        return new ResponseEntity<>(ex.getErrmsg(), ex.getHttpStatus());
    }
}

