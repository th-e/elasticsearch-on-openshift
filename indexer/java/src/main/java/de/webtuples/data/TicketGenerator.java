package de.webtuples.data;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

final public class TicketGenerator {

    private static Random randomGenerator = new Random();
    private static NameGenerator nameGenerator = new NameGenerator();


    private TicketGenerator() {
    }


    public static XContentBuilder getRandomTicket() throws IOException {

        FakeValuesService fakerService = new FakeValuesService(
                new Locale("de-CH"), new RandomService());
        Faker faker = new Faker();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        // @formatter:off
        return jsonBuilder()
                .startObject()
                    .field("pointOfSale", String.valueOf(randomGenerator.nextInt(50) +1))
                    .field("time",faker.date().birthday() )
                    .startObject("productInfo")
                        .field("id", String.valueOf(randomGenerator.nextInt(50) + 100))
                        .field("from", "Hamburg")
                        .field("to", "Milano")
                    .endObject()
                    .startObject("utilizationTime")
                        .field("from", dateFormat.format(faker.date().future(10, TimeUnit.DAYS)))
                        .field("to", dateFormat.format(faker.date().future(16, TimeUnit.DAYS)))
                    .endObject()
                    .startObject("travelerInfo")
                        .field("lastname", nameGenerator.getRandomLastName())
                        .field("firstname", nameGenerator.getRandomFirstName())
                        .field("dateOfBirth", dateFormat.format(faker.date().birthday()) )
                    .endObject()
                    .startObject("price")
                        .field("value",  fakerService.numerify("##.#0"))
                        .field("currency", "CHF")
                    .endObject()
                .endObject();
        // @formatter:on
    }


}
