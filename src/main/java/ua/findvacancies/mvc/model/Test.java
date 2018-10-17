package ua.findvacancies.mvc.model;



import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.Vacancy;

import java.util.List;
import java.util.Set;

/**
 * Created by AnGo on 22.06.2017.
 */
public class Test
{
    public static void main(String[] args)
    {
//        Provider provider = new Provider(new DOUStrategy());
//        Provider provider = new Provider(new HHStrategy());
//        Provider provider = new Provider(new RabotaUAStrategy());
        Provider provider = new Provider(new WorkUAStrategy());
        String keyWords = "Java developer -senior";

        System.out.println("KeyLine: "+ StringUtils.getKeyWordsLine(keyWords));
        System.out.println("Exclude: "  + StringUtils.getExcludeWordsSet(keyWords));

        List<Vacancy> vacancies =provider.getJavaVacancies(keyWords, -10);


        for (Vacancy vacancy : vacancies)
        {
            System.out.println(vacancy);
        }

    }
}
