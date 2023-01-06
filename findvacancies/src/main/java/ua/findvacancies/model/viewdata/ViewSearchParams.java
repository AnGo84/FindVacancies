package ua.findvacancies.model.viewdata;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewSearchParams {
    @NotBlank(message = "{viewSearchParams.SearchLine.Blank}")
    private String searchLine;

    @NotBlank(message = "{viewSearchParams.Days.Blank}")
    @Min(value = 0, message = "{viewSearchParams.Days.MinValue}")
    private String days;

    @NotEmpty(message = "{viewSearchParams.Sites.Blank}")
    private Set<String> sites;
}
