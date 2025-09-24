package ua.findvacancies.common.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.impl.WorkUaJobScraper;
import ua.findvacancies.common.service.JobFilterService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("WorkUaJobScraper Unit Tests")
class WorkUaJobScraperTest {

    private static final String MOCK_URL = "http://mock-work.ua/url";

    private final JobSearchCriteria criteria = JobSearchCriteria.builder()
            .keywords(Set.of("Java"))
            .ignoredWords(Set.of("Junior"))
            .build();

    private final JsoupDocumentFetcher mockDocumentFetcher = Mockito.mock(JsoupDocumentFetcher.class);

    private final JobFilterService mockJobFilterService = Mockito.mock(JobFilterService.class);

    private final WorkUaJobScraper workUaJobScraper = new WorkUaJobScraper(mockDocumentFetcher, mockJobFilterService);

    @Test
    @DisplayName("Should successfully get JobSite value")
    void shouldGetJobSiteValue() {
        assertEquals(JobSite.WORK_UA, workUaJobScraper.getJobSite());
    }

    @Test
    @DisplayName("Should successfully scrape vacancies from a valid HTML document")
    void shouldScrapeVacanciesSuccessfully() throws IOException {
        // Given
        String htmlContent = getHtmlContentString();

        Document doc = Jsoup.parse(htmlContent, MOCK_URL);
        when(mockDocumentFetcher.fetchDocument(anyString())).thenReturn(doc).thenReturn(Jsoup.parse("", MOCK_URL));
        when(mockJobFilterService.matches(any(JobVacancy.class), eq(criteria))).thenReturn(true);

        // When
        List<JobVacancy> vacancies = workUaJobScraper.scrapeJobs(criteria);

        // Then
        assertNotNull(vacancies);
        assertEquals(2, vacancies.size());

        JobVacancy vacancy1 = vacancies.getFirst();
        assertEquals("Junior Java, Algorithm Developer (Math Guru)", vacancy1.getTitle());
        assertEquals(LocalDate.of(2025, 9, 9), vacancy1.getDatePosted());
        assertEquals("Logivations", vacancy1.getCompanyName());
        assertEquals("Львів", vacancy1.getCity());
        assertEquals("N/A", vacancy1.getSalary());
        assertFalse(vacancy1.isHot());
        assertTrue(vacancy1.getDescription().startsWith("Повна зайнятість. Також готові взяти студента, "));
        assertEquals("https://www.work.ua/jobs/4182127/", vacancy1.getUrl());
        assertEquals(JobSite.WORK_UA, vacancy1.getJobSite());

        verify(mockDocumentFetcher, atLeastOnce()).fetchDocument(anyString());
        verify(mockJobFilterService, times(2)).matches(any(JobVacancy.class), eq(criteria));
    }

    @Test
    @DisplayName("Should return empty list if no vacancies are found")
    void shouldReturnEmptyListIfNoVacancies() throws IOException {
        // Given
        String htmlContent = "<html><body></body></html>";
        Document doc = Jsoup.parse(htmlContent, MOCK_URL);

        when(mockDocumentFetcher.fetchDocument(anyString())).thenReturn(doc);

        // When
        List<JobVacancy> vacancies = workUaJobScraper.scrapeJobs(criteria);

        // Then
        assertNotNull(vacancies);
        assertTrue(vacancies.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list if fetching the document fails")
    void shouldReturnEmptyListIfDocumentFetchFails() throws IOException {
        // Given
        when(mockDocumentFetcher.fetchDocument(anyString())).thenThrow(new IOException("Simulated network error"));

        // When
        List<JobVacancy> vacancies = workUaJobScraper.scrapeJobs(criteria);

        // Then
        assertTrue(vacancies.isEmpty());
    }

    @Test
    @DisplayName("Should correctly filter vacancies based on JobFilterService")
    void shouldFilterVacancies() throws IOException {
        // Given
        String htmlContent = getHtmlContentString();
        Document doc = Jsoup.parse(htmlContent, MOCK_URL);
        when(mockDocumentFetcher.fetchDocument(anyString())).thenReturn(doc).thenReturn(Jsoup.parse("", MOCK_URL));

        // Mock the filter service to reject the first vacancy and accept the second
        when(mockJobFilterService.matches(any(JobVacancy.class), eq(criteria)))
                .thenReturn(false) // First one is rejected
                .thenReturn(true);  // Second one is accepted

        // When
        List<JobVacancy> vacancies = workUaJobScraper.scrapeJobs(criteria);

        // Then
        assertNotNull(vacancies);
        assertEquals(1, vacancies.size());
        assertEquals("Java-розробник (Middle)", vacancies.getFirst().getTitle());
    }

    private String getHtmlContentString() {
        return """
                <p><a name="4182127"></a></p>
                <div class="card card-hover card-visited wordwrap job-link mt-sm sm:mt-lg" tabindex="0">
                   <div class="mb-lg ">
                      <img class="preview-img preview-img-logo" src="https://i.work.ua/employer_design/7/4/8/158748_company_logo_2.png" alt="Logivations" width="100" height="50" />
                      <h2 class="my-0"><a tabindex="-1" title="Junior Java, Algorithm Developer (Math Guru), вакансія від 9 вересня 2025" href="/jobs/4182127/">Junior Java, Algorithm Developer (Math Guru)</a></h2>
                   </div>
                   <div class="mt-xs"><span class="mr-xs"> <span class="strong-600">Logivations</span></span><span class="">Львів, </span><span class="distance-block unclickable-inner-block text-blue-link"> <a class="bp-more nowrap" href="/jobs-java+developer/" data-config-city="44" data-analytics-jobseeker-close-modal="js-analytics-jobseeker-close-modal">2,8&nbsp;км&nbsp;від&nbsp;центру</a> </span></div>
                   <p class="ellipsis ellipsis-line ellipsis-line-3 text-default-7 mb-0">Повна зайнятість. Також готові взяти студента, людину з інвалідністю, пенсіонера. Досвід роботи від 1 року. Вища освіта. We work with Adidas, Coca Cola, DHL, Heineken, Lidl and 30.000 other users worldwide. Our solutions are fully cloud based and use the latest IT technologies like machine learning and robotics. We are looking for&hellip;</p>
                   <div id="not-logged-in_4182127" class="alert alert-warning mt-lg mb-0 hidden">
                      <p>Щоб зберегти вакансію, треба <a href="/jobseeker/login/">увійти</a> або <a href="/jobseeker/login/">зареєструватися</a>.</p>
                   </div>
                   <div class="flex flex-align-center flex-wrap">
                      <time class="text-default-7 mt-lg" datetime="2025-09-09 07:59:03">6&nbsp;год.&nbsp;тому</time>
                      <div class="flex flex-align-center ml-auto mt-lg">
                         <div class="saved-jobs" data-id="4182127">
                            <div class="saved-jobs-block">
                               <div class="inline-flex flex-align-center"><button class="btn btn-icon btn-tertiary"></button> <span class="saved-vacancy hidden-xs ml-xs">У <a class="saved-jobs-lnk" href="/jobseeker/my/saved-jobs/" target="_blank">збережених</a></span></div>
                            </div>
                            <div class="no-saved-jobs-block"><a class="btn btn-tertiary btn-icon btn-icon-left" href="/jobseeker/my/saved-jobs/?action=add&amp;id=4182127" rel="nofollow"> <span class="no-saved-vacancy hidden-xs">Зберегти</span> </a></div>
                         </div>
                      </div>
                   </div>
                </div>
                <p><a name="7109326"></a></p>
                <div class="card card-hover card-visited wordwrap job-link mt-sm sm:mt-lg" tabindex="0">
                   <div class="mb-lg mt-lg sm:mt-xl">
                      <h2 class="my-0"><a tabindex="-1" title="Java-розробник (Middle), вакансія від 8 вересня 2025" href="/jobs/7109326/">Java-розробник (Middle)</a></h2>
                   </div>
                   <div><span class="strong-600">120 000&thinsp;&ndash;&thinsp;165 000&nbsp;грн</span></div>
                   <div class="mt-xs"><span class="mr-xs"> <span class="strong-600">Дика Л.В., ФОП</span><span class="label-text-gray">, Агенція</span></span><span class="">Київ</span></div>
                   <p class="ellipsis ellipsis-line ellipsis-line-3 text-default-7 mb-0">Повна зайнятість. Досвід роботи від 2 років. Наша компанія &mdash; IT-постачальник SaaS-рішень для туристичних агентств. Ми пропонуємо агрегатор NDC та revenue management, що дозволяє агентствам оптимізувати продаж квитків і збільшувати прибуток з кожної транзакції&hellip;</p>
                   <div id="not-logged-in_7109326" class="alert alert-warning mt-lg mb-0 hidden">
                      <p>Щоб зберегти вакансію, треба <a href="/jobseeker/login/">увійти</a> або <a href="/jobseeker/login/">зареєструватися</a>.</p>
                   </div>
                   <div class="flex flex-align-center flex-wrap">
                      <time class="text-default-7 mt-lg" datetime="2025-09-08 11:25:48">вчора</time>
                      <div class="flex flex-align-center ml-auto mt-lg">
                         <div class="saved-jobs" data-id="7109326">
                            <div class="saved-jobs-block">
                               <div class="inline-flex flex-align-center"><button class="btn btn-icon btn-tertiary"></button> <span class="saved-vacancy hidden-xs ml-xs">У <a class="saved-jobs-lnk" href="/jobseeker/my/saved-jobs/" target="_blank">збережених</a></span></div>
                            </div>
                            <div class="no-saved-jobs-block"><a class="btn btn-tertiary btn-icon btn-icon-left" href="/jobseeker/my/saved-jobs/?action=add&amp;id=7109326" rel="nofollow"> <span class="no-saved-vacancy hidden-xs">Зберегти</span> </a></div>
                         </div>
                      </div>
                   </div>
                </div>
                """;
    }

}