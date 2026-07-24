package br.com.logicore.modules.department.validator;

import br.com.logicore.common.exception.DuplicateResourceException;
import br.com.logicore.modules.department.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class DepartmentValidatorTest {


    @Mock
    private DepartmentRepository repository;

    private DepartmentValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DepartmentValidator(repository);
    }


    @Test
    void shouldNotThrowExceptionWhenDepartmentNameIsAvailable() {

        when(repository.existsByNomeIgnoreCase("Technology"))
                .thenReturn(false);


        assertThatCode(() ->
                validator.validateUniqueName("Technology")
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldThrowExceptionWhenDepartmentNameAlreadyExists() {

        when(repository.existsByNomeIgnoreCase("Technology"))
                .thenReturn(true);


        assertThatThrownBy(() ->
                validator.validateUniqueName("Technology")
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("A department with this name is already registered.");
    }


    @Test
    void shouldNotThrowExceptionWhenUpdatingWithAvailableName() {

        when(repository.existsByNomeIgnoreCaseAndIdNot("Technology", 1L))
                .thenReturn(false);


        assertThatCode(() ->
                validator.validateUniqueNameForUpdate("Technology", 1L)
        ).doesNotThrowAnyException();
    }


    @Test
    void shouldThrowExceptionWhenAnotherDepartmentUsesNameOnUpdate() {

        when(repository.existsByNomeIgnoreCaseAndIdNot("Technology", 1L))
                .thenReturn(true);


        assertThatThrownBy(() ->
                validator.validateUniqueNameForUpdate("Technology", 1L)
        )
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Another department is already using this name.");
    }
}