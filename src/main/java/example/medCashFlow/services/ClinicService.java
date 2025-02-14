package example.medCashFlow.services;

import example.medCashFlow.dto.auth.RegisterDTO;
import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.exceptions.ClinicNotFoundException;
import example.medCashFlow.exceptions.InvalidClinicException;
import example.medCashFlow.mappers.ClinicMapper;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository repository;

    private final ClinicMapper mapper;
    private final EmployeeService employeeService;

    public Clinic getClinicById(UUID id) {
        return repository.findById(id).orElseThrow(ClinicNotFoundException::new);
    }

    public List<ClinicResponseDTO> getAllClinics() {
        return repository.findAllByOrderByCreatedAtAsc().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public void createClinic(RegisterDTO data) {
        ClinicRegisterDTO clinicData = data.clinic();

        if (!isClinicValid(clinicData)) {
            throw new InvalidClinicException();
        }


        Clinic savedClinc = repository.save(mapper.toClinic(clinicData));
        employeeService.createEmployee(data.manager(), savedClinc);
    }

    public void activateClinic(UUID id) {
        Clinic clinic = getClinicById(id);

        clinic.setActive(true);
        repository.save(clinic);
    }

    public void deactivateClinic(UUID id) {
        Clinic clinic = getClinicById(id);

        if (!clinic.isActive()) {
            throw new ClinicNotFoundException("Clínica já estava inativa");
        }

        clinic.setActive(false);
        repository.save(clinic);
    }

    public boolean isClinicValid(ClinicRegisterDTO clinicData) {
        return isClinicValidByName(clinicData.name()) && isClinicValidByCnpj(clinicData.cnpj()) && isClinicValidByPhone(clinicData.phone());
    }

    public boolean isClinicValidByName(String name) {
        if (repository.existsByName(name)) {
            throw new InvalidClinicException("clinic.name");
        }

        return true;
    }

    public boolean isClinicValidByCnpj(String cnpj) {
        if (repository.existsByCnpj(cnpj)) {
            throw new InvalidClinicException("clinic.cnpj");
        }

        return true;
    }

    public boolean isClinicValidByPhone(String phone) {
        if (repository.existsByPhone(phone)) {
            throw new InvalidClinicException("clinic.phone");
        }

        return true;
    }
}
