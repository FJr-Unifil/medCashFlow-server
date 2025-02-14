package example.medCashFlow.services;

import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.exceptions.InvalidInvolvedException;
import example.medCashFlow.exceptions.InvolvedNotFoundException;
import example.medCashFlow.mappers.InvolvedMapper;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Involved;
import example.medCashFlow.repository.InvolvedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvolvedService {

    private final InvolvedRepository repository;

    private final InvolvedMapper mapper;

    public Involved getInvolvedById(Long id) {
        return repository.findById(id).orElseThrow(InvolvedNotFoundException::new);
    }

    public InvolvedResponseDTO getInvolvedResponseDTOById(Long id) {
        Involved involved = getInvolvedById(id);
        return mapper.toResponseDTO(involved);
    }

    public List<InvolvedResponseDTO> getAllInvolvedsByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(mapper::toResponseDTO).toList();
    }

    public boolean isInvolvedValid(String document, String email) {
        return (isInvolvedValidByDocument(document) && isInvolvedValidByEmail(email));
    }

    private boolean isInvolvedValidByDocument(String document) {
        if (repository.existsByDocument(document)) {
            throw new InvalidInvolvedException("involved.document");
        }
        return true;
    }

    private boolean isInvolvedValidByEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new InvalidInvolvedException("involved.email");
        }
        return true;
    }

    public InvolvedResponseDTO createInvolved(InvolvedRegisterDTO data, Clinic clinic) {
        if (!isInvolvedValid(data.document(), data.email())) {
            throw new InvalidInvolvedException();
        }

        Involved involved = mapper.toInvolved(data, clinic);

        repository.save(involved);
        return mapper.toResponseDTO(involved);
    }

    public InvolvedResponseDTO updateInvolved(InvolvedRegisterDTO data, Clinic clinic, Long id) {
        Involved existingInvolved = getInvolvedById(id);

        if (!data.email().equals(existingInvolved.getEmail())
                && repository.existsByEmail(data.email())) {
            throw new InvalidInvolvedException("involved.email");
        }

        if (!data.document().equals(existingInvolved.getDocument())
                && repository.existsByDocument(data.document())) {
            throw new InvalidInvolvedException("involved.document");
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

