package example.medCashFlow.model;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "clinics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Clinic(ClinicRegisterDTO clinicData) {
        this.name = clinicData.name();
        this.cnpj = clinicData.cnpj();
        this.phone = clinicData.phone();
    }
}
