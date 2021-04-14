package server;

import org.apache.commons.lang3.RandomStringUtils;
import utils.Link;

import java.util.List;

public class SearchHttpServerFake implements SearchHttpServer {
    public String randomString(int len) {
        return RandomStringUtils.random(len, true, true);
    }

    @Override
    public List<Link> search(String host, String request) {
        return List.of(new Link(randomString(30), randomString(200)));
    }
}
