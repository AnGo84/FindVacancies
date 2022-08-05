package ua.findvacancies.model.viewdata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
