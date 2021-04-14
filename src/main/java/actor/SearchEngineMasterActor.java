package actor;

import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import engine.SearchEngine;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import utils.AggregatorResult;
import utils.Link;
import utils.SearchRequest;
import utils.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SearchEngineMasterActor extends UntypedActor {
    List<SearchEngine> engines;
    AggregatorResult result;
    CompletableFuture<AggregatorResult> futureResult;
    boolean finished = false;

    public SearchEngineMasterActor(List<SearchEngine> engines, CompletableFuture<AggregatorResult> futureResult, Duration timeout) {
        this.engines = engines;
        this.result = new AggregatorResult(new HashMap<>());
        this.futureResult = futureResult;
        context().system().scheduler().scheduleOnce(
                FiniteDuration.create(timeout.toMillis(), TimeUnit.MILLISECONDS),
                () -> self().tell(new TimeoutExcedeed(), self()),
                context().system().dispatcher());
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
            } else if (o instanceof TimeoutExcedeed) {
                finish();
            }
        }
    }

    public void finish() {
        finished = true;
        futureResult.complete(result);
    }
}
