package ua.findvacancies.model.strategy;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DjinniStrategyTest {

	private DjinniStrategy djinniStrategy;
	private DocumentConnect mockDocumentConnect;

	@BeforeEach
	public void beforeEach() {
		mockDocumentConnect = mock(DocumentConnect.class);
		djinniStrategy = new DjinniStrategy(mockDocumentConnect);
	}

	@Test
	public void whenGetVacancies_returnResult() throws IOException {
		SearchParam searchParam = TestUtils.getSearchParams();
		searchParam.setDays(2000);
		String djinniSearchURL = String.format(djinniStrategy.getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), 1);

		Document document = TestUtils.getDocumentByClassPath("sites/djinni/Djinni_vacancies.html");

		when(mockDocumentConnect.getDocument(djinniSearchURL)).thenReturn(document);
		List<Vacancy> result = djinniStrategy.getVacancies(searchParam);

		assertNotNull(result);
		assertEquals(15, result.size());
	}

}
