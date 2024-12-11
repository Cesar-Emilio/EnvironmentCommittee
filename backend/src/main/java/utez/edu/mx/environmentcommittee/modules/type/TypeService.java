package utez.edu.mx.environmentcommittee.modules.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.environmentcommittee.utils.CustomResponseEntity;

import java.util.List;

@Service
public class TypeService {
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private CustomResponseEntity customResponseEntity;

    // BRING ALL TYPES
    public ResponseEntity<?> findAll() {
        List<Type> types = typeRepository.findAll();
        return customResponseEntity.getOkResponse("Tipos de eventos encontrados", "OK", 200, types);
    }

    // BRING TYPE BY ID
    public ResponseEntity<?> findById(long id) {
        Type type = typeRepository.findById(id).orElse(null);
        return type == null
                ? customResponseEntity.get404Response()
                : customResponseEntity.getOkResponse("Tipo de evento encontrado", "OK", 200, type);
    }

    // SAVE TYPE
    public ResponseEntity<?> save(Type type) {
        typeRepository.save(type);
        return customResponseEntity.getOkResponse("Tipo de evento registrado correctamente", "CREATED", 201, null);
    }

    // UPDATE TYPE
    public ResponseEntity<?> update(Type type) {
        if (!typeRepository.existsById(type.getId())) {
            return customResponseEntity.get404Response();
        }
        typeRepository.save(type);
        return customResponseEntity.getOkResponse("Tipo de evento actualizado correctamente", "OK", 200, null);
    }

    // DELETE TYPE
    public ResponseEntity<?> deleteById(long id) {
        if (!typeRepository.existsById(id)) {
            return customResponseEntity.get404Response();
        }
        typeRepository.deleteById(id);
        return customResponseEntity.getOkResponse("Tipo de evento eliminado correctamente", "OK", 200, null);
    }
}
