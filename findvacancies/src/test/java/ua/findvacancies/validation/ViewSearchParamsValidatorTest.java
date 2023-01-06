package ua.findvacancies.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ViewSearchParamsValidatorTest {

    private Validator searchParamsValidator;

    @BeforeEach
    public void beforeEach() {
        searchParamsValidator = new ViewSearchParamsValidator();
    }

    @Test
    public void supports() {
        assertTrue(searchParamsValidator.supports(ViewSearchParams.class));
        assertFalse(searchParamsValidator.supports(Object.class));
    }

    @Test
    public void SearchParamsIsValid() {
        Set sites = Stream.of("site1", "site2", "site3")
                .collect(Collectors.toCollection(HashSet::new));
        ViewSearchParams viewSearchParams = new ViewSearchParams("searchLine", "days", sites);

        BindException errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void SearchParamsWhenWrongSearchLine_thenIsNotValid() {
        ViewSearchParams viewSearchParams = new ViewSearchParams(null, "days", new HashSet<>());

        BindException errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getFieldErrorCount("searchLine"));
        assertEquals("searchLine.blank", errors.getFieldError("searchLine").getCode());
        assertEquals("Search field must not be empty.", errors.getFieldError("searchLine").getDefaultMessage());

        viewSearchParams.setSearchLine("");
        errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getFieldErrorCount("searchLine"));

        assertEquals("searchLine.blank", errors.getFieldError("searchLine").getCode());
        assertEquals("Search field must not be empty.", errors.getFieldError("searchLine").getDefaultMessage());
    }

    @Test
    public void SearchParamsWhenWrongDays_thenIsNotValid() {
        ViewSearchParams viewSearchParams = new ViewSearchParams("searchLine", null, new HashSet<>());

        BindException errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getFieldErrorCount("days"));
        assertEquals("days.blank", errors.getFieldError("days").getCode());
        assertEquals("Days amount must not be empty.", errors.getFieldError("days").getDefaultMessage());

        viewSearchParams.setDays("");
        errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getFieldErrorCount("days"));

        assertEquals("days.blank", errors.getFieldError("days").getCode());
        assertEquals("Days amount must not be empty.", errors.getFieldError("days").getDefaultMessage());
    }

    @Test
    public void SearchParamsWhenWrongSitesList_thenIsNotValid() {
        ViewSearchParams viewSearchParams = new ViewSearchParams("searchLine", "days", null);

        BindException errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getFieldErrorCount("sites"));
        assertEquals("sites.empty", errors.getFieldError("sites").getCode());
        assertEquals("Sites list must not be empty.", errors.getFieldError("sites").getDefaultMessage());


        viewSearchParams.setSites(new HashSet<>());
        errors = new BindException(viewSearchParams, "searchParams");
        ValidationUtils.invokeValidator(searchParamsValidator, viewSearchParams, errors);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getFieldErrorCount("sites"));

        assertEquals("sites.empty", errors.getFieldError("sites").getCode());
        assertEquals("Sites list must not be empty.", errors.getFieldError("sites").getDefaultMessage());
    }

}