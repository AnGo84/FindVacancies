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

public class HHStrategyTest {
    private HHStrategy hhStrategy;
    private DocumentConnect mockDocumentConnect;
    private final DocumentConnect documentConnect = new DocumentConnect();

    @Before
    public void beforeEach() {
        mockDocumentConnect = mock(DocumentConnect.class);
        hhStrategy = new HHStrategy(mockDocumentConnect);
    }

    @Test
    public void whenGetVacancies_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String searchURL = String.format(HHStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(), 0);
        //System.out.println("SearchURL: " + searchURL);
        Document document = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancies.html");
        Document documentVacancy = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_Luxoft.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_salary_Fortuna.html");
        Document documentVacancyWithOutName = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_without_Company_Name.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39921284?query=java%20developer")).thenReturn(documentVacancy);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39456818?query=java%20developer")).thenReturn(documentVacancyWithSalary);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39802143?query=java%20developer")).thenReturn(documentVacancyWithOutName);
        List<Vacancy> result = hhStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(3, result.size());

        searchParam.setDays(0);
        result = hhStrategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongDate_returnResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(0);
        String searchURL = String.format(HHStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(),0);

        Document document = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancies.html");
        Document documentVacancy = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_wrong_date.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_salary_Fortuna.html");
        Document documentVacancyWithOutName = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_without_Company_Name.html");
        //System.out.println("Document: " + document);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39921284?query=java%20developer")).thenReturn(documentVacancy);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39456818?query=java%20developer")).thenReturn(documentVacancyWithSalary);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39802143?query=java%20developer")).thenReturn(documentVacancyWithOutName);
        List<Vacancy> result = hhStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(0, AppDateUtils.compareDatesByDayMonthYear(new Date(), result.get(0).getDate()));
    }

    @Test
    public void whenGetVacanciesWithWrongSite_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String searchURL = String.format(HHStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(),0);

        Document document = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancies.html");
        Document documentVacancy = TestUtils.getDocumentByClassPath("sites/hh/HH_vacancy_with_wrong_date.html");
        Document documentVacancyWithSalary = TestUtils.getDocumentFromText("wrong text");

        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39921284?query=java%20developer")).thenReturn(documentVacancy);
        when(mockDocumentConnect.getDocument("https://grc.ua/vacancy/39456818?query=java%20developer")).thenReturn(documentVacancyWithSalary);

        List<Vacancy> result = hhStrategy.getVacancies(searchParam);
        //System.out.println("Result: " + result);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void whenGetVacanciesWithWrongVacancyLink_returnEmptyResult() throws IOException {
        SearchParam searchParam = TestUtils.getSearchParams();
        searchParam.setDays(2000);
        String searchURL = String.format(DOUStrategy.URL_FORMAT, searchParam.getKeyWordsSearchLine(),0);
        Document document = TestUtils.getDocumentFromText("wrong text");
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(document);
        List<Vacancy> result = hhStrategy.getVacancies(searchParam);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

    }

    @Test
    public void whenGetVacanciesWithWrongSearchParams_returnEmptyResult() throws IOException {
        List<Vacancy> result = hhStrategy.getVacancies(null);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());

        SearchParam searchParam = new SearchParam();
        String searchURL = String.format(DOUStrategy.URL_FORMAT, "",0);
        when(mockDocumentConnect.getDocument(searchURL)).thenReturn(null);
        result = hhStrategy.getVacancies(searchParam);
        //System.out.println("" + result);
        assertNotNull(result);
        assertEquals(true, result.isEmpty());
    }
}