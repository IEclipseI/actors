package aggragator;

import actor.SearchEngineMasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import engine.SearchEngine;
import lombok.SneakyThrows;
import scala.concurrent.duration.Duration;
import utils.AggregatorResult;
import utils.SearchRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchAggregator {
    List<SearchEngine> engines;

    public SearchAggregator(List<SearchEngine> engines) {
        this.engines = engines;
    }

    @SneakyThrows
    public AggregatorResult search(String request, Duration timeout) {
        ActorSystem system = ActorSystem.create("SearchAggregatorSystem");
        CompletableFuture<AggregatorResult> result = new CompletableFuture<>();
        ActorRef master = system.actorOf(Props.create(SearchEngineMasterActor.class, engines, result, timeout));
        master.tell(new SearchRequest(request), ActorRef.noSender());
        try {
            return result.get();
        } finally {
            system.terminate();
        }
    }
}
