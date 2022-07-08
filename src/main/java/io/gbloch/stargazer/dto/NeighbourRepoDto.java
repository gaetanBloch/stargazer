package io.gbloch.stargazer.dto;

import java.util.Set;

public record NeighbourRepoDto(String repo, Set<String> stargazers) {}
