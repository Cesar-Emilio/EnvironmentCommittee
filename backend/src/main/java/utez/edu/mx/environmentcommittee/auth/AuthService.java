package utez.edu.mx.environmentcommittee.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.auth.DTO.AuthLoginDTO;
import utez.edu.mx.environmentcommittee.modules.group.Group;
import utez.edu.mx.environmentcommittee.modules.group.GroupRepository;
import utez.edu.mx.environmentcommittee.modules.user.User;
import utez.edu.mx.environmentcommittee.modules.user.UserDetailsImpl;
import utez.edu.mx.environmentcommittee.modules.user.UserRepository;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;
import utez.edu.mx.environmentcommittee.utils.security.JWTUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private CustomResponseEntity customResponseEntity;

    private JWTUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    private GroupRepository groupRepository;

    @Autowired
    public AuthService(UserRepository userRepository, CustomResponseEntity customResponseEntity, JWTUtil jwtUtil, PasswordEncoder passwordEncoder, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.customResponseEntity = customResponseEntity;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> login(AuthLoginDTO authLoginDTO) {
        User found = userRepository.findByEmailOrUsername(authLoginDTO.getUsername()).orElse(null);

        if (found == null) {
            return customResponseEntity.get404Response("El usuario no se ha encontrado");
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

                //Colocar el grupo si es que existe
                Optional<Group> group = groupRepository.findByUsersId(found.getId());
                if (group.isPresent()) {
                    userResponse.put("group", group.get());
                } else

                if (found.getGroup() != null) {
                    userResponse.put("group", found.getGroup());
                }

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
                response.put("user", userResponse);

                logger.info("Response: " + response);

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