package utez.edu.mx.environmentcommittee.modules.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    // BRING ALL GROUPS
    @Transactional(readOnly = true)
    public ResponseEntity<?> findAll() {
        List<Group> groups = groupRepository.findAllWithAdmin();
        if (groups.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron grupos", "OK", 200, null);
        }
        return customResponseEntity.getOkResponse("Grupos encontrados", "OK", 200, groups);
    }

    // BRING GROUP BY ID
    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(long id) {
        Group group = groupRepository.findByIdWithAdmin(id);
        if (group == null) {
            return customResponseEntity.get404Response();
        }
        return customResponseEntity.getOkResponse("Grupo encontrado", "OK", 200, group);
    }

    // FIND GROUPS BY MUNICIPALITY
    @Transactional(readOnly = true)
    public ResponseEntity<?> findByMunicipality(String municipality) {
        List<Group> groups = groupRepository.findByMunicipality(municipality);
        if (groups.isEmpty()) {
            return customResponseEntity.getOkResponse("No se encontraron grupos con el municipio dado", "OK", 200, null);
        }
        return customResponseEntity.getOkResponse("Grupos encontrados", "OK", 200, groups);
    }

    // SAVE GROUP
    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> save(Group group) {
        try {
            groupRepository.save(group);
            return customResponseEntity.getOkResponse("Grupo registrado correctamente", "CREATED", 201, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response("Error al registrar el grupo: " + e.getMessage());
        }
    }

    // UPDATE GROUP
    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> update(Group group) {
        Optional<Group> existingGroup = groupRepository.findById(group.getId());
        if (!existingGroup.isPresent()) {
            return customResponseEntity.get404Response(); // Grupo no encontrado
        }

        try {
            // Actualizar solo los campos permitidos
            Group groupToUpdate = existingGroup.get();
            groupToUpdate.setName(group.getName());
            groupToUpdate.setMunicipality(group.getMunicipality());
            groupToUpdate.setNeighborhood(group.getNeighborhood());

            // Nota: No actualizamos el campo "admin" aquí.

            groupRepository.save(groupToUpdate);
            return customResponseEntity.getOkResponse("Grupo actualizado con éxito", "OK", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response("Error al actualizar el grupo: " + e.getMessage());
        }
    }

    // DELETE GROUP
    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<?> deleteById(long id) {
        Optional<Optional<Group>> group = Optional.ofNullable(groupRepository.findById(id));
        if (!group.isPresent()) {
            return customResponseEntity.get404Response();
        }
        try {
            groupRepository.deleteById(id);
            return customResponseEntity.getOkResponse("Grupo eliminado correctamente", "OK", 200, null);
        } catch (Exception e) {
            e.printStackTrace();
            return customResponseEntity.get400Response("Error al eliminar el grupo: " + e.getMessage());
        }
    }
}
