package example.cashcard;


import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;


import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    // @BeforeEach annotation is used to signal that the annotated method should be executed before each invocation of @Test
    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "sarah1"),
                new CashCard(100L, 1.00, "sarah1"),
                new CashCard(101L, 150.00, "sarah1"));
    }

    @Test
    public void cashCardSerializationTest() throws IOException {
        CashCard cashCard = cashCards[0];
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }

    @Test
    public void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(cashCards[0]);
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }


    @Test
    void cashCardListSerializationTest() throws IOException {
        //It serializes the cashCards variable into JSON, then asserts that list.json should contain the same data as the serialized cashCards variable
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
        assertThat(jsonList.write(cashCards)).hasJsonPathNumberValue("@[0].id");
        assertThat(jsonList.write(cashCards)).extractingJsonPathNumberValue("@[0].id")
                .isEqualTo(99);
        assertThat(jsonList.write(cashCards)).hasJsonPathNumberValue("@[0].amount");
        assertThat(jsonList.write(cashCards)).extractingJsonPathNumberValue("@[0].amount")
                .isEqualTo(123.45);

        assertThat(jsonList.write(cashCards)).hasJsonPathNumberValue("@[1].id");
        assertThat(jsonList.write(cashCards)).extractingJsonPathNumberValue("@[1].id")
                .isEqualTo(100);
        assertThat(jsonList.write(cashCards)).hasJsonPathNumberValue("@[1].amount");
        assertThat(jsonList.write(cashCards)).extractingJsonPathNumberValue("@[1].amount")
                .isEqualTo(1.00);
    }


    @Test
    public void cashCardListDeserializationTest() throws IOException {
        String expected="""
                [
                    {"id": 99, "amount": 123.45 , "owner": "sarah1"},
                    {"id": 100, "amount": 1.00 , "owner": "sarah1"},
                    {"id": 101, "amount": 150.00, "owner": "sarah1"}
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
        assertThat(jsonList.parseObject(expected)[0].id()).isEqualTo(99);
        assertThat(jsonList.parseObject(expected)[0].amount()).isEqualTo(123.45);
        assertThat(jsonList.parseObject(expected)[1].id()).isEqualTo(100);
        assertThat(jsonList.parseObject(expected)[1].amount()).isEqualTo(1.00);
    }




}

