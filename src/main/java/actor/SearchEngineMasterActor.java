package actor;

import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import engine.SearchEngine;
import scala.concurrent.duration.Duration;
import utils.AggregatorResult;
import utils.Link;
import utils.SearchRequest;
import utils.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SearchEngineMasterActor extends UntypedActor {
    List<SearchEngine> engines;
    AggregatorResult result;
    CompletableFuture<AggregatorResult> futureResult;
    boolean finished = false;

    public SearchEngineMasterActor(List<SearchEngine> engines, CompletableFuture<AggregatorResult> futureResult, Duration timeout) {
        this.engines = engines;
        this.result = new AggregatorResult(new HashMap<>());
        this.futureResult = futureResult;
        context().setReceiveTimeout(timeout);
    }

    @Override
    public void onReceive(Object o) {
        if (!finished) {
            if (o instanceof SearchRequest) {
                engines.forEach(engine -> getContext().actorOf(Props.create(SearchEngineActor.class, engine)).tell(o, context().self()));
            } else if (o instanceof SearchResult) {
                SearchResult searchResult = (SearchResult) o;
                Map<String, List<Link>> resultMap = this.result.getResult();
                resultMap.put(searchResult.getHost(), searchResult.getLinks());
                if (resultMap.size() == engines.size()) {
                    finish();
                }
            } else if (o instanceof ReceiveTimeout) {
                finish();
            }
        }
    }

    public void finish() {
        finished = true;
        futureResult.complete(result);
    }
}
