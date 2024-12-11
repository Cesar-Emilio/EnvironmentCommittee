package utez.edu.mx.environmentcommittee.modules.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu.mx.environmentcommittee.modules.type.Type;
import utez.edu.mx.environmentcommittee.modules.user.User;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "event")
public class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private long id;

        @Column(name = "title", nullable = false)
        private String title;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Column(name = "date", nullable = false)
        @Temporal(TemporalType.DATE)
        private Date date;

        @Column(name = "status", nullable = false)
        private String status;

        @ManyToOne
        @JoinColumn(name = "type_id", nullable = false)
        private Type type;

        @ManyToMany
        @JsonIgnore
        @JoinTable(
                name = "event_has_users",
                joinColumns = @JoinColumn(name = "event_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id")
        )
        private List<User> users;

        public Event() {}

        public Event(String title, Date date, String status) {
                this.title = title;
                this.date = date;
                this.status = status;
        }

        public Event(long id, String title, Date date, String status) {
                this.id = id;
                this.title = title;
                this.date = date;
                this.status = status;
        }

        public Event(String title, Date date, String status, Type type, List<User> users) {
                this.title = title;
                this.date = date;
                this.status = status;
                this.type = type;
                this.users = users;
        }

        public Event(long id, String title, Date date, String status, Type type, List<User> users) {
                this.id = id;
                this.title = title;
                this.date = date;
                this.status = status;
                this.type = type;
                this.users = users;
        }

        public long getId() {
                return id;
        }

        public void setId(long id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public Date getDate() {
                return date;
        }

        public void setDate(Date date) {
                this.date = date;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public Type getType() {
                return type;
        }

        public void setType(Type type) {
                this.type = type;
        }

        public List<User> getUsers() {
                return users;
        }

        public void setUsers(List<User> users) {
                this.users = users;
        }
}
