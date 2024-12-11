package utez.edu.mx.environmentcommittee.modules.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

public class EventDTO {
    private String title;

    private Date date;

    private Long typeId;

    private String status;


    public EventDTO() {
    }

    public EventDTO(String title, Date date, Long typeId, String status) {
        this.title = title;
        this.date = date;
        this.typeId = typeId;
        this.status = status;
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

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
