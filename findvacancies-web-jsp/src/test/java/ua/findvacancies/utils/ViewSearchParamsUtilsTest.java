package ua.findvacancies.utils;

import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.strategy.Strategy;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class ViewSearchParamsUtilsTest {

    @Test
    public void whenGetProvidersSet() {
        assertNotNull(ViewSearchParamsUtils.getProvidersSet(null));
        assertNotNull(ViewSearchParamsUtils.getProvidersSet(new ViewSearchParams()));

        ViewSearchParams viewSearchParams = TestUtils.getViewSearchParams();
        Set<Provider> providers = ViewSearchParamsUtils.getProvidersSet(viewSearchParams);
        assertNotNull(providers);
        assertTrue(providers.isEmpty());

        Set<String> sites = Stream.of("workua", "dou", "rabotaua")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        providers = ViewSearchParamsUtils.getProvidersSet(viewSearchParams);
        assertNotNull(providers);
        assertFalse(providers.isEmpty());
        assertEquals(3, providers.size());
        assertTrue(providers.contains(Provider.DOU));
        assertTrue(providers.contains(Provider.RABOTAUA));
        assertTrue(providers.contains(Provider.WORKUA));
        assertFalse(providers.contains(Provider.HEADHUNTER));

        sites = Stream.of("wrong", "dou", "wrong2")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        providers = ViewSearchParamsUtils.getProvidersSet(viewSearchParams);
        assertNotNull(providers);
        assertFalse(providers.isEmpty());
        assertEquals(1, providers.size());
        assertTrue(providers.contains(Provider.DOU));
        assertFalse(providers.contains(Provider.RABOTAUA));
        assertFalse(providers.contains(Provider.WORKUA));
        assertFalse(providers.contains(Provider.HEADHUNTER));

    }

    @Test
    public void whenGetStrategiesSet() {
        assertNotNull(ViewSearchParamsUtils.getStrategiesSet(null));
        assertNotNull(ViewSearchParamsUtils.getStrategiesSet(new ViewSearchParams()));

        ViewSearchParams viewSearchParams = TestUtils.getViewSearchParams();
        Set<Strategy> strategies = ViewSearchParamsUtils.getStrategiesSet(viewSearchParams);
        assertNotNull(strategies);
        assertTrue(strategies.isEmpty());

        Set<String> sites = Stream.of("workua", "dou", "rabotaua")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        strategies = ViewSearchParamsUtils.getStrategiesSet(viewSearchParams);
        assertNotNull(strategies);
        assertFalse(strategies.isEmpty());
        assertEquals(3, strategies.size());

        sites = Stream.of("wrong", "dou", "wrong2")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        strategies = ViewSearchParamsUtils.getStrategiesSet(viewSearchParams);
        assertNotNull(strategies);
        assertFalse(strategies.isEmpty());
        assertEquals(1, strategies.size());
    }

}