package br.com.logicore.modules.address.validator;

import br.com.logicore.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AddressValidator {

    public void validateLatitude(BigDecimal latitude) {

        if (latitude == null) {
            return;
        }

        if (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {

            throw new BusinessException("Latitude must be between -90 and 90.");
        }

    }

    public void validateLongitude(BigDecimal longitude) {

        if (longitude == null) {
            return;
        }

        if (longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {

            throw new BusinessException("Longitude must be between -180 and 180.");
        }

    }

    public void validateState(String estado) {

        if (estado == null || estado.length() != 2) {
            throw new BusinessException("State must contain exactly 2 characters.");
        }

    }

}