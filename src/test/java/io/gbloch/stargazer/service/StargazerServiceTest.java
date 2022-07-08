package io.gbloch.stargazer.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.gbloch.stargazer.dto.NeighbourRepoDto;
import io.quarkus.test.junit.QuarkusTest;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
final class StargazerServiceTest {

    @Inject
    StargazerService stargazerService;

    @Test
    void should_getSeveralStarNeighbours_when_repoIsStarredSeveralTimesWithSeveralRepos() {
        // Given

        // When
        List<NeighbourRepoDto> neighbours = new ArrayList<>(
                stargazerService.getStarNeighbours("foo", "bar"));

        // Then
        assertThat(neighbours).hasSize(1);
        assertThat(neighbours.get(0).repo()).isEqualTo("foo");
        assertThat(neighbours.get(0).stargazers())
                .hasSize(2)
                .contains("bar", "baz");
    }
}
