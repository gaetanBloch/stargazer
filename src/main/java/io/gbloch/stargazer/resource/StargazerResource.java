package io.gbloch.stargazer.resource;

import io.gbloch.stargazer.service.StargazerService;
import io.smallrye.common.constraint.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Starneighbours")
@RequiredArgsConstructor
@Path("/api/v1")
public final class StargazerResource {

    private final StargazerService stargazerService;

    @GET
    @Operation(summary = "Get the star neighbours of a repository")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{user}/{repo}/starneighbours")
    public Response getStarNeighbours(
            @PathParam("user") @NotNull String user,
            @PathParam("repo") @NotNull String repo
    ) {
        try {
            return Response.status(200)
                    .entity(stargazerService.getStarNeighbours(user, repo))
                    .build();
        } catch (NotFoundException e) {
            return new ErrorResponse(e).toResponse();
        }
    }

    private record ErrorResponse(String message, Status status) {

        public ErrorResponse(NotFoundException e) {
            this(e.getMessage(), Status.NOT_FOUND);
        }

        public Response toResponse() {
            return Response.status(status).entity(this).build();
        }
    }
}
