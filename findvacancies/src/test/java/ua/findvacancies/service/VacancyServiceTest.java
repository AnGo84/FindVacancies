package ua.findvacancies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.strategy.*;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VacancyServiceTest {
    private final VacancyService vacancyService = new VacancyService();

    private Set<Strategy> strategies;
    private SearchParam searchParam;
    private DocumentConnect mockDocumentConnect;


    @BeforeEach
    public void beforeEach() throws IOException {
        mockDocumentConnect = mock(DocumentConnect.class);

        DOUStrategy douStrategy = new DOUStrategy(mockDocumentConnect);
        HHStrategy hhStrategy = new HHStrategy(mockDocumentConnect);
        RabotaUAStrategy rabotaUAStrategy = new RabotaUAStrategy(mockDocumentConnect);
        WorkUAStrategy workUAStrategy = new WorkUAStrategy(mockDocumentConnect);
        GRCStrategy grcStrategy = new GRCStrategy(mockDocumentConnect);

        strategies = new HashSet<>(
                Arrays.asList(douStrategy, hhStrategy, rabotaUAStrategy, workUAStrategy, grcStrategy));

        searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);

        //mock DOU

        String douSearchURL = String.format(douStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine());
        when(mockDocumentConnect.getDocument(douSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/suntech-innovation/vacancies/79386/?from=list_hot"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_Suntech_hot.html"));
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/trueplay/vacancies/117358/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_TruePlay_with_salary.html"));

        //mock HH
        String hhSearchURL = String.format(hhStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 0);

        when(mockDocumentConnect.getDocument(hhSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39921284?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_wrong_date.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39456818?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_salary_Fortuna.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39802143?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_without_Company_Name.html"));

        //mock RabotaUA
        String rabotaSearchURL = String.format(rabotaUAStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(RobotaUAStrategy.WORD_SEPARATOR), 1);

        when(mockDocumentConnect.getDocument(rabotaSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RabotaUA_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company816229/vacancy5872044"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RabotaUA_vacancy_hot.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company1042/vacancy8182405"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RabotaUA_vacancy_with_salary.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company794/vacancy8168568"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RabotaUA_vacancy_common.html"));

        //mock WorkUA
        String workSearchURL = String.format(workUAStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

        when(mockDocumentConnect.getDocument(workSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/3747708/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_hot.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4016482/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_with_salary.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4031195/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_common.html"));

        //mock GRC
        String grcSearchURL = String.format(grcStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

        when(mockDocumentConnect.getDocument(grcSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/grc/GRC_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancies/69925523"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/grc/GRC_vacancy_with_salary.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancies/69924631"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/grc/GRC_vacancy_common.html"));
    }

    @Test
    public void whenReturnDefaultViewSearchParams() {
        ViewSearchParams searchParams = vacancyService.getDefaultViewSearchParams();
        assertNotNull(searchParams);
        assertEquals(String.valueOf(VacancyService.DEFAULT_DAYS), searchParams.getDays());
        assertEquals(VacancyService.DEFAULT_SEARCH, searchParams.getSearchLine());
    }

    @Test
    public void whenGetVacancyListReturnResults() {
        // threads
        List<Vacancy> vacancyList = vacancyService.getVacancyListByThreads(strategies, searchParam);

        assertNotNull(vacancyList);
        assertFalse(vacancyList.isEmpty());
        assertEquals(13, vacancyList.size());

        // lazy
        List<Vacancy> vacancyList2 = vacancyService.getVacancyList(strategies, searchParam);

        assertNotNull(vacancyList2);
        assertFalse(vacancyList2.isEmpty());
        assertEquals(13, vacancyList2.size());
    }

}