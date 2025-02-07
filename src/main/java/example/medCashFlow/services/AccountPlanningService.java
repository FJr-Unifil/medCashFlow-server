package example.medCashFlow.services;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.exceptions.AccountPlanningNotFoundException;
import example.medCashFlow.mappers.AccountPlanningMapper;
import example.medCashFlow.model.AccountPlanning;
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

    public AccountPlanning toAccountPlanning(AccountPlanningRegisterDTO data) {
        return mapper.toAccountPlanning(data);
    }

    public AccountPlanning getAccountPlanningById(Long id) {
        return repository.findById(id).orElseThrow(AccountPlanningNotFoundException::new);
    }

    public AccountPlanningResponseDTO getAccountPlanningByIdDTO(Long id) {
        return toResponseDTO(getAccountPlanningById(id));
    }

    public AccountPlanningResponseDTO saveAccountPlanning(AccountPlanning accountPlanning) {
        repository.save(accountPlanning);
        return toResponseDTO(accountPlanning);
    }

    public AccountPlanningResponseDTO updateAccountPlanning(AccountPlanning accountPlanning) {
        if (getAccountPlanningById(accountPlanning.getId()) == null) {
            throw new AccountPlanningNotFoundException();
        }

        repository.save(accountPlanning);
        return toResponseDTO(accountPlanning);
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
