package utez.edu.mx.environmentcommittee.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.auth.AuthService;
import utez.edu.mx.environmentcommittee.auth.DTO.AuthLoginDTO;
import utez.edu.mx.environmentcommittee.modules.group.GroupRepository;
import utez.edu.mx.environmentcommittee.modules.role.Role;
import utez.edu.mx.environmentcommittee.modules.role.RoleRepository;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;
import utez.edu.mx.environmentcommittee.utils.security.JWTUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private JWTUtil jwtUtil;

    public UserService(UserRepository userRepository, CustomResponseEntity customResponseEntity, PasswordEncoder passwordEncoder, RoleRepository roleRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.customResponseEntity = customResponseEntity;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
    }


    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll() {
        List<User> users = userRepository.findAllWithGroupAndRole();
        if (users.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron usuarios", "OK", 200, null);
        }

        return customResponseEntity.getOkResponse("Usuarios encontrados", "OK", 200, users);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(long id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        if (user.isEmpty()) {
            return customResponseEntity.get404Response();
        }

        return customResponseEntity.getOkResponse("Usuario encontrado", "OK", 200, user);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findByRoleId(long roleId) {
        List<User> users = userRepository.findByRoleId(roleId);
        if (users.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron usuarios para este rol", "OK", 200, null);
        }

        return customResponseEntity.getOkResponse("Usuarios encontrados", "OK", 200, users);
    }

    //Al usarse para el registro de usuario, debe retornar el token de acceso del método login
    //Nota 2: Supondré que este es el método que se encarga de registrar un usuario
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> save(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo electrónico ya está registrado");
        }
        User user = new User();

        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (isRoleIdValid(dto.getRoleId())) {
            user.setRole(roleRepository.findById(dto.getRoleId()).get());
        } else {
            return customResponseEntity.get404Response("No se encontró el rol con el ID proporcionado");
        }

        //Suponer que un usuario empieza sin grupo al registrarse
        if (dto.getGroupId() != null) {
            if (isGroupIdValid(dto.getGroupId())) {
                user.setGroup(groupRepository.findById(dto.getGroupId()).get());
                userRepository.save(user);
            } else {
                return customResponseEntity.get404Response("No se encontró el grupo con el ID proporcionado");
            }
        }

        user = userRepository.saveAndFlush(user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario");
        }

        UserDetails userDetails = new UserDetailsImpl(user);
        String jwt = jwtUtil.generateToken(userDetails);

        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("name", user.getName());
        userResponse.put("lastname", user.getLastname());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("phone", user.getPhone());
        userResponse.put("role", user.getRole());
        if (user.getGroup() != null) {
            userResponse.put("group", user.getGroup());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", userResponse);

        return customResponseEntity.getOkResponse(
                "Registro exitoso",
                "OK",
                200,
                response
        );
    }


    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> update(UserDTO user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
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
            if (isRoleIdValid(user.getRoleId())) {
                existingUser.setRole(roleRepository.findById(user.getRoleId()).get());
            } else {
                return customResponseEntity.get404Response("No se encontró el rol con el ID proporcionado");
            }

            // Si la contraseña no está vacía, actualizarla
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Si el grupo es valido, asignarlo
            if (isGroupIdValid(user.getGroupId())) {
                existingUser.setGroup(groupRepository.findById(user.getGroupId()).get());
            } else {
                return customResponseEntity.get404Response("No se encontró el grupo con el ID proporcionado");
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

    private boolean isRoleIdValid(Long roleId) {
        return roleId != null && roleRepository.existsById(roleId);
    }

    private boolean isGroupIdValid(Long groupId) {
        return groupId != null && groupRepository.existsById(groupId);
    }
}
