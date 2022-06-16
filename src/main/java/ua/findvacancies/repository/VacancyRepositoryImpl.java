package ua.findvacancies.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.findvacancies.model.Vacancy;

import java.util.List;

@Repository
public class VacancyRepositoryImpl implements VacancyRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int save(Vacancy vacancy) {
		return jdbcTemplate.update(
				"insert into vacancies (title, salary, city, company_name, site_name, url) values(?,?,?,?,?,?)",
				vacancy.getTitle(), vacancy.getSalary(), vacancy.getCity(), vacancy.getCompanyName(), vacancy.getSiteName(), vacancy.getUrl());

	}

	@Override
	public List findAll() {
		return jdbcTemplate.query(
				"select * from vacancies",
				(rs, rowNum) ->
						Vacancy.builder()
								.title(rs.getString("title"))
								.salary(rs.getString("salary"))
								.city(rs.getString("city"))
								.companyName(rs.getString("company_name"))
								.siteName(rs.getString("site_name"))
								.url(rs.getString("url"))
								.build()
		);
	}

}
