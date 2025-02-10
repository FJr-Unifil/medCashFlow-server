package example.medCashFlow.services;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.exceptions.AccountPlanningNotFoundException;
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

    public AccountPlanningResponseDTO toResponseDTO(AccountPlanning accountPlanning) {
        return mapper.toResponseDTO(accountPlanning);
    }

    public AccountPlanning toAccountPlanning(AccountPlanningRegisterDTO data, Clinic clinic) {
        return mapper.toAccountPlanning(data, clinic);
    }

    public AccountPlanning getAccountPlanningById(Long id) {
        return repository.findById(id).orElseThrow(AccountPlanningNotFoundException::new);
    }

    public AccountPlanningResponseDTO saveAccountPlanning(AccountPlanning accountPlanning) {
        repository.save(accountPlanning);
        return toResponseDTO(accountPlanning);
    }

    public AccountPlanningResponseDTO updateAccountPlanning(AccountPlanning accountPlanning, Long id) {
        AccountPlanning existingAccountPlanning = getAccountPlanningById(id);

        existingAccountPlanning.setName(accountPlanning.getName());
        existingAccountPlanning.setDescription(accountPlanning.getDescription());
        existingAccountPlanning.setEmoji(accountPlanning.getEmoji());
        existingAccountPlanning.setColor(accountPlanning.getColor());

        repository.save(existingAccountPlanning);
        return toResponseDTO(existingAccountPlanning);
    }

    public void deleteAccountPlanning(Long id) {
        AccountPlanning accountPlanning = getAccountPlanningById(id);
        repository.delete(accountPlanning);
    }

    public List<AccountPlanningResponseDTO> getAllAccountPlanningsByClinicId(UUID clinicId) {
        return repository.findByClinicIdOrderById(clinicId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
