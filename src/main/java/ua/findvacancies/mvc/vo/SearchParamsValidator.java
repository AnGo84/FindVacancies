package ua.findvacancies.mvc.vo;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by AnGo on 24.07.2017.
 */
@Component
public class SearchParamsValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return SearchParams.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchParams searchParams = (SearchParams) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "searchLine", "searchLine.empty", "Search field must not be empty.");
        if (searchParams.getSearchLine() != null && searchParams.getSearchLine().length() == 0) {
            errors.rejectValue("searchLine", "searchLine.tooShort", "Search field must not be empty.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "days", "days.empty", "Days amount must not be empty.");
        if (searchParams.getDays() != null && searchParams.getDays().length() == 0) {
            errors.rejectValue("days", "days.tooShort", "Days amount must not be empty.");
        }

    }
}
