package ua.findvacancies;


import ua.findvacancies.mappers.SearchParamMapper;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;

import java.util.HashSet;
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
        ViewSearchParams viewSearchParams = new ViewSearchParams(keyWords, "7", new HashSet<>());
        SearchParam searchParam = new SearchParamMapper().convert(viewSearchParams);

        System.out.println("KeyLine: "+ searchParam.getKeyWordsSearchLine());
        System.out.println("Exclude: "  + searchParam.getExcludeWords());

        long startTime = System.currentTimeMillis();

        List<Vacancy> vacancies = Provider.RABOTAUA.getStrategy().getVacancies(searchParam);

        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
        System.out.println("That took " + (endTime - startTime)/1000 + " seconds");

        System.out.println("Find vacancies: "+vacancies.size());
        for (Vacancy vacancy : vacancies)
        {
            System.out.println(vacancy);
        }

    }
}
