package example.medCashFlow.services;

import example.medCashFlow.exceptions.PaymentMethodNotFoundException;
import example.medCashFlow.model.PaymentMethod;
import example.medCashFlow.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    @Transactional(readOnly = true)
    public PaymentMethod getPaymentMethodById(Long id) {
        return repository.findById(id).orElseThrow(PaymentMethodNotFoundException::new);
    }
}
