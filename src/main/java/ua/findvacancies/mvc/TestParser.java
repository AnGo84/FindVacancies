package ua.findvacancies.mvc;


import ua.findvacancies.mvc.mappers.SearchParamMapper;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.util.List;

public class TestParser
{
    public static void main(String[] args)
    {
//        Provider provider = new Provider(new DOUStrategy());
//        Provider provider = new Provider(new HHStrategy());
//        Provider provider = new Provider(new RabotaUAStrategy());
//        Provider provider = new Provider(new WorkUAStrategy());
        //String keyWords = "Java developer -senior";
        String keyWords = "Java developer";
        ViewSearchParams viewSearchParams =new ViewSearchParams(keyWords, "5");
        SearchParam searchParam =  new SearchParamMapper().convert(viewSearchParams);

        System.out.println("KeyLine: "+ searchParam.getKeyWordsSearchLine());
        System.out.println("Exclude: "  + searchParam.getExcludeWords());

        List<Vacancy> vacancies = Provider.HEADHUNTER.getStrategy().getVacancies(searchParam);
        System.out.println("Find vacancies: "+vacancies.size());
        for (Vacancy vacancy : vacancies)
        {
            System.out.println(vacancy);
        }

    }
}
