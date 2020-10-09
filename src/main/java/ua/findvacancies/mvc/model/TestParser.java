package ua.findvacancies.mvc.model;



import ua.findvacancies.mvc.mappers.SearchParamMapper;
import ua.findvacancies.mvc.utils.AppStringUtils;
import ua.findvacancies.mvc.utils.ViewSearchParamsUtils;
import ua.findvacancies.mvc.viewdata.Vacancy;
import ua.findvacancies.mvc.viewdata.ViewSearchParams;

import java.util.List;

public class TestParser
{
    public static void main(String[] args)
    {
//        Provider provider = new Provider(new DOUStrategy());
//        Provider provider = new Provider(new HHStrategy());
//        Provider provider = new Provider(new RabotaUAStrategy());
//        Provider provider = new Provider(new WorkUAStrategy());
        String keyWords = "Java developer -senior";
        ViewSearchParams viewSearchParams =new ViewSearchParams(keyWords, "-3");
        SearchParam searchParam =  new SearchParamMapper().convert(viewSearchParams);

        System.out.println("KeyLine: "+ searchParam.getKeyWordsSearchLine());
        System.out.println("Exclude: "  + searchParam.getExcludeWords());

        List<Vacancy> vacancies =Provider.HEADHUNTER.getStrategy().getVacancies(searchParam);

        for (Vacancy vacancy : vacancies)
        {
            System.out.println(vacancy);
        }

    }
}
