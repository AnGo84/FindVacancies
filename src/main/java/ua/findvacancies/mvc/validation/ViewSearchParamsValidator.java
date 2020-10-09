package ua.findvacancies.mvc.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ua.findvacancies.mvc.viewdata.ViewSearchParams;

@Component
public class ViewSearchParamsValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ViewSearchParams.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ViewSearchParams viewSearchParams = (ViewSearchParams) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "searchLine", "searchLine.blank", "Search field must not be empty.");
        if (viewSearchParams.getSearchLine() != null && viewSearchParams.getSearchLine().length() == 0) {
            errors.rejectValue("searchLine", "searchLine.tooShort", "Search field must not be empty.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "days", "days.blank", "Days amount must not be empty.");
        if (viewSearchParams.getDays() != null && viewSearchParams.getDays().length() == 0) {
            errors.rejectValue("days", "days.tooShort", "Days amount must not be empty.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sites", "sites.empty", "Sites list must not be empty.");
        if (viewSearchParams.getSites() != null && viewSearchParams.getSites().isEmpty()) {
            errors.rejectValue("sites", "sites.empty", "Sites list must not be empty.");
        }

    }
}
