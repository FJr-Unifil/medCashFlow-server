package example.medCashFlow.services;

import example.medCashFlow.model.Role;
import example.medCashFlow.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public Role getRoleById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean isRoleValid(Long roleId) {
        return repository.existsById(roleId);
    }
}
