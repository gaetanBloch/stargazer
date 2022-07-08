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
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Starneighbours")
@RequiredArgsConstructor
@Path("/api/v1")
public final class StargazerResource {

    static final String SERVICE_UNAVAILABLE_MESSAGE = "Error while accessing Github API";

    private final StargazerService stargazerService;

    @GET
    @Operation(summary = "Get the star neighbours of a repository")
    @Retry(delay = 1000, maxRetries = 3)
    @Fallback(fallbackMethod = "getStarNeighboursFallback")
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

    /**
     * @return Empty collection and 503 error when we couldn't reach the Github API after retry
     */
    private Response getStarNeighboursFallback(String user, String repo) {
        return Response.status(Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorResponse(SERVICE_UNAVAILABLE_MESSAGE, Status.SERVICE_UNAVAILABLE))
                .build();
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
