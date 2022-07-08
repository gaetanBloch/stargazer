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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public final class StargazerService {

    public static final String NOT_FOUND_MESSAGE =
            "The user [%s] or the repository [%s] could not be found";

    @Inject
    @RestClient
    GithubClient githubClient;

    public Set<NeighbourRepoDto> getStarNeighbours(String user, String repo) {
        try {
            return doGetStarNeighbours(user, repo);
        } catch (ClientWebApplicationException e) {
            // Handle Not found exception more elegantly
            if (e.getResponse().getStatus() == Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, user, repo));
            }
            throw e;
        }
    }

    private Set<NeighbourRepoDto> doGetStarNeighbours(String user, String repo) {
        final var neighbourRepos = new LinkedHashMap<String, NeighbourRepoDto>();
        final Set<Stargazer> stargazers = githubClient.getRepoStargazers(user, repo);
        stargazers.forEach(stargazer -> addStarredRepos(stargazer, neighbourRepos));
        // Do not put requested repository in the list of neighbours
        neighbourRepos.remove(repo);
        // Improvement: We could re-order the Set with closest
        // neighbours first (biggest number of stargazers)
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
