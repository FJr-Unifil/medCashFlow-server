package example.medCashFlow.services;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.exceptions.AccountPlanningNotFoundException;
import example.medCashFlow.exceptions.ForbiddenException;
import example.medCashFlow.mappers.AccountPlanningMapper;
import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.repository.AccountPlanningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountPlanningService {

    private final AccountPlanningRepository repository;
    
    private final AccountPlanningMapper mapper;

    public AccountPlanning getAccountPlanningById(Long id) {
        return repository.findById(id).orElseThrow(AccountPlanningNotFoundException::new);
    }

    public AccountPlanning getAccountPlanningByIdAndValidateClinic(Long id, UUID clinicId) {
        AccountPlanning accountPlanning = getAccountPlanningById(id);
        if (!accountPlanning.getClinic().getId().equals(clinicId)) {
            throw new ForbiddenException("Você não tem permissão para acessar este plano de contas");
        }
        return accountPlanning;
    }

    public AccountPlanningResponseDTO getAccountPlanningResponseDTOById(Long id, UUID clinicId) {
        AccountPlanning accountPlanning = getAccountPlanningByIdAndValidateClinic(id, clinicId);
        return mapper.toResponseDTO(accountPlanning);
    }

    public AccountPlanningResponseDTO createAccountPlanning(AccountPlanningRegisterDTO data, Clinic clinic) {
        AccountPlanning accountPlanning = mapper.toAccountPlanning(data, clinic);
        repository.save(accountPlanning);
        return mapper.toResponseDTO(accountPlanning);
    }

    public AccountPlanningResponseDTO updateAccountPlanning(AccountPlanningRegisterDTO data, Clinic clinic, Long id) {
        AccountPlanning existingAccountPlanning = getAccountPlanningByIdAndValidateClinic(id, clinic.getId());

        mapper.updateAccountPlanning(existingAccountPlanning, data);

        repository.save(existingAccountPlanning);
        return mapper.toResponseDTO(existingAccountPlanning);
    }

    public void deleteAccountPlanning(Long id, UUID clinicId) {
        AccountPlanning accountPlanning = getAccountPlanningByIdAndValidateClinic(id, clinicId);
        repository.delete(accountPlanning);
    }

    public List<AccountPlanningResponseDTO> getAllAccountPlanningsByClinicId(UUID clinicId) {
        return repository.findByClinicIdOrderById(clinicId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}
