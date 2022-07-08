package io.gbloch.stargazer.resource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
final class StargazerResourceTest {

    @SuppressWarnings("unchecked")
    @Test
    void should_getStarNeighbours_when_repoIsStarred() {
        // Given

        // When
        List<Map<String, Object>> neighbours = given()
                .when()
                .get("/api/v1/foo/bar/starneighbours")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .extract()
                .as(List.class);

        // Then
        assertThat(neighbours.get(0)).containsEntry("repo", "foo");
        List<String> mergifyStargazers = (List<String>) neighbours.get(0).get("stargazers");
        assertThat(mergifyStargazers)
                .hasSize(2)
                .contains("bar", "baz");
    }
}
