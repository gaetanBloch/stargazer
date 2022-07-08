package io.gbloch.stargazer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.gbloch.stargazer.TestUtils;
import io.gbloch.stargazer.client.GithubClient;
import io.gbloch.stargazer.dto.NeighbourRepoDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
final class StargazerServiceTest {

    private static final String DUMMY_REPO = "foo";

    @InjectMock
    @RestClient
    GithubClient githubClient;

    @Inject
    StargazerService stargazerService;

    @Test
    void should_notGetStarNeighbours_when_repoIsNotStarred() {
        // Given
        when(githubClient.getRepoStargazers(anyString(), anyString())).thenReturn(Set.of());
        // When
        Set<NeighbourRepoDto> neighbours =
                stargazerService.getStarNeighbours(TestUtils.USER_GBLOCH.login(), DUMMY_REPO);

        // Then
        assertThat(neighbours).isEmpty();
    }

    @Test
    void should_getoneStarNeigbhour_when_repoIsStarredOnce() {
        // Given
        when(githubClient.getRepoStargazers(anyString(), anyString()))
                .thenReturn(Set.of(TestUtils.USER_GBLOCH));
        when(githubClient.getUserStarredRepos(anyString())).thenReturn(
                Set.of(TestUtils.REPO_MERGIFY));

        // When
        List<NeighbourRepoDto> neighbours =
                new ArrayList<>(
                        stargazerService.getStarNeighbours(TestUtils.USER_GBLOCH.login(),
                                DUMMY_REPO));

        // Then
        assertThat(neighbours).hasSize(1);
        assertThat(neighbours.get(0).repo()).isEqualTo(TestUtils.REPO_MERGIFY.full_name());
        assertThat(neighbours.get(0).stargazers()).hasSize(1)
                .contains(TestUtils.USER_GBLOCH.login());
    }

    @Test
    void should_getoneStarNeigbhour_when_repoIsStarredTwiceWithSameRepo() {
        // Given
        when(githubClient.getRepoStargazers(anyString(), anyString()))
                .thenReturn(Set.of(TestUtils.USER_GBLOCH, TestUtils.USER_JD));
        when(githubClient.getUserStarredRepos(anyString()))
                .thenReturn(Set.of(TestUtils.REPO_MERGIFY))
                .thenReturn(Set.of(TestUtils.REPO_MERGIFY));

        // When
        List<NeighbourRepoDto> neighbours =
                new ArrayList<>(
                        stargazerService.getStarNeighbours(TestUtils.USER_GBLOCH.login(),
                                DUMMY_REPO));

        // Then
        assertThat(neighbours).hasSize(1);
        assertThat(neighbours.get(0).repo()).isEqualTo(TestUtils.REPO_MERGIFY.full_name());
        assertThat(neighbours.get(0).stargazers())
                .hasSize(2)
                .contains(TestUtils.USER_GBLOCH.login(), TestUtils.USER_JD.login());
    }

    @Test
    void should_getSeveralStarNeighbours_when_repoIsStarredSeveralTimesWithSeveralRepos() {
        // Given
        TestUtils.initClientMock(githubClient);

        // When
        List<NeighbourRepoDto> neighbours =
                new ArrayList<>(
                        stargazerService.getStarNeighbours(TestUtils.USER_GBLOCH.login(),
                                DUMMY_REPO));

        // Then
        assertThat(neighbours).hasSize(2);
        assertThat(neighbours.get(0).repo()).isEqualTo(TestUtils.REPO_MERGIFY.full_name());
        assertThat(neighbours.get(0).stargazers())
                .hasSize(2)
                .contains(TestUtils.USER_GBLOCH.login(), TestUtils.USER_JD.login());
        assertThat(neighbours.get(1).repo()).isEqualTo(TestUtils.REPO_STARGAZER.full_name());
        assertThat(neighbours.get(1).stargazers()).hasSize(1)
                .contains(TestUtils.USER_GBLOCH.login());
    }

    @Test
    void should_notGetStarNeighbour_when_sameRequestedRepo() {
        // Given
        when(githubClient.getRepoStargazers(anyString(), anyString()))
                .thenReturn(Set.of(TestUtils.USER_GBLOCH, TestUtils.USER_JD));
        when(githubClient.getUserStarredRepos(anyString()))
                .thenReturn(Set.of(TestUtils.REPO_MERGIFY))
                .thenReturn(Set.of(TestUtils.REPO_MERGIFY));

        // When
        Set<NeighbourRepoDto> neighbours =
                stargazerService.getStarNeighbours(
                        TestUtils.USER_GBLOCH.login(), TestUtils.REPO_MERGIFY.full_name());

        // Then
        assertThat(neighbours).isEmpty();
    }

    @Test
    void should_throwNotFoundException_when_userOrRepoIsNotFound() {
        // Given
        doThrow(new ClientWebApplicationException(Response.status(Status.NOT_FOUND).build()))
                .when(githubClient)
                .getRepoStargazers(anyString(), anyString());

        // When Then
        assertThatThrownBy(() -> stargazerService.getStarNeighbours(
                TestUtils.USER_GBLOCH.login(),
                TestUtils.REPO_MERGIFY.full_name()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        StargazerService.NOT_FOUND_MESSAGE,
                        TestUtils.USER_GBLOCH.login(),
                        TestUtils.REPO_MERGIFY.full_name()
                );
    }
}
