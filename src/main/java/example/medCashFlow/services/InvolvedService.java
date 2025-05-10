package example.medCashFlow.services;

import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.exceptions.ApiValidationError;
import example.medCashFlow.exceptions.MultipleInvalidDataException;
import example.medCashFlow.exceptions.ResourceNotFoundException;
import example.medCashFlow.mappers.InvolvedMapper;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Involved;
import example.medCashFlow.repository.InvolvedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InvolvedService {

    private final InvolvedRepository repository;

    private final InvolvedMapper mapper;

    public Involved getInvolvedById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        Involved.class.getSimpleName(),
                        "id",
                        id.toString()
                )
        );
    }

    public InvolvedResponseDTO getInvolvedResponseDTOById(Long id) {
        Involved involved = getInvolvedById(id);
        return mapper.toResponseDTO(involved);
    }

    public List<InvolvedResponseDTO> getAllInvolvedsByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(mapper::toResponseDTO).toList();
    }

    public Map<String, String> getInvalidFields(InvolvedRegisterDTO data) {
        Map<String, String> invalidFields = new HashMap<>();

        if (repository.existsByDocument(data.document())) {
            invalidFields.put("document", data.document());
        }

        if (repository.existsByEmail(data.email())) {
            invalidFields.put("email", data.email());
        }

        if (repository.existsByPhone(data.phone())) {
            invalidFields.put("phone", data.phone());
        }

        return invalidFields;
    }

    public InvolvedResponseDTO createInvolved(InvolvedRegisterDTO data, Clinic clinic) {
        List<ApiValidationError> errors = new ArrayList<>();
        Map<String, String> invalidFields = getInvalidFields(data);

        if (!invalidFields.isEmpty()) {
            invalidFields.forEach((key, value) -> errors.add(new ApiValidationError(
                    Involved.class.getSimpleName(),
                    key,
                    value
            )));
            throw new MultipleInvalidDataException(errors);
        }

        Involved involved = mapper.toInvolved(data, clinic);

        repository.save(involved);
        return mapper.toResponseDTO(involved);
    }

    public InvolvedResponseDTO updateInvolved(InvolvedRegisterDTO data, Long id) {
        List<ApiValidationError> errors = new ArrayList<>();
        Involved existingInvolved = getInvolvedById(id);

        if (repository.existsByDocumentAndIdNot(data.document(), id)) {
            errors.add(new ApiValidationError(
                    Involved.class.getSimpleName(),
                    "document",
                    data.document()
            ));
        }

        if (repository.existsByEmailAndIdNot(data.email(), id)) {
            errors.add(new ApiValidationError(
                    Involved.class.getSimpleName(),
                    "email",
                    data.email()
            ));
        }

        if (repository.existsByPhoneAndIdNot(data.phone(), id)) {
            errors.add(new ApiValidationError(
                    Involved.class.getSimpleName(),
                    "phone",
                    data.phone()
            ));
        }

        if (!errors.isEmpty()) {
            throw new MultipleInvalidDataException(errors);
        }

        mapper.updateInvolved(existingInvolved, data);

        repository.save(existingInvolved);
        return mapper.toResponseDTO(existingInvolved);
    }

    public void deleteInvolved(Long id) {
        Involved involved = getInvolvedById(id);
        involved.setActive(false);
        repository.save(involved);
    }

    public void activateInvolved(Long id) {
        Involved involved = getInvolvedById(id);
        involved.setActive(true);
        repository.save(involved);
    }

}

