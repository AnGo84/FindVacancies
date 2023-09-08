package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class DjinniStrategy extends AbstractStrategy {

	private static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final String DATE_FORMAT_TEXT = "hh:mm dd.MM.yyyy";
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
		return "https://djinni.co";
	}


	@Override
	public String getSiteURLPattern() {
		return "https://djinni.co/jobs/?keywords=%s&page=%d";
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
				if (doc == null || doc.baseUri().endsWith("/jobs/")) {
					break;
				}

				Element vacancyListIdEl = doc.getElementsByClass("list-jobs").first();
				if (vacancyListIdEl == null) {
					break;
				}
				Elements vacanciesListEl = vacancyListIdEl.getElementsByClass("job-list-item");

				if (CollectionUtils.isEmpty(vacanciesListEl)) {
					break;
				}
				//log.info("Djinni vacanciesListEl: {}", vacanciesListEl.size());
				for (Element element : vacanciesListEl) {

					Element elementVacancyURL = element.getElementsByClass("job-list-item__link").first();
					String vacancyURL = elementVacancyURL.attr("href");
					if (vacancyURL.startsWith("/")) {
						vacancyURL = getSiteURL() + vacancyURL;
					}

					String vacancyTitle = elementVacancyURL.text();

					String vacancyCompanyName = getTextFromNextTagByClassName(element, "job-list-item__pic", "a");

					Element elementCounts = element.getElementsByClass("job-list-item__counts").first();
					Element elementDateCover = elementCounts.children().first();
					Element elementDate = elementDateCover.children().first();

					String dateString = elementDate.attr("data-original-title");
					dateString = ObjectUtils.isEmpty(dateString) ? elementDate.attr("title") : "";
					Date vacancyDate = parseVacationDate(dateString);

					Element elementSalary = element.getElementsByClass("public-salary-item").first();
					String vacancySalary = elementSalary == null ? "" : elementSalary.text();

					Element elementLocation = element.getElementsByClass("location-text").first();

					Element elementCity = elementLocation.getElementsByTag("span").first();
					String vacancyCity = elementCity == null ? elementLocation.text() : elementCity.text();

					Vacancy vacancy = Vacancy.builder()
							.title(vacancyTitle)
							.salary(vacancySalary)
							.city(vacancyCity)
							.companyName(vacancyCompanyName)
							.siteName(getSiteURL())
							.url(vacancyURL)
							.date(vacancyDate)
							.build();

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
		return new Vacancy();
	}

	private Date parseVacationDate(String dateString) {
		//log.info("Date: {}", dateString);
		try {
			return simpleDateTextFormat.parse(dateString);
		} catch (ParseException e) {
			log.warn("Error on parsing vacancy date '{}': {}", dateString, e.getMessage());
		}
		return new Date();
	}

}
