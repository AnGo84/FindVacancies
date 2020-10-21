package ua.findvacancies.mvc.utils;

import org.springframework.util.CollectionUtils;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewSearchParamsUtils {

    public static Set<Provider> getProvidersSet(ViewSearchParams viewSearchParams) {
        if (viewSearchParams == null || CollectionUtils.isEmpty(viewSearchParams.getSites())) {
            return new HashSet<>();
        }
        Set<Provider> providers = viewSearchParams.getSites().stream()
                .filter(siteName -> Provider.contains(siteName))
                .map(siteName -> Provider.valueOf(siteName.toUpperCase()))
                .collect(Collectors.toSet());
        return providers;
    }
}
