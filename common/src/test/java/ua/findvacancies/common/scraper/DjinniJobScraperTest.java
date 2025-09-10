package ua.findvacancies.common.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.impl.DjinniJobScraper;
import ua.findvacancies.common.service.JobFilterService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("DjinniJobScraper Unit Tests")
class DjinniJobScraperTest {

    private static final String MOCK_URL = "http://mock-djinni-url.com";

    private final JobSearchCriteria criteria = JobSearchCriteria.builder()
            .keywords(Set.of("Java"))
            .ignoredWords(Set.of("Junior"))
            .build();


    private final JsoupDocumentFetcher mockDocumentFetcher = Mockito.mock(JsoupDocumentFetcher.class);

    private final JobFilterService mockJobFilterService = Mockito.mock(JobFilterService.class);

    private final DjinniJobScraper djinniJobScraper =  new DjinniJobScraper(mockDocumentFetcher, mockJobFilterService);

    @Test
    @DisplayName("Should successfully get JobSite value")
    void shouldGetJobSiteValue() {
        assertEquals(JobSite.DJINNI_CO, djinniJobScraper.getJobSite());
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
        List<JobVacancy> vacancies = djinniJobScraper.scrapeJobs(criteria);

        // Then
        assertNotNull(vacancies);
        assertEquals(2, vacancies.size());

        JobVacancy vacancy1 = vacancies.getFirst();
        assertEquals("Middle Full Stack Developer (React+Java)", vacancy1.getTitle());
        assertEquals(LocalDate.of(2025, 9, 8), vacancy1.getDatePosted());
        assertEquals("Sigma Software", vacancy1.getCompanyName());
        assertEquals("Країни Європи та Україна", vacancy1.getCity());
        assertEquals("N/A", vacancy1.getSalary());
        assertFalse(vacancy1.isHot());
        assertTrue(vacancy1.getDescription().startsWith("Customer It is an international technology company"));
        assertEquals("https://djinni.co/jobs/757365-middle-full-stack-developer-react-java/", vacancy1.getUrl());
        assertEquals(JobSite.DJINNI_CO, vacancy1.getJobSite());

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
        List<JobVacancy> vacancies = djinniJobScraper.scrapeJobs(criteria);

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
        List<JobVacancy> vacancies = djinniJobScraper.scrapeJobs(criteria);

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
        List<JobVacancy> vacancies = djinniJobScraper.scrapeJobs(criteria);

        // Then
        assertNotNull(vacancies);
        assertEquals(1, vacancies.size());
        assertEquals("Senior/Lead Full Stack Developer (Java / Vue)", vacancies.getFirst().getTitle());
    }

    private String getHtmlContentString() {
        return """
                <ul class="list-unstyled list-jobs mb-4">
                   <li id="job-item-757365" class="mb-4">
                      <div class="d-flex flex-wrap align-items-center gap-1 fs-5 mb-2 text-secondary">
                         <div class="d-inline-flex align-items-center gap-1">
                            <div id="" class="userpic-wrapper userpic-color_5 userpic_xs userpic-transparent is-company-logo userpic--logo" style="--userpic-bg: var(--userpic-color-5);" data-initials="S"><img class="userpic-image userpic-image_img" src="https://p.djinni.co/98/7ae1100b9e8b3e1882b7022a1ddb4a/sigma_logo_400.png" alt="" /></div>
                            <a class="text-body js-analytics-event" href="/jobs/company-sigma-software/" data-analytics="company_page" data-json-parameter="{ &quot;action&quot;: &quot;jobs_list_click&quot;, &quot;company_id&quot;: 42 }"> Sigma Software </a>
                         </div>
                         <div class="d-flex align-items-center gap-1"><span class="middot"> &middot; </span> <span class="text-nowrap"> 8 переглядів </span> <span class="middot"> &middot; </span> <span class="text-nowrap"> 4 відгуки </span> <span class="middot"> &middot; </span> <span class="text-nowrap" title="15:41 08.09.2025" data-toggle="tooltip"> 1г </span></div>
                      </div>
                      <h2 class="fs-3 mb-2"><a class="job-item__title-link" href="/jobs/757365-middle-full-stack-developer-react-java/"> Middle Full Stack Developer (React+Java) </a></h2>
                      <div class="fw-medium d-flex flex-wrap align-items-center gap-1"><span class="text-nowrap">Тільки віддалено</span> <span class="middot"> &middot; </span> <span class="text-nowrap"> <span class="location-text">Країни Європи та Україна</span> </span> <span class="middot"> &middot; </span> <span class="text-nowrap">4 роки досвіду</span> <span class="middot"> &middot; </span> <span class="text-nowrap">B2 &ndash; Вище середнього</span></div>
                      <div>
                         <span class="js-truncated-text">Customer It is an international technology company that specializes in developing high-load platforms for data processing and analytics. Its core product helps businesses manage large volumes of data, build models, and gain actionable insights. The... </span>
                         <p><strong>Customer</strong></p>
                         <p>It&nbsp;is&nbsp;an&nbsp;international technology company that specializes in&nbsp;developing high-load platforms for data processing and analytics. Its core product helps businesses manage large volumes of&nbsp;data, build models, and gain actionable insights. The company operates globally and serves clients primarily in&nbsp;the marketing and advertising sectors, and focuses on&nbsp;modern technologies, microservices architecture, and cloud-based solutions.</p>
                         <p><strong>Project</strong></p>
                         <p>The project focuses on&nbsp;working with audiences and targeting from the advertisers&rsquo; side (such as&nbsp;marketing agencies and similar structures). It&nbsp;is&nbsp;an&nbsp;independent project within a&nbsp;larger AdTech platform.</p>
                         <p><strong>Responsibilities</strong></p>
                         <ul>
                            <li>Develop and maintain scalable web applications using a&nbsp;microservices architecture</li>
                            <li>Participate in&nbsp;designing and implementing RESTful APIs</li>
                            <li>Support ingestion, processing, and analytics pipelines for large datasets</li>
                            <li>Take part in&nbsp;proof-of-concept (POC) initiatives to&nbsp;assess the viability of&nbsp;emerging tools and practices</li>
                            <li>Contribute to&nbsp;Front-end development using React</li>
                            <li>Ensure code quality through testing, code reviews, and adherence to&nbsp;best practices</li>
                            <li>Work with cloud-based infrastructure and tools to&nbsp;deploy and monitor applications</li>
                            <li>Collaborate with cross-functional teams including Data Analysts and Scientists</li>
                            <li>Support continuous improvement by&nbsp;suggesting tools, frameworks, and workflow enhancements</li>
                         </ul>
                         <p><strong>Requirements</strong></p>
                         <ul>
                            <li>4&nbsp;years of&nbsp;experience in&nbsp;software development using <strong>Java</strong></li>
                            <li>Experience in&nbsp;building multitier web applications and creating REST APIs</li>
                            <li>Good understanding of&nbsp;OOP principles, design patterns, algorithms, and data structures</li>
                            <li>Hands on&nbsp;experience with <strong>React</strong></li>
                            <li>Hands on&nbsp;experience with Linux/Unix environments</li>
                            <li>Experience with relational and/or NoSQL databases (e.g., PostgreSQL, MongoDB, Cassandra)</li>
                            <li>Ability to&nbsp;write clean, maintainable, and well-tested code</li>
                            <li>Eagerness to&nbsp;learn and contribute to&nbsp;engineering best practices</li>
                            <li>At&nbsp;least an&nbsp;Upper-Intermediate level of&nbsp;English</li>
                            <li>Familiarity with cloud platforms (AWS, Google Cloud, or&nbsp;Azure)</li>
                         </ul>
                         <span id="job-description-757365"> <a class="text-secondary" href="#job-item-757365" data-toggle="show-more" data-target="#job-description-757365" data-truncated-state="true" data-more-text="Більше" data-less-text="Згорнути деталі"> <span class="text-nowrap">Більше</span> </a> </span>
                      </div>
                   </li>
                   <li id="job-item-760848" class="mb-4">
                      <div class="d-flex flex-wrap align-items-center gap-1 fs-5 mb-2 text-secondary">
                         <div class="d-inline-flex align-items-center gap-1">
                            <div id="" class="userpic-wrapper userpic-color_3 userpic_xs userpic-transparent is-company-logo userpic--logo" style="--userpic-bg: var(--userpic-color-3);" data-initials="W"><img class="userpic-image userpic-image_img" src="https://p.djinni.co/7c/d3ba882925a169b7889f4ce72c5b18/logo_telegram_400.jpg" alt="" /></div>
                            <a class="text-body js-analytics-event" href="/jobs/company-wiserbrand/" data-analytics="company_page" data-json-parameter="{ &quot;action&quot;: &quot;jobs_list_click&quot;, &quot;company_id&quot;: 2073 }"> WiserBrand </a>
                         </div>
                         <div class="d-flex align-items-center gap-1"><span class="middot"> &middot; </span> <span class="text-nowrap"> 60 переглядів </span> <span class="middot"> &middot; </span> <span class="text-nowrap"> 23 відгуки </span> <span class="middot"> &middot; </span> <span class="text-nowrap" title="12:12 08.09.2025" data-toggle="tooltip"> 5г </span></div>
                      </div>
                      <h2 class="fs-3 mb-2"><a class="job-item__title-link" href="/jobs/760848-senior-lead-full-stack-developer-java-vue/"> Senior/Lead Full Stack Developer (Java / Vue) </a></h2>
                      <div class="fw-medium d-flex flex-wrap align-items-center gap-1"><span class="text-nowrap">Тільки віддалено</span> <span class="middot"> &middot; </span> <span class="text-nowrap"> <span class="location-text">Україна</span> </span> <span class="middot"> &middot; </span> <span class="text-nowrap">7 років досвіду</span> <span class="middot"> &middot; </span> <span class="text-nowrap">B2 &ndash; Вище середнього</span></div>
                      <div>
                         <span class="js-truncated-text">We are looking for a highly skilled Full Stack Java/Vue Developer with strong leadership, architecture, and DevOps expertise to join our team. Requirements: 7+ years of experience in development with Java Strong expertise in Java Spring Boot Hands-on... </span>
                         <p>We are looking for a highly skilled <strong>Full Stack Java/Vue Developer</strong> with strong leadership, architecture, and DevOps expertise to join our team.<br />&nbsp;</p>
                         <p><strong>Requirements:</strong></p>
                         <ul>
                            <li>7+ years of experience in development with Java</li>
                            <li>Strong expertise in Java Spring Boot&nbsp;</li>
                            <li>Hands-on experience with Vue 3</li>
                            <li>Advanced knowledge of PostgreSQL (database optimization, performance tuning, scaling)</li>
                            <li>Experience with DevOps practices (CI/CD, containerization with Docker/Kubernetes, cloud infrastructure management)</li>
                            <li>Proven experience in architecting scalable solutions</li>
                            <li>Strong background in code review, quality assurance, and system optimization</li>
                            <li>English &mdash; Upper Intermediate+</li>
                         </ul>
                         <p><strong>Responsibilities:</strong></p>
                         <ul>
                            <li>Driving technical strategy and mentoring the development team</li>
                            <li>Design and architect complex, scalable solutions in collaboration with senior management</li>
                            <li>Oversee backend development with Java/Spring Boot, ensuring clean and maintainable code</li>
                            <li>Lead the integration of modern frontend frameworks with Vue.js</li>
                            <li>Establish and maintain DevOps pipelines, monitor system reliability, and optimize performance</li>
                            <li>Conduct code reviews and enforce best engineering practices</li>
                            <li>Communicate effectively with both technical and non-technical stakeholders, aligning business and technical objectives</li>
                         </ul>
                         <p><strong>We offer:</strong></p>
                         <ul>
                            <li>A full-time job</li>
                            <li>Flexible working hours</li>
                            <li>Paid vacation and sick leave</li>
                            <li>Managing your taxes and accounting</li>
                            <li>Career and professional growth opportunities</li>
                            <li>Optional benefits package that includes Health insurance, Gym membership, English courses, compensation of certification, courses, and training</li>
                            <li>Creative and lively team of IT specialists, adequate management, and zero unnecessary bureaucracy.</li>
                         </ul>
                         <span id="job-description-760848"> <a class="text-secondary" href="#job-item-760848" data-toggle="show-more" data-target="#job-description-760848" data-truncated-state="true" data-more-text="Більше" data-less-text="Згорнути деталі"> <span class="text-nowrap">Більше</span> </a> </span>
                      </div>
                   </li>
                </ul>
                """;
    }

}
