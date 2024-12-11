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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ResponseEntity<?> login(AuthLoginDTO authLoginDTO){
        User found = userRepository.findByEmailOrUsername(authLoginDTO.getUser()).orElse(null);

        if (found == null){
            return customResponseEntity.get404Response("El usuario no encontrado");
        } else {

            //Validar la contraseña usando matches
            if (!passwordEncoder.matches(authLoginDTO.getPassword(), found.getPassword())) {
                return customResponseEntity.get401Response();
            }

            try {
                UserDetails userDetails = new UserDetailsImpl(found);

                //Generar el token
                String jwt = jwtUtil.generateToken(userDetails);

                //Obtener atributos del token generado
                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
                response.put("user", Map.of(
                        "id", found.getId(),
                        "name", found.getName(),
                        "lastname", found.getLastname(),
                        "username", found.getUsername(),
                        "email", found.getEmail(),
                        "group", found.getGroup(),
                        "phone", found.getPhone(),
                        "role", found.getRole(),
                        "pass", found.getPassword()
                ));

                return customResponseEntity.getOkResponse(
                        "Inicio de sesion exitoso",
                        "OK",
                        200,
                        response
                );
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                return customResponseEntity.get400Response();
            }
        }
    }
}