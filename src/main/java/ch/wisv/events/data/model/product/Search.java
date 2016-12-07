package ch.wisv.events.data.model.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Search Object.
 * Object for search API.
 */
@JsonAutoDetect
public class Search {

    /**
     * Query used for searching.
     */
    @Getter
    @Setter
    private String query;

    /**
     * Collection of suggested items.
     */
    @Getter
    @Setter
    private Collection<SearchItem> suggestions;

    /**
     * Default Constructor.
     *
     * @param query Search query
     */
    public Search(String query) {
        this.query = query;
        this.suggestions = new ArrayList<>();
    }

    /**
     * Add suggestion item to Search.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    public void addItem(String title, Long key) {
        SearchItem temp = new SearchItem(title, key);
        this.suggestions.add(temp);
    }
}