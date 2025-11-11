package ua.findvacancies.common.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JobSiteTest {

    @Test
    void testGetUrlName() {
        assertEquals("djinni.co", JobSite.DJINNI_CO.getSiteName());
        assertEquals("work.ua", JobSite.WORK_UA.getSiteName());
        assertEquals("jobs.dou.ua", JobSite.DOU_UA.getSiteName());
    }

    @Test
    void testFromUrlName_ValidNames() {
        assertEquals(Optional.of(JobSite.DJINNI_CO), JobSite.fromUrlName("djinni.co"));
        assertEquals(Optional.of(JobSite.WORK_UA), JobSite.fromUrlName("work.ua"));
        assertEquals(Optional.of(JobSite.DOU_UA), JobSite.fromUrlName("jobs.dou.ua"));
        // Test case-insensitivity
        assertEquals(Optional.of(JobSite.DJINNI_CO), JobSite.fromUrlName("DJINNI.CO"));
    }

    @Test
    void testFromUrlName_InvalidName() {
        assertFalse(JobSite.fromUrlName("invalid.com").isPresent());
        assertFalse(JobSite.fromUrlName(null).isPresent());
        assertFalse(JobSite.fromUrlName("").isPresent());
    }

    @Test
    void testJoinSiteNames_withMultipleSites() {
        String result = JobSite.joinSiteNames(Set.of(JobSite.WORK_UA, JobSite.DJINNI_CO));

        assertEquals("djinni.co, work.ua", result);
    }

    @Test
    void testJoinSiteNames_withSingleSite() {
        assertEquals("work.ua", JobSite.joinSiteNames(Set.of(JobSite.WORK_UA)));
    }

    @Test
    void testJoinSiteNames_withEmptySet() {
        assertEquals("", JobSite.joinSiteNames(Set.of()));
    }

    @Test
    void testJoinSiteNames_withNullSet() {
        assertEquals("", JobSite.joinSiteNames(null));
    }

}
