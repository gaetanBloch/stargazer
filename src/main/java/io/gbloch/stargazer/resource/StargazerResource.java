package io.gbloch.stargazer.resource;

import io.smallrye.common.constraint.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Starneighbours")
@RequiredArgsConstructor
@Path("/api/v1")
public final class StargazerResource {

    @GET
    @Operation(summary = "Get the star neighbours of a repository")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{user}/{repo}/starneighbours")
    public Response getStarNeighbours(
            @PathParam("user") @NotNull String user,
            @PathParam("repo") @NotNull String repo
    ) {
        return Response.status(200).build();
    }
}
