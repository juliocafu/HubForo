package com.hubforo.hub_foro.infra.errores;

import com.hubforo.hub_foro.ValidacionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity manejarError404EntidadNoEncontrada() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso no encontrado");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarError400(MethodArgumentNotValidException e){
        var errores =  e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity tratarError409(SQLIntegrityConstraintViolationException e){
        String mensaje = "El título o mensaje ya existe";
        return new ResponseEntity<>(mensaje, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity manejarError404Validacion(ValidacionException e) {
        String mensaje = "El ID ingresado no existe en la base de datos";
        return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity manejarErrorGenerico(Exception e) {
        String mensaje = "Error inesperado: " + e.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensaje);
    }

    private record DatosErrorValidacion(String campo, String error){

        public DatosErrorValidacion(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
}

