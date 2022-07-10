package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    private final LineCreationRequest sinbundangLineCreationRequest = new LineCreationRequest(
            "신분당선", "bg-red-600", 1L, 2L, 10L);
    private final LineCreationRequest bundangLineCreationRequest = new LineCreationRequest(
            "분당선", "bg-green-600", 1L, 3L, 20L);

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void canFindTheLineCreatedWhenLineWasCreated() {
        // when
        var creationResponse = createLine(sinbundangLineCreationRequest);

        // then
        var lineNames = RestAssured
                .when()
                    .get("/lines")
                .then()
                    .extract().jsonPath().getList("name", String.class);

        assertAll(
                () -> assertThat(creationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lineNames).containsExactlyInAnyOrder(sinbundangLineCreationRequest.getName())
        );
    }

    private ExtractableResponse<Response> createLine(LineCreationRequest creationRequest) {
        return RestAssured
                .given()
                    .body(creationRequest, ObjectMapperType.JACKSON_2)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/lines")
                .then()
                    .extract();
    }
}
