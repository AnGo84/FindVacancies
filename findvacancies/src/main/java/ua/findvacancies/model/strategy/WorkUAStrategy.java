package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class WorkUAStrategy extends AbstractStrategy {

	private static final Pattern ANY_DIGITS_PATTERN = Pattern.compile("\\d");
	private static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final String DATE_FORMAT_TEXT = "dd MMMM yyyy";
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

	private static final String[] months = {
			"січня", "лютого", "березня", "квітня", "травня", "червня",
			"липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};

	private static final Locale locale = new Locale("ua");
	private static final DateFormatSymbols dateShotFormatSymbols = DateFormatSymbols.getInstance(locale);
	private static final SimpleDateFormat simpleDateTextFormat = new SimpleDateFormat(DATE_FORMAT_TEXT, locale);

	static {
		dateShotFormatSymbols.setMonths(months);
		simpleDateTextFormat.setDateFormatSymbols(dateShotFormatSymbols);
	}

	private final DocumentConnect documentConnect;

	@Override
	public String getSiteURL() {
		return "https://work.ua";
	}


	@Override
	public String getSiteURLPattern() {
		return "https://www.work.ua/jobs-%s/?page=%d";
	}

	@Override
	public List<Vacancy> getVacancies(SearchParam searchParam) {
		if (searchParam == null) {
			return Collections.emptyList();
		}
		initVacanciesList();
		try {
			int pageCount = 0;
			boolean hasData = true;
			while (hasData) {
				String searchPageURL = String.format(getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), ++pageCount);
				Document doc = documentConnect.getDocument(searchPageURL);
				if (doc == null) {
					break;
				}

				Element vacancyListIdEl = doc.getElementById("pjax-job-list");
				if (vacancyListIdEl == null) {
					break;
				}
				Elements vacanciesListEl = vacancyListIdEl.getElementsByClass("job-link");

				if (CollectionUtils.isEmpty(vacanciesListEl)) {
					break;
				}
				for (Element element : vacanciesListEl) {
					String vacancyURL = element.getElementsByTag("a").attr("href");
					if (vacancyURL.startsWith("/")) {
						vacancyURL = getSiteURL() + vacancyURL;
					}
					Vacancy vacancy = getVacancy(vacancyURL);
					vacancy.setSiteName(getSiteURL());

					checkAndAddVacancyToList(vacancy, searchParam);
				}
			}

		} catch (Exception e) {
			log.error("Error on parsing WorkUA: {}", e.getMessage(), e);
		}

		return vacancies;
	}

	@Override
	public Vacancy getVacancy(String vacancyURL) {
		String vacancyDate = "";
		try {
			Document vacancyDoc = documentConnect.getDocument(vacancyURL);
			if (vacancyDoc != null) {
				Element vacancyEl = vacancyDoc.getElementsByClass("wordwrap").first();
				vacancyDate = getTextFromFirstElByClassName(vacancyEl.getElementsByClass("cut-bottom-print"), "text-muted");

				Element vacancyCityEl = vacancyEl.getElementsByClass("glyphicon-map-marker").first();
				String vacancyCity = "";
				if (vacancyCityEl != null) {
					vacancyCity = vacancyCityEl.parent().text();
				}

				return Vacancy.builder()
						.companyName(getTextFromNextTagByClassName(vacancyEl, "glyphicon-company", "b"))
						.title(vacancyEl.getElementById("h1-name").text())
						.city(vacancyCity)
						.salary(getTextFromNextTagByClassName(vacancyEl, "glyphicon-hryvnia", "b"))
						.isHot(!CollectionUtils.isEmpty(vacancyEl.getElementsByClass("label-hot")))
						.url(vacancyURL)
						.date(parseVacationDate(vacancyDate))
						.build();
			}
		} catch (Exception e) {
			log.error("Error on parsing vacancy by url {}: {}", vacancyURL, e.getMessage(), e);
		}
		return new Vacancy();
	}

	private Date parseVacationDate(String dateString) {
		try {
			//dateString = dateString.substring(dateString.lastIndexOf(NON_BREAKING_SPACE_CHAR) + 1);
			Matcher matcher = ANY_DIGITS_PATTERN.matcher(dateString);

			if (matcher.find()) {
                /*System.out.println("Start index: " + matcher.start());
                System.out.println("End index: " + matcher.end());*/
				dateString = dateString.substring(matcher.start());

				return simpleDateTextFormat.parse(dateString);
			}
		} catch (ParseException e) {
			log.warn("Error on parsing vacancy date '{}': {}", dateString, e.getMessage());
		}
		return new Date();
	}

}
