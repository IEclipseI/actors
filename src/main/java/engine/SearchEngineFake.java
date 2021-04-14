package engine;

import lombok.AllArgsConstructor;
import server.SearchHttpServer;
import utils.SearchResult;

@AllArgsConstructor
public class SearchEngineFake implements SearchEngine {
    SearchHttpServer server;
    String host;

    @Override
    public SearchResult search(String request) {
        return new SearchResult(host, server.search(host, request));
    }

    @Override
    public String getName() {
        return host;
    }

}
