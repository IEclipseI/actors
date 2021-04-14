import aggragator.SearchAggregator;
import engine.SearchEngine;
import engine.SearchEngineFake;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import scala.concurrent.duration.Duration;
import server.SearchHttpServerFake;
import utils.AggregatorResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class SearchEngineTest {
    @Spy
    private final SearchHttpServerFake httpServer = new SearchHttpServerFake();

    private List<SearchEngine> engines;

    @Before
    public void before() {
        engines = List.of(
                new SearchEngineFake(httpServer, "google"),
                new SearchEngineFake(httpServer, "yandex"),
                new SearchEngineFake(httpServer, "bing")
        );
    }

    @Test
    public void test() {
        SearchAggregator searchAggregator = new SearchAggregator(engines);

        AggregatorResult hello = searchAggregator.search("hello", Duration.create(5, TimeUnit.SECONDS));

        assertThat(hello.getResult()).size().isEqualTo(3);

        hello.getResult().forEach((host, res) -> System.out.println(host + ": " + res));
    }

    @Test
    public void testNotAllReceived() {
        Mockito.when(httpServer.search(anyString(), anyString())).thenAnswer(i -> {
                    if (i.getArgumentAt(0, String.class).equals("bing")) {
                        Thread.sleep(4000);
                    } else {
                        Thread.sleep(1000);
                    }
                    return i.callRealMethod();
                }
        );
        SearchAggregator searchAggregator = new SearchAggregator(engines);
        AggregatorResult qq = searchAggregator.search("qq", Duration.create(2, TimeUnit.SECONDS));
        assertThat(qq.getResult()).size().isEqualTo(2);
        assertThat(qq.getResult().get("google")).isNotNull();
        assertThat(qq.getResult().get("yandex")).isNotNull();
        assertThat(qq.getResult().get("bing")).isNull();

        assertThat(qq.getResult().get("google")).isNotEmpty();
        assertThat(qq.getResult().get("yandex")).isNotEmpty();

    }
}
