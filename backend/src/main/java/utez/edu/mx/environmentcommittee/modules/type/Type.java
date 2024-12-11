package utez.edu.mx.environmentcommittee.modules.type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import utez.edu.mx.environmentcommittee.modules.event.Event;

import java.util.List;

@Entity
@Table(name = "type")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "type")
    @JsonIgnore
    private List<Event> events;

    public Type() {
    }

    public Type(String name) {
        this.name = name;
    }

    public Type(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Type(String name, List<Event> events) {
        this.name = name;
        this.events = events;
    }

    public Type(long id, String name, List<Event> events) {
        this.id = id;
        this.name = name;
        this.events = events;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
