package io.gbloch.stargazer.resource;

import io.smallrye.common.constraint.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Path("/api/v1")
public final class StargazerResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{user}/{repo}/starneighbours")
    public Response getStarNeighbours(
            @PathParam("user") @NotNull String user,
            @PathParam("repo") @NotNull String repo
    ) {
        return Response.status(200).build();
    }
}
