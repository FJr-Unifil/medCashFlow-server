package example.medCashFlow.services;

import example.medCashFlow.exceptions.RoleNotFoundException;
import example.medCashFlow.model.Role;
import example.medCashFlow.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return repository.findById(id).orElseThrow(RoleNotFoundException::new);
    }

}
