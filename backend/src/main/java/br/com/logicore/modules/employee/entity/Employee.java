package br.com.logicore.modules.employee.entity;


import br.com.logicore.modules.address.entity.Address;
import br.com.logicore.modules.cargo.entity.Cargo;
import br.com.logicore.modules.department.entity.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 20)
    private String matricula;


    @Column(nullable = false, length = 150)
    private String nome;


    @Column(nullable = false, unique = true, length = 11)
    private String cpf;


    @Column(length = 20)
    private String rg;


    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;


    @Column(length = 20)
    private String telefone;


    @Column(length = 150)
    private String email;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Department departamento;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    private Address endereco;


    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;


    @Column(length = 30)
    private String status;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;


    @PrePersist
    public void prePersist() {

        criadoEm = LocalDateTime.now();

        if(status == null){
            status = "ACTIVE";
        }
    }



}