package utez.edu.mx.environmentcommittee.auth.DTO;

import jakarta.validation.constraints.*;

public class AuthLoginDTO {
    @NotNull(message = "El usuario no puede ser nulo.")
    @Size(min = 1, message = "El usuario no puede estar vacío.")
    private String username;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Size(min = 1, message = "La contraseña no puede estar vacía.")
    private String password;

    public AuthLoginDTO() {
    }

    public AuthLoginDTO(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}