package example.medCashFlow.services;

import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository repository;

    public Clinic getClinicById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean isClinicValid(Long id) {
        return repository.existsByIdAndIsActiveTrue(id);
    }
}
