package utez.edu.mx.environmentcommittee.modules.user.DTO;

import utez.edu.mx.environmentcommittee.modules.role.Role;

public class UserDTO {
    private long id;
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private String username;
    private Role role;
    private String group;

    public UserDTO() {}

    public UserDTO(long id, String name, String lastname, String phone, String email, String username, Role role, String group) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.role = role;
        this.group = group;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
