package server;

import utils.Link;

import java.util.List;

public interface SearchHttpServer {
    List<Link> search(String host, String request);
}
