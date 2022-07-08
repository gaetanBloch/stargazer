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
        final var neighbourRepos = new LinkedHashMap<String, NeighbourRepoDto>();
        final Set<Stargazer> stargazers = githubClient.getRepoStargazers(user, repo);
        stargazers.forEach(stargazer -> addStarredRepos(stargazer, neighbourRepos));
        // Do not put requested repository in the list of neighbours
        neighbourRepos.remove(repo);
        return new LinkedHashSet<>(neighbourRepos.values());
    }

    private void addStarredRepos(
            Stargazer stargazer,
            Map<String, NeighbourRepoDto> neighbourRepos
    ) {
        final Set<Repo> starredRepos = githubClient.getUserStarredRepos(stargazer.login());
        starredRepos.forEach(
                starredRepo -> addStarredRepoInNeighbours(stargazer, starredRepo, neighbourRepos)
        );
    }

    private void addStarredRepoInNeighbours(
            Stargazer stargazer,
            Repo starredRepo,
            Map<String, NeighbourRepoDto> neighbourRepos
    ) {
        String repoFullName = starredRepo.full_name();
        if (!neighbourRepos.containsKey(repoFullName)) {
            neighbourRepos.put(repoFullName, new NeighbourRepoDto(repoFullName, new HashSet<>()));
        }
        neighbourRepos.get(repoFullName).stargazers().add(stargazer.login());
    }
}
