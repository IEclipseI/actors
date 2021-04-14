package engine;

import utils.SearchResult;

public interface SearchEngine {
    String getName();

    SearchResult search(String request);
}
