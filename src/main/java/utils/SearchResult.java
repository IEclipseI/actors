package utils;

import lombok.Value;

import java.util.List;

@Value
public class SearchResult {
    String host;
    List<Link> links;
}
