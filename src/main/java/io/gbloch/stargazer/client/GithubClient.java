package io.gbloch.stargazer.client;

import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "github-api")
public interface GithubClient {

  @GET
  @Path("repos/{user}/{repo}/stargazers")
  Set<Stargazer> getRepoStargazers(@PathParam("user") String user, @PathParam("repo") String repo);

  @GET
  @Path("users/{user}/starred")
  Set<Repo> getUserStarredRepos(@PathParam("user") String user);
}
