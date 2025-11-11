package ua.findvacancies.common.scraper;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
public class JsoupDocumentFetcher {

    public static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/119.0.0.0 Safari/537.36";


    /**
     * Fetches an HTML document from the given URL.
     *
     * @param url The URL to fetch.
     * @return The Jsoup Document object.
     * @throws IOException If a connection error occurs or the URL is invalid.
     */
    public Document fetchDocument(final String url) throws IOException {
        log.debug("Fetching document from URL: {}", url);
        try {
            // Connect to the URL
            // .timeout(10000) sets a timeout of 10 seconds for the connection and read.
            // .userAgent(...) helps mimic a real browser to avoid being blocked.
            return Jsoup.connect(url)
                    .timeout(10000) // 10 seconds timeout
                    .userAgent(DEFAULT_USER_AGENT)
                    .get();
        } catch (IOException e) {
            log.error("Failed to fetch document from URL: {}. Error: {}", url, e.getMessage());
            throw e;
        }
    }

}
