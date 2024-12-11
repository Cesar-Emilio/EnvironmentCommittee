package utez.edu.mx.environmentcommittee.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.environmentcommittee.auth.DTO.AuthLoginDTO;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginDTO authLoginDTO) {
        return authService.login(authLoginDTO);
    }

}