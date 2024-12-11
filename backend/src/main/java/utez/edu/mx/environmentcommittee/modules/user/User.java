package utez.edu.mx.environmentcommittee.modules.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu.mx.environmentcommittee.modules.event.Event;
import utez.edu.mx.environmentcommittee.modules.group.Group;
import utez.edu.mx.environmentcommittee.modules.role.Role;

import java.util.List;

@Entity
@Table(name = "user")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private long id;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "lastname", nullable = false)
        private String lastname;

        @Column(name = "phone", nullable = false)
        private String phone;

        @Column(name = "email", nullable = false, unique = true)
        private String email;

        @Column(name = "username", nullable = false, unique = true)
        private String username;

        @Column(name = "password", nullable = false)
        private String password;

        @ManyToOne
        @JoinColumn(name = "group_id")
        @JsonIgnore
        private Group group;

        @ManyToOne
        @JoinColumn(name = "role_id", nullable = false)
        private Role role;

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

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public Group getGroup() {return group;}
        public void setGroup(Group group) {this.group = group;}
}
