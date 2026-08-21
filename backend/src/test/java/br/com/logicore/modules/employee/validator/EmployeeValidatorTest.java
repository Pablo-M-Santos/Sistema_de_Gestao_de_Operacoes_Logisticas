package br.com.logicore.modules.employee.validator;

import br.com.logicore.common.exception.BusinessException;
import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.employee.entity.Employee;
import br.com.logicore.modules.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeValidatorTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeValidator validator;

    private final Employee other = Employee.builder().id(2L).build();

    @BeforeEach
    void setUp() {
        validator = new EmployeeValidator(repository);
    }

    @Test
    void shouldNotThrowWhenCpfAvailable() {
        when(repository.existsByCpf("12345678901")).thenReturn(false);

        assertThatCode(() -> validator.validateUniqueCpf("12345678901"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenCpfExists() {
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueCpf("12345678901"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowWhenMatriculaAvailable() {
        when(repository.existsByMatricula("EMP001")).thenReturn(false);

        assertThatCode(() -> validator.validateUniqueMatricula("EMP001"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenMatriculaExists() {
        when(repository.existsByMatricula("EMP001")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueMatricula("EMP001"))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowWhenCpfAvailableForUpdate() {
        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());

        assertThatCode(() -> validator.validateUniqueCpfForUpdate("12345678901", 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenAnotherEmployeeUsesCpfOnUpdate() {
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> validator.validateUniqueCpfForUpdate("12345678901", 1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowWhenMatriculaAvailableForUpdate() {
        when(repository.findByMatricula("EMP001")).thenReturn(Optional.empty());

        assertThatCode(() -> validator.validateUniqueMatriculaForUpdate("EMP001", 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenAnotherEmployeeUsesMatriculaOnUpdate() {
        when(repository.findByMatricula("EMP001")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> validator.validateUniqueMatriculaForUpdate("EMP001", 1L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldNotThrowWhenStatusActive() {
        assertThatCode(() -> validator.validateStatus("ACTIVE"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowWhenStatusInactive() {
        assertThatCode(() -> validator.validateStatus("INACTIVE"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenStatusInvalid() {
        assertThatThrownBy(() -> validator.validateStatus("XYZ"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Status must be ACTIVE or INACTIVE.");
    }
}
