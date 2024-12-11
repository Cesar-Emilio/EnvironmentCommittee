package utez.edu.mx.environmentcommittee.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.auth.DTO.AuthLoginDTO;
import utez.edu.mx.environmentcommittee.modules.user.User;
import utez.edu.mx.environmentcommittee.modules.user.UserDetailsImpl;
import utez.edu.mx.environmentcommittee.modules.user.UserRepository;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;
import utez.edu.mx.environmentcommittee.utils.security.JWTUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private UserRepository userRepository;

    private CustomResponseEntity customResponseEntity;

    private JWTUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, CustomResponseEntity customResponseEntity, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customResponseEntity = customResponseEntity;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> login(AuthLoginDTO authLoginDTO) {
        User found = userRepository.findByEmailOrUsername(authLoginDTO.getUser()).orElse(null);

        if (found == null) {
            return customResponseEntity.get404Response("El usuario no encontrado");
        } else {
            if (!passwordEncoder.matches(authLoginDTO.getPassword(), found.getPassword())) {
                return customResponseEntity.get401Response();
            }

            try {
                UserDetails userDetails = new UserDetailsImpl(found);

                String jwt = jwtUtil.generateToken(userDetails);

                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("id", found.getId());
                userResponse.put("name", found.getName());
                userResponse.put("lastname", found.getLastname());
                userResponse.put("username", found.getUsername());
                userResponse.put("email", found.getEmail());
                userResponse.put("phone", found.getPhone());
                userResponse.put("role", found.getRole());

                if (found.getGroup() != null) {
                    userResponse.put("group", found.getGroup());
                }

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
                response.put("user", userResponse);

                return customResponseEntity.getOkResponse(
                        "Inicio de sesión exitoso",
                        "OK",
                        200,
                        response
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return customResponseEntity.get400Response();
            }
        }
    }

}