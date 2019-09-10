package de.webtuples;

import de.webtuples.data.TicketGenerator;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Indexer {

    private static final String ES_HOST = "localhost";
    private static final String INDEX_NAME = "tickets";

    private static boolean noSuchIndex(RestHighLevelClient client, String indexName) throws IOException {
        Response response = client.getLowLevelClient().performRequest(new Request("HEAD", "/" + indexName));
        return 404 == response.getStatusLine().getStatusCode();
    }

    public static void main(String[] args) {

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(ES_HOST, 9200, "http")));

        try {
            if (noSuchIndex(client, INDEX_NAME)) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);
                createIndexRequest.settings(Settings.builder()
                        .put("index.number_of_shards", 3)
                        .put("index.number_of_replicas", 0)
                );
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                System.out.println("Index created");
            } else {
                System.out.println("Index already exists");
            }

            BulkProcessor.Listener listener = new BulkProcessor.Listener() {
                int count = 0;

                @Override
                public void beforeBulk(long l, BulkRequest bulkRequest) {
                    count = count + bulkRequest.numberOfActions();
                    System.out.println("Uploaded " + count + " documents");
                }

                @Override
                public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                    if (bulkResponse.hasFailures()) {
                        for (BulkItemResponse bulkItemResponse : bulkResponse) {
                            if (bulkItemResponse.isFailed()) {
                                System.out.println(bulkItemResponse.getOpType());
                                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                                System.out.println("Failure " + failure.toString());
                            }
                        }
                    }
                }

                @Override
                public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                    System.out.println("Exceptions " + throwable.toString());
                }
            };

            // see https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.3/java-rest-high-document-bulk.html#java-rest-high-document-bulk-processor
            BulkProcessor bulkProcessor = BulkProcessor.builder(
                    (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener), listener)
                    .setBulkActions(100000)
                    .setBulkSize(new ByteSizeValue(50L, ByteSizeUnit.MB))
                    .setConcurrentRequests(0)
                    .setFlushInterval(TimeValue.timeValueSeconds(10L))
                    .setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3))
                    .build();

            for (int i = 0; i < 1000*1000*500; i++) {
                IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                        .id(String.valueOf(i))
                        .source(TicketGenerator.getRandomTicket());
                bulkProcessor.add(indexRequest);
            }
            boolean terminated = bulkProcessor.awaitClose(120L, TimeUnit.SECONDS);
            System.out.println("terminated? " + terminated);
            Thread.sleep(120000);
            client.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
