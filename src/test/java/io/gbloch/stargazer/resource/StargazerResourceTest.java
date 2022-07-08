package io.gbloch.stargazer.resource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.gbloch.stargazer.TestUtils;
import io.gbloch.stargazer.client.GithubClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
final class StargazerResourceTest {

    @InjectMock
    @RestClient
    GithubClient githubClient;

    @SuppressWarnings("unchecked")
    @Test
    void should_getStarNeighbours_when_repoIsStarred() {
        // Given
        TestUtils.initClientMock(githubClient);

        // When
        List<Map<String, Object>> neighbours =
                given()
                        .when()
                        // The URL needs to be valid but not the user and repo values because the client is
                        // mocked
                        .get("/api/v1/foo/bar/starneighbours")
                        .then()
                        .statusCode(200)
                        .body("size()", is(2))
                        .extract()
                        .as(List.class);

        // Then
        assertThat(neighbours.get(0)).containsEntry("repo", TestUtils.REPO_MERGIFY.full_name());
        List<String> mergifyStargazers = (List<String>) neighbours.get(0).get("stargazers");
        assertThat(mergifyStargazers)
                .hasSize(2)
                .contains(TestUtils.USER_GBLOCH.login(), TestUtils.USER_JD.login());
        assertThat(neighbours.get(1)).containsEntry("repo", TestUtils.REPO_STARGAZER.full_name());
        List<String> stargazerStargazers = (List<String>) neighbours.get(1).get("stargazers");
        assertThat(stargazerStargazers).hasSize(1).contains(TestUtils.USER_GBLOCH.login());
    }

    @Test
    void should_notGetStarNeighbours_when_repoIsNotStarred() {
        // Given
        when(githubClient.getRepoStargazers(anyString(), anyString())).thenReturn(Set.of());

        // When Then
        given()
                .when()
                // The URL needs to be valid but not the user and repo values because the client is
                // mocked
                .get("/api/v1/foo/bar/starneighbours")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void should_returnNotFound_when_userOrRepoIsNotFound() {
        // Given
        doThrow(new NotFoundException("baz"))
                .when(githubClient)
                .getRepoStargazers(anyString(), anyString());

        // When Then
        given()
                .when()
                // The URL needs to be valid but not the user and repo values because the client is
                // mocked
                .get("/api/v1/foo/bar/starneighbours")
                .then()
                .statusCode(404)
                .body("message", equalTo("baz"))
                .body("status", equalTo("NOT_FOUND"));
    }
}
