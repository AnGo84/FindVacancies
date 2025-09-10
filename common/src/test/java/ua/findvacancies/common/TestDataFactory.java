package ua.findvacancies.common;

import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class TestDataFactory {

    private static final Random random = new Random();
    private static final JobSite[] JOB_SITES = JobSite.values();

    private TestDataFactory() {
    }

    public static JobVacancy buildJovVacancy(String title) {
        return buildJovVacancy(title, randomDJobSite(), LocalDate.now(), "Description for " + title);
    }

    public static JobVacancy buildJovVacancy(String title, LocalDate dataPosted) {
        return buildJovVacancy(title, randomDJobSite(), dataPosted, "Description for " + title);
    }

    public static JobVacancy buildJovVacancy(String title, LocalDate dataPosted, String description) {
        return buildJovVacancy(title, randomDJobSite(), dataPosted, description);
    }

    public static JobVacancy buildJovVacancy(String title,
                                             JobSite jobSite,
                                             LocalDate dataPosted,
                                             String description) {
        int randomNumberInRange = getRandomNumberInRange(1, 50) * 10000;

        return JobVacancy.builder()
                .title(title)
                .salary(randomNumberInRange + " UAH")
                .city("Kyiv")
                .companyName("TechCorp")
                .jobSite(jobSite)
                .url("http://example.com/java")
                .datePosted(dataPosted)
                .isHot(true)
                .description(description)
                .skills(List.of("Java", "Spring", "AWS"))
                .build();
    }

    public static JobSite randomDJobSite() {
        return JOB_SITES[random.nextInt(JOB_SITES.length)];
    }

    private static int getRandomNumberInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
