package io.gbloch.stargazer;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
final class StargazerResourceTest {

    @Test
    void should_getStarNeighbours_when_repoIsStarred() {
        given()
                .when()
                .get("/api/v1/foo/bar/starneighbours")
                .then()
                .statusCode(200);
    }
}
