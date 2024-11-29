package example.medCashFlow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installment {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    @Column(nullable = false, name = "installment_number")
    private Integer installmentNumber;

    @Column(nullable = false)
    private Double pricing;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

}
