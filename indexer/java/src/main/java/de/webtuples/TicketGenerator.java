package de.webtuples;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Locale;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

final class TicketGenerator {

    private TicketGenerator(){}


    static XContentBuilder getRandomTicket() throws IOException {

        FakeValuesService faker = new FakeValuesService(
                new Locale("de-CH"), new RandomService());

        return jsonBuilder()
                .startObject()
                .startObject("productInfo")
                .field("number", "timestamp")
                .field("from", "Hamburg")
                .field("to", "Milano")
                .startObject("price")
                .startObject("amount")
                .field("value", "11.7")
                .endObject()
                .endObject()
                .startObject("utilizationTime")
                .field("from", "2016-11-01T00:00:00")
                .field("to", faker.numerify("20##-##-##T00:00:00"))
                .endObject()
                .endObject()
                .endObject();
    }


}
