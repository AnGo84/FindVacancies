package ua.findvacancies.mvc.model.strategy;

import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.utils.AppDateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DOUStrategyTest {
    private DOUStrategy douStrategy;
    private DocumentConnect mockDocumentConnect;
    private final DocumentConnect documentConnect = new DocumentConnect();

    @Before
    public void beforeEach() {
        mockDocumentConnect = mock(DocumentConnect.class);
        douStrategy = new DOUStrategy(mockDocumentConnect);
    }

    @Test
    public void whenGetVacancies_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine());

        Document document = TestUtils.getDocumentByClassPath("sites/dou/DOU_vacancies.html");
        Document documentVacancyHot = TestUtils.getDocumentByClassPath("sites/dou/DOU_Suntech_hot.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/dou/DOU_TruePlay_with_salary.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(douSearchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/suntech-innovation/vacancies/79386/?from=list_hot")).thenReturn(documentVacancyHot);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/trueplay/vacancies/117358/")).thenReturn(documentVacancyWithSalary);
        List<Vacancy> result = douStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(2, result.size());

        searchParam.setDays(0);
        result = douStrategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongDate_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(0);
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine());

        Document document = TestUtils.getDocumentByClassPath("sites/dou/DOU_vacancies.html");
        Document documentVacancyHot = TestUtils.getDocumentByClassPath("sites/dou/DOU_Suntech_with_wrong_date.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/dou/DOU_TruePlay_with_salary.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(douSearchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/suntech-innovation/vacancies/79386/?from=list_hot")).thenReturn(documentVacancyHot);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/trueplay/vacancies/117358/")).thenReturn(documentVacancyWithSalary);
        List<Vacancy> result = douStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(0, AppDateUtils.compareDatesByDayMonthYear(new Date(), result.get(0).getDate()));
    }

    @Test
    public void whenGetVacanciesWithWrongSite_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine());

        Document document = TestUtils.getDocumentByClassPath("sites/dou/DOU_vacancies.html");
        Document documentVacancyHot = TestUtils.getDocumentByClassPath("sites/dou/DOU_Suntech_hot.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentFromText("wrong text");

        when(mockDocumentConnect.getDocument(douSearchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/suntech-innovation/vacancies/79386/?from=list_hot")).thenReturn(documentVacancyHot);
        when(mockDocumentConnect.getDocument("https://jobs.dou.ua/companies/trueplay/vacancies/117358/")).thenReturn(documentVacancyWithSalary);
        List<Vacancy> result = douStrategy.getVacancies(searchParam);
        System.out.println("Result: " + result);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongVacancyLink_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine());
        Document document = TestUtils.getDocumentFromText("wrong text");
        when(mockDocumentConnect.getDocument(douSearchURL)).thenReturn(document);
        List<Vacancy> result = douStrategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

    }

    @Test
    public void whenGetVacanciesWithWrongSearchParams_returnEmptyResult() throws IOException {
        List<Vacancy> result = douStrategy.getVacancies(null);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

        SearchParam searchParam = new SearchParam();
        String douSearchURL = String.format(DOUStrategy.URL_FORMAT, "");
        when(mockDocumentConnect.getDocument(douSearchURL)).thenReturn(null);
        result = douStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());
    }
}