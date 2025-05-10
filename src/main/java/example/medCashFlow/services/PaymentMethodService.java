package example.medCashFlow.services;

import example.medCashFlow.exceptions.ResourceNotFoundException;
import example.medCashFlow.model.PaymentMethod;
import example.medCashFlow.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    public PaymentMethod getPaymentMethodById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        PaymentMethod.class.getSimpleName(),
                        "id",
                        id.toString()
                )
        );
    }
}
