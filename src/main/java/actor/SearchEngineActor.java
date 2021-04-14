package actor;

import akka.actor.UntypedActor;
import engine.SearchEngine;
import lombok.AllArgsConstructor;
import utils.SearchRequest;

@AllArgsConstructor
public class SearchEngineActor extends UntypedActor {
    SearchEngine engine;

    @Override
    public void onReceive(Object o) {
        if (o instanceof SearchRequest) {
            context().sender().tell(engine.search(((SearchRequest) o).getRequest()), context().self());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
