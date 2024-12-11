package utez.edu.mx.environmentcommittee.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.auth.AuthController;
import utez.edu.mx.environmentcommittee.auth.AuthService;
import utez.edu.mx.environmentcommittee.auth.DTO.AuthLoginDTO;
import utez.edu.mx.environmentcommittee.modules.user.DTO.UserDTO;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CustomResponseEntity customResponseEntity, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customResponseEntity = customResponseEntity;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDTO transformUserToDTO(User u) {
        return new UserDTO(
                u.getId(),
                u.getName(),
                u.getLastname(),
                u.getPhone(),
                u.getEmail(),
                u.getUsername(),
                u.getRole(),
                u.getGroup() != null ? u.getGroup().getName() : "No asignado"
        );
    }


    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll() {
        List<User> users = userRepository.findAllWithGroupAndRole();
        if (users.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron usuarios", "OK", 200, null);
        }

        List<UserDTO> userDtos = users.stream()
                .map(this::transformUserToDTO)
                .collect(Collectors.toList());

        return customResponseEntity.getOkResponse("Usuarios encontrados", "OK", 200, userDtos);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(long id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        if (user.isEmpty()) {
            return customResponseEntity.get404Response();
        }

        UserDTO userDto = transformUserToDTO(user.get());
        return customResponseEntity.getOkResponse("Usuario encontrado", "OK", 200, userDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findByRoleId(long roleId) {
        List<User> users = userRepository.findByRoleId(roleId);
        if (users.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron usuarios para este rol", "OK", 200, null);
        }

        List<UserDTO> userDtos = users.stream()
                .map(this::transformUserToDTO)
                .collect(Collectors.toList());

        return customResponseEntity.getOkResponse("Usuarios encontrados", "OK", 200, userDtos);
    }

    //Al usarse para el registro de usuario, debe retornar el token de acceso del método login
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo electrónico ya está registrado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.saveAndFlush(user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario");
        }

        AuthLoginDTO authLoginDTO = new AuthLoginDTO();
        authLoginDTO.setUser(user.getEmail());
        authLoginDTO.setPassword(user.getPassword());
        return new AuthService().login(authLoginDTO);
    }


    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> update(User user) {
        User existingUser = userRepository.findById(user.getId());
        if (existingUser == null) {
            return customResponseEntity.get404Response();
        }

        try {
            // Validar y asignar campos requeridos
            existingUser.setName(user.getName());
            existingUser.setLastname(user.getLastname());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setUsername(user.getUsername());

            // Validar y asignar rol
            if (user.getRole() != null && user.getRole().getId() > 0) {
                existingUser.setRole(user.getRole());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El rol es obligatorio y debe contener un ID válido.");
            }

            // Si la contraseña no está vacía, actualizarla
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(existingUser);
            return customResponseEntity.getOkResponse("Usuario actualizado correctamente", "OK", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el usuario.");
        }
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> deleteById(long id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        if (user.isEmpty()) {
            return customResponseEntity.get404Response();
        }

        try {
            userRepository.deleteById(id);
            return customResponseEntity.getOkResponse("Usuario eliminado correctamente", "OK", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response();
        }
    }
}
