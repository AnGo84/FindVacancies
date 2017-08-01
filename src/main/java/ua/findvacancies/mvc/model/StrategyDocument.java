package ua.findvacancies.mvc.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by AnGo on 22.06.2017.
 */
public class StrategyDocument
{
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.109 Safari/537.36";
    private static final String REFERRER = "http://google.ru/";
    protected static Document getDocument(String url) throws IOException
    {
        Document document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();

        return document;
    }
}
