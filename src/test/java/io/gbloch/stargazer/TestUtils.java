package io.gbloch.stargazer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.gbloch.stargazer.client.GithubClient;
import io.gbloch.stargazer.client.Repo;
import io.gbloch.stargazer.client.Stargazer;
import java.util.LinkedHashSet;
import java.util.Set;

public final class TestUtils {

  public static final Stargazer USER_GBLOCH = new Stargazer("gbloch");
  public static final Stargazer USER_JD = new Stargazer("jd");
  public static final Repo REPO_STARGAZER = new Repo("gaetanBloch/stargazer");
  public static final Repo REPO_MERGIFY = new Repo("merfyio/mergify-engine");

  private TestUtils() {
    // Prevent instantiability
  }

  public static void initClientMock(GithubClient githubClient) {
    Set<Stargazer> stargazers = new LinkedHashSet<>();
    stargazers.add(TestUtils.USER_GBLOCH);
    stargazers.add(TestUtils.USER_JD);
    when(githubClient.getRepoStargazers(anyString(), anyString())).thenReturn(stargazers);
    Set<Repo> repos = new LinkedHashSet<>();
    repos.add(TestUtils.REPO_MERGIFY);
    repos.add(TestUtils.REPO_STARGAZER);
    when(githubClient.getUserStarredRepos(anyString()))
        .thenReturn(repos)
        .thenReturn(Set.of(TestUtils.REPO_MERGIFY));
  }
}
