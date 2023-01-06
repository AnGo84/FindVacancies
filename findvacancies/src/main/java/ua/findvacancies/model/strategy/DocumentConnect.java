package ua.findvacancies.model.strategy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * The steps blow can help you to get the "User-Agent":
 *
 * open the chrome, press the F12.
 * enter any available website, then press Enter.
 * click the "NetWork" on DevTools, and click the website that entered just right.
 */
public class DocumentConnect {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static final String REFERRER = "https://google.ua/";

    public Document getDocument(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();

        return document;
    }
}
