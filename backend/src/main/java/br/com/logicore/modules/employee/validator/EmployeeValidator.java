package br.com.logicore.modules.employee.validator;

import br.com.logicore.modules.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EmployeeValidator {


    private final EmployeeRepository repository;


    public EmployeeValidator(EmployeeRepository repository) {
        this.repository = repository;
    }

    public void validateUniqueCpf(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new RuntimeException(
                    "There is already an employee with this CPF."
            );

        }

    }


    public void validateUniqueMatricula(String matricula) {

        if (repository.existsByMatricula(matricula)) {
            throw new RuntimeException(
                    "There is already an employee with this registration number."
            );
        }

    }


    public void validateUniqueCpfForUpdate(
            String cpf,
            Long id
    ) {

        repository.findByCpf(cpf)
                .filter(employee ->
                        !employee.getId().equals(id)
                )

                .ifPresent(employee -> {
                    throw new RuntimeException(
                            "There is already an employee with this CPF."
                    );

                });

    }


    public void validateUniqueMatriculaForUpdate(
            String matricula,
            Long id
    ) {


        repository.findByMatricula(matricula)

                .filter(employee ->
                        !employee.getId().equals(id)
                )

                .ifPresent(employee -> {

                    throw new RuntimeException(
                            "There is already an employee with this registration number."
                    );

                });

    }


}