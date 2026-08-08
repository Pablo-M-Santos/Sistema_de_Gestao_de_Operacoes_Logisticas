package br.com.logicore.modules.cargo.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.cargo.repository.CargoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class CargoValidatorTest {

    @Mock
    private CargoRepository repository;

    private CargoValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CargoValidator(repository);
    }

    @Test
    void shouldNotThrowExceptionWhenCargoNameIsAvailable() {
        when(repository.existsByNomeIgnoreCase("Analyst")).thenReturn(false);

        assertThatCode(() -> validator.validateUniqueName("Analyst"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenCargoNameAlreadyExists() {
        when(repository.existsByNomeIgnoreCase("Analyst")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueName("Analyst"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("There is already a position with this name.");
    }

    @Test
    void shouldNotThrowExceptionWhenCargoCodeIsAvailable() {
        when(repository.existsByCodigoIgnoreCase("ANL")).thenReturn(false);

        assertThatCode(() -> validator.validateUniqueCode("ANL"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenCargoCodeAlreadyExists() {
        when(repository.existsByCodigoIgnoreCase("ANL")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUniqueCode("ANL"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("There is already a position with this code.");
    }

    @Test
    void shouldNotThrowExceptionWhenUpdatingWithAvailableName() {
        when(repository.findByNomeIgnoreCase("Analyst")).thenReturn(java.util.Optional.empty());

        assertThatCode(() -> validator.validateUniqueNameForUpdate("Analyst", 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenAnotherCargoUsesNameOnUpdate() {
        br.com.logicore.modules.cargo.entity.Cargo cargo = br.com.logicore.modules.cargo.entity.Cargo.builder()
                .id(2L)
                .nome("Analyst")
                .build();

        when(repository.findByNomeIgnoreCase("Analyst")).thenReturn(java.util.Optional.of(cargo));

        assertThatThrownBy(() -> validator.validateUniqueNameForUpdate("Analyst", 1L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("There is already a position with this name.");
    }

    @Test
    void shouldNotThrowExceptionWhenUpdatingWithAvailableCode() {
        when(repository.findByCodigoIgnoreCase("ANL")).thenReturn(java.util.Optional.empty());

        assertThatCode(() -> validator.validateUniqueCodeForUpdate("ANL", 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenAnotherCargoUsesCodeOnUpdate() {
        br.com.logicore.modules.cargo.entity.Cargo cargo = br.com.logicore.modules.cargo.entity.Cargo.builder()
                .id(2L)
                .codigo("ANL")
                .build();

        when(repository.findByCodigoIgnoreCase("ANL")).thenReturn(java.util.Optional.of(cargo));

        assertThatThrownBy(() -> validator.validateUniqueCodeForUpdate("ANL", 1L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("There is already a position with this code.");
    }
}

