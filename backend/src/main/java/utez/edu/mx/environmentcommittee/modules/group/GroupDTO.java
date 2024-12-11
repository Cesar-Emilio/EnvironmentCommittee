package utez.edu.mx.environmentcommittee.modules.group;

import java.util.List;

public class GroupDTO {
    private String name;
    private String municipality;
    private String neighborhood;
    private Long adminId;

    public GroupDTO() {
    }

    public GroupDTO(String name, String municipality, String neighborhood, Long adminId) {
        this.name = name;
        this.municipality = municipality;
        this.neighborhood = neighborhood;
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
