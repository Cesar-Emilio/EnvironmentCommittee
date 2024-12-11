package utez.edu.mx.environmentcommittee.auth.DTO;

import jakarta.validation.constraints.*;

public class AuthLoginDTO {
    @NotNull(message = "El usuario no puede ser nulo.")
    @Size(min = 1, message = "El usuario no puede estar vacío.")
    private String user;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Size(min = 1, message = "La contraseña no puede estar vacía.")
    private String password;

    public AuthLoginDTO() {
    }

    public AuthLoginDTO(String password, String user) {
        this.password = password;
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}