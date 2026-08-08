package br.com.logicore.modules.address.validator;

import br.com.logicore.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressValidatorTest {

    private final AddressValidator validator = new AddressValidator();

    @Test
    void shouldNotThrowExceptionWhenStateIsValid() {
        assertThatCode(() -> validator.validateState("SP"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenStateIsInvalid() {
        assertThatThrownBy(() -> validator.validateState("S"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("State must contain exactly 2 characters.");
    }

    @Test
    void shouldNotThrowExceptionWhenLatitudeIsValid() {
        assertThatCode(() -> validator.validateLatitude(new BigDecimal("-23.55052000")))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsInvalid() {
        assertThatThrownBy(() -> validator.validateLatitude(new BigDecimal("-91")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Latitude must be between -90 and 90.");
    }

    @Test
    void shouldNotThrowExceptionWhenLongitudeIsValid() {
        assertThatCode(() -> validator.validateLongitude(new BigDecimal("-46.63330800")))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsInvalid() {
        assertThatThrownBy(() -> validator.validateLongitude(new BigDecimal("181")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Longitude must be between -180 and 180.");
    }
}

