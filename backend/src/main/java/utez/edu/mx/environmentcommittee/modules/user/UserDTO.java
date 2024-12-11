package utez.edu.mx.environmentcommittee.modules.user;

import utez.edu.mx.environmentcommittee.modules.role.Role;

public class UserDTO {
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private String username;
    private String password;
    private Long roleId;
    private Long groupId;

    public UserDTO() {}

    public UserDTO(String name, String lastname, String phone, String email, String username, String password, Long roleId, Long groupId) {
        this.name = name;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.groupId = groupId;
    }

    // Getters and Setters

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
}
