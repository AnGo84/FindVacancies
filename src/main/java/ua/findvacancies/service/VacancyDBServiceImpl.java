package ua.findvacancies.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.repository.VacancyRepository;

import java.util.List;

@Service
public class VacancyDBServiceImpl implements VacancyDBService {

	@Autowired
	private VacancyRepository vacancyRepository;

	@Override
	public int save(Vacancy vacancy) {
		return vacancyRepository.save(vacancy);
	}

	@Override
	public List findAll() {
		return vacancyRepository.findAll();
	}

}
