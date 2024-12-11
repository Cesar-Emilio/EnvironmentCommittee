package utez.edu.mx.environmentcommittee.utils;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomResponseEntity {

    private Map<String, Object> body;

    // OK Response
    public ResponseEntity<?> getOkResponse(String message, String status, int code, Object data) {
        body = new HashMap<>();
        body.put("message", message);
        body.put("status", status);
        body.put("code", code);
        if (data != null) {
            body.put("data", data);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // BAD REQUEST Response with default message
    public ResponseEntity<?> get400Response() {
        return get400Response("Ha ocurrido un error en la operación");
    }

    // BAD REQUEST Response with custom message
    public ResponseEntity<?> get400Response(String message) {
        body = new HashMap<>();
        body.put("message", message);
        body.put("status", "BAD_REQUEST");
        body.put("code", 400);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // UNAUTHORIZED Response with default message
    public ResponseEntity<?> get401Response() {
        return get401Response("Credenciales incorrectas");
    }

    // UNAUTHORIZED Response with custom message
    public ResponseEntity<?> get401Response(String message) {
        body = new HashMap<>();
        body.put("message", message);
        body.put("status", "UNAUTHORIZED");
        body.put("code", 401);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // NOT FOUND Response with default message
    public ResponseEntity<?> get404Response() {
        body = new HashMap<>();
        body.put("message", "El recurso no se ha encontrado");
        body.put("status", "NOT_FOUND");
        body.put("code", 404);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // NOT FOUND Response with custom message
    public ResponseEntity<?> get404Response(String message) {
        body = new HashMap<>();
        body.put("message", message);
        body.put("status", "NOT_FOUND");
        body.put("code", 404);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
