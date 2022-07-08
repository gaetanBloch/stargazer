package io.gbloch.stargazer.service;

import io.gbloch.stargazer.client.GithubClient;
import io.gbloch.stargazer.client.Repo;
import io.gbloch.stargazer.client.Stargazer;
import io.gbloch.stargazer.dto.NeighbourRepoDto;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class StargazerService {

    @Inject
    @RestClient
    GithubClient githubClient;

    public Set<NeighbourRepoDto> getStarNeighbours(String user, String repo) {
        return doGetStarNeighbours(user, repo);
    }

    private Set<NeighbourRepoDto> doGetStarNeighbours(String user, String repo) {
        return Set.of(new NeighbourRepoDto("foo", Set.of("bar", "baz")));
    }
}
