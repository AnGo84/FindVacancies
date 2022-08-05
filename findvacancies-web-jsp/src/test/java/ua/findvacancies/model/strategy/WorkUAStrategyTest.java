package ua.findvacancies.model.strategy;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.utils.AppDateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkUAStrategyTest {
    private WorkUAStrategy strategy;
    private DocumentConnect mockDocumentConnect;
    private final DocumentConnect documentConnect = new DocumentConnect();

    @BeforeEach
    public void beforeEach() {
        mockDocumentConnect = mock(DocumentConnect.class);
        strategy = new WorkUAStrategy(mockDocumentConnect);
    }

    @Test
    public void whenGetVacancies_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String searchURL = String.format(strategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

        Document document = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancies.html");
        Document documentVacancyHot = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_hot.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_with_salary.html");
        Document documentVacancyCommon = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_common.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/3747708/")).thenReturn(documentVacancyHot);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4016482/")).thenReturn(documentVacancyWithSalary);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4031195/")).thenReturn(documentVacancyCommon);

        List<Vacancy> result = strategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(3, result.size());

        searchParam.setDays(0);
        result = strategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongDate_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(0);
        String searchURL = String.format(strategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

        Document document = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancies.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_with_salary.html");
        Document documentVacancyCommon = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_common.html");
        Document documentVacancyWithWrongDate = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_with_wrong_data.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4016482/")).thenReturn(documentVacancyWithSalary);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4031195/")).thenReturn(documentVacancyCommon);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4038296/")).thenReturn(documentVacancyWithWrongDate);
        List<Vacancy> result = strategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(1, result.size());

        Assertions.assertEquals(0, AppDateUtils.compareDatesByDayMonthYear(new Date(), result.get(0).getDate()));
    }

    @Test
    public void whenGetVacanciesWithWrongSite_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        String searchURL = String.format(strategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

        Document document = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancies.html");
        Document documentVacancyHot = TestUtils.getDocumentByClassPath("sites/workua/WorkUA_vacancy_hot.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentFromText("wrong text");

        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/3747708/")).thenReturn(documentVacancyHot);
        when(mockDocumentConnect.getDocument("https://www.work.ua/jobs/4016482/")).thenReturn(documentVacancyWithSalary);
        List<Vacancy> result = strategy.getVacancies(searchParam);
        //System.out.println("Result: " + result);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongVacancyLink_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String searchURL = String.format(strategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);
        Document document = TestUtils.getDocumentFromText("wrong text");
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        List<Vacancy> result = strategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

    }

    @Test
    public void whenGetVacanciesWithWrongSearchParams_returnEmptyResult() throws IOException {
        List<Vacancy> result = strategy.getVacancies(null);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

        SearchParam searchParam = new SearchParam();
        String searchURL = String.format(strategy.getSiteURLPattern(), "", 1);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(null);
        result = strategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());
    }
}