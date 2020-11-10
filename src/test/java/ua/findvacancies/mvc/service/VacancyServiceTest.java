package ua.findvacancies.mvc.service;

import org.junit.Before;
import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.model.strategy.*;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VacancyServiceTest {
    private final VacancyService vacancyService = new VacancyService();

    private Set<Strategy> strategies;
    private SearchParam searchParam;
    private DocumentConnect mockDocumentConnect;


    @Before
    public void beforeEach() throws IOException {
        mockDocumentConnect = mock(DocumentConnect.class);
        strategies = new HashSet<>(Arrays.asList(new DOUStrategy(mockDocumentConnect),
                new HHStrategy(mockDocumentConnect),
                new RabotaUAStrategy(mockDocumentConnect),
                new WorkUAStrategy(mockDocumentConnect)));

        searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);

        //mock DOU
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine());
        when(mockDocumentConnect.getDocument(douSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/suntech-innovation/vacancies/79386/?from=list_hot"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_Suntech_hot.html"));
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/trueplay/vacancies/117358/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/dou/DOU_TruePlay_with_salary.html"));

        //mock HH
        String hhSearchURL = String.format(HHStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(),0);

        when(mockDocumentConnect.getDocument(hhSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39921284?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_wrong_date.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39456818?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_salary_Fortuna.html"));
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39802143?query=java%20developer"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_without_Company_Name.html"));

        //mock RabotaUA
        String rabotaSearchURL = String.format(RabotaUAStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(RabotaUAStrategy.WORD_SEPARATOR), 1);

        when(mockDocumentConnect.getDocument(rabotaSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RobotaUA_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company816229/vacancy5872044"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RobotaUA_vacancy_hot.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company1042/vacancy8182405"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RobotaUA_vacancy_with_salary.html"));
        when(mockDocumentConnect.getDocument("https://rabota.ua/ua/company794/vacancy8168568"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/rabotaua/RobotaUA_vacancy_common.html"));

        //mock WorkUA
        String workSearchURL = String.format(WorkUAStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(), 1);

        when(mockDocumentConnect.getDocument(workSearchURL))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancies.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/3747708/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_hot.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4016482/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_with_salary.html"));
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4031195/"))
                .thenReturn(TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_common.html"));
    }

    @Test
    public void whenReturnDefaultViewSearchParams() {
        ViewSearchParams searchParams = vacancyService.getDefaultViewSearchParams();
        assertNotNull(searchParams);
        assertEquals(String.valueOf(VacancyService.DEFAULT_DAYS), searchParams.getDays());
        assertEquals(VacancyService.DEFAULT_SEARCH, searchParams.getSearchLine());
    }

    @Test
    public void whenGetVacancyListReturnResults(){
        // threads
        List<Vacancy> vacancyList = vacancyService.getVacancyListByThreads(strategies, searchParam);

        assertNotNull(vacancyList);
        assertFalse(vacancyList.isEmpty());
        assertEquals(11, vacancyList.size());

        // lazy
        List<Vacancy> vacancyList2 = vacancyService.getVacancyList(strategies, searchParam);

        assertNotNull(vacancyList2);
        assertFalse(vacancyList2.isEmpty());
        assertEquals(11, vacancyList2.size());
    }

}