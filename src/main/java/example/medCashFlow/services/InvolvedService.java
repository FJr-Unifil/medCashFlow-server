package example.medCashFlow.services;

import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.exceptions.InvalidInvolvedException;
import example.medCashFlow.exceptions.InvolvedNotFoundException;
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

    public Involved getInvolvedById(Long id) {
        return repository.findById(id).orElseThrow(InvolvedNotFoundException::new);
    }

    public List<InvolvedResponseDTO> getAllInvolvedsByClinicId(UUID clinicId) {
        return repository.findAllByClinicIdOrderById(clinicId).stream()
                .map(involved -> new InvolvedResponseDTO(
                        involved.getId(),
                        involved.getName(),
                        involved.getDocument(),
                        involved.getPhone(),
                        involved.getEmail(),
                        involved.isActive()
                )).toList();
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

    public InvolvedResponseDTO saveInvolved(Involved involved) {
        if (!isInvolvedValid(involved.getDocument(), involved.getEmail())) {
            throw new InvalidInvolvedException();
        }

        repository.save(involved);
        return new InvolvedResponseDTO(
                involved.getId(),
                involved.getName(),
                involved.getDocument(),
                involved.getPhone(),
                involved.getEmail(),
                involved.isActive()
        );
    }

    public InvolvedResponseDTO updateInvolved(Involved involved, Long id) {
        Involved existingInvolved = getInvolvedById(id);

        if (!involved.getEmail().equals(existingInvolved.getEmail())
                && repository.existsByEmail(involved.getEmail())) {
            throw new InvalidInvolvedException("involved.email");
        }

        if (!involved.getDocument().equals(existingInvolved.getDocument())
                && repository.existsByDocument(involved.getDocument())) {
            throw new InvalidInvolvedException("involved.document");
        }

        existingInvolved.setName(involved.getName());
        existingInvolved.setDocument(involved.getDocument());
        existingInvolved.setPhone(involved.getPhone());
        existingInvolved.setEmail(involved.getEmail());

        repository.save(existingInvolved);

        return new InvolvedResponseDTO(
                existingInvolved.getId(),
                existingInvolved.getName(),
                existingInvolved.getDocument(),
                existingInvolved.getPhone(),
                existingInvolved.getEmail(),
                existingInvolved.isActive()
        );
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

