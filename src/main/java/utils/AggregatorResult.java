package utils;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AggregatorResult {
    Map<String, List<Link>> result;
}
