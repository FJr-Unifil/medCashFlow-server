package example.medCashFlow.services;

import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.exceptions.AccountPlanningNotFoundException;
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

    public AccountPlanning getAccountPlanningById(Long id) {
        return repository.findById(id).orElseThrow(AccountPlanningNotFoundException::new);
    }

    public AccountPlanningResponseDTO saveAccountPlanning(AccountPlanning accountPlanning) {
        repository.save(accountPlanning);
        return toDTO(accountPlanning);
    }

    public AccountPlanningResponseDTO updateAccountPlanning(AccountPlanning newData, Long id) {
        AccountPlanning accountPlanning = getAccountPlanningById(id);

        accountPlanning.setName(newData.getName());
        accountPlanning.setDescription(newData.getDescription());
        accountPlanning.setEmoji(newData.getEmoji());
        accountPlanning.setColor(newData.getColor());

        repository.save(accountPlanning);
        return toDTO(accountPlanning);
    }

    public void deleteAccountPlanning(Long id) {
        AccountPlanning accountPlanning = getAccountPlanningById(id);
        repository.delete(accountPlanning);
    }

    public List<AccountPlanningResponseDTO> getAllAccountPlanningsByClinicId(UUID clinicId) {
        return repository.findByClinicIdOrderById(clinicId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public AccountPlanningResponseDTO toDTO(AccountPlanning accountPlanning) {
        return new AccountPlanningResponseDTO(
                accountPlanning.getId(),
                accountPlanning.getName(),
                accountPlanning.getDescription(),
                accountPlanning.getEmoji(),
                accountPlanning.getColor(),
                accountPlanning.getClinic().getId()
        );
    }
}
