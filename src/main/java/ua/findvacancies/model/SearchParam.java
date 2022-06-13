package ua.findvacancies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchParam {
    public static final String DEFAULT_WORD_SEPARATOR = "+";
    public static final String EMPTY_SEARCH_LINE = "";

    private String searchLine;
    private int days;
    private Set<String> keyWords;
    private Set<String> excludeWords;


    public String getKeyWordsSearchLine() {
        return getKeyWordsSearchLine(DEFAULT_WORD_SEPARATOR);
    }

    public String getKeyWordsSearchLine(String wordSeparator) {
        if (CollectionUtils.isEmpty(keyWords)){
            return EMPTY_SEARCH_LINE;
        }
        if (StringUtils.isBlank(wordSeparator)) {
            return String.join(DEFAULT_WORD_SEPARATOR, keyWords);
        }
        return String.join(wordSeparator, keyWords);
    }

}
