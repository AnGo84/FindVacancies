package ua.findvacancies.mvc.model;

import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class SearchParam {
    private static final String DEFAULT_WORD_SEPARATOR = "+";
    public static final String EMPTY_SEARCH_LINE = "";

    private String searchLine;
    private int days;
    private Set<String> keyWords;
    private Set<String> excludeWords;

    public SearchParam() {
    }

    public String getSearchLine() {
        return searchLine;
    }

    public void setSearchLine(String searchLine) {
        this.searchLine = searchLine;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    public Set<String> getExcludeWords() {
        return excludeWords;
    }

    public void setExcludeWords(Set<String> excludeWords) {
        this.excludeWords = excludeWords;
    }

    public String getKeyWordsSearchLine() {
        //return String.join(DEFAULT_WORD_SEPARATOR, keyWords);
        return getKeyWordsSearchLine(DEFAULT_WORD_SEPARATOR);
    }

    public String getKeyWordsSearchLine(String wordSeparator) {
        if (CollectionUtils.isEmpty(keyWords)){
            return EMPTY_SEARCH_LINE;
        }
        if (Strings.isBlank(wordSeparator)) {
            return String.join(DEFAULT_WORD_SEPARATOR, keyWords);
        }
        return String.join(wordSeparator, keyWords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchParam)) return false;

        SearchParam that = (SearchParam) o;

        if (days != that.days) return false;
        if (searchLine != null ? !searchLine.equals(that.searchLine) : that.searchLine != null) return false;
        if (keyWords != null ? !keyWords.equals(that.keyWords) : that.keyWords != null) return false;
        return excludeWords != null ? excludeWords.equals(that.excludeWords) : that.excludeWords == null;
    }

    @Override
    public int hashCode() {
        int result = searchLine != null ? searchLine.hashCode() : 0;
        result = 31 * result + days;
        result = 31 * result + (keyWords != null ? keyWords.hashCode() : 0);
        result = 31 * result + (excludeWords != null ? excludeWords.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchParam{");
        sb.append("searchLine='").append(searchLine).append('\'');
        sb.append(", days=").append(days);
        sb.append(", keyWords=").append(keyWords);
        sb.append(", excludeWords=").append(excludeWords);
        sb.append('}');
        return sb.toString();
    }
}
