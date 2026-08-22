package br.com.logicore.modules.driver.entity;

import br.com.logicore.modules.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false, unique = true)
    private Employee funcionario;

    @Column(nullable = false, unique = true, length = 20)
    private String cnh;

    @Column(nullable = false, length = 5)
    private String categoria;

    @Column(name = "validade_cnh", nullable = false)
    private LocalDate validadeCnh;

    @Column(length = 500)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
