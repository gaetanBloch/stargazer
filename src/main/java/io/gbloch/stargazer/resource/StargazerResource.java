package io.gbloch.stargazer.resource;

import io.gbloch.stargazer.dto.NeighbourRepoDto;
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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.client.api.WebClientApplicationException;

@Tag(name = "Starneighbours")
@RequiredArgsConstructor
@Path("/api/v1")
public final class StargazerResource {

    static final String SERVICE_UNAVAILABLE_MESSAGE = "Error while accessing Github API";
    static final String INTERNAL_SERVER_ERROR_MESSAGE = "Unexpected internal server error";

    private final StargazerService stargazerService;

    @GET
    @Operation(
            summary = "Get the star neighbours of a repository"
    )
    @APIResponse(
            responseCode = "OK",
            content = @Content(schema = @Schema(implementation = NeighbourRepoDto.class)))
    @APIResponse(
            responseCode = "NOT_FOUND",
            description = "The user or the repository could not be found on Github",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(
            responseCode = "SERVICE_UNAVAILABLE",
            description = "The Github API could not be reached",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Retry(delay = 1000, maxRetries = 3)
    @Fallback(fallbackMethod = "getStarNeighboursFallback", skipOn = ClientWebApplicationException.class)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{user}/{repo}/starneighbours")
    public Response getStarNeighbours(
            @Parameter(description = "Github user login") @PathParam("user") @NotNull String user,
            @Parameter(description = "Targeted repositiory") @PathParam("repo") @NotNull String repo
    ) {
        // Improvement: The results could be paged
        // Improvement: The number of neighbours could be limited
        try {
            return Response.status(200)
                    .entity(stargazerService.getStarNeighbours(user, repo))
                    .build();
        } catch (NotFoundException e) {
            return new ErrorResponse(e).toResponse();
        } catch (ClientWebApplicationException e) {
             return Response.status(Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse(SERVICE_UNAVAILABLE_MESSAGE, Status.SERVICE_UNAVAILABLE))
                    .build();
        }
    }

    /**
     * @return Empty collection and 503 error when we couldn't reach the Github API after retry
     */
    private Response getStarNeighboursFallback(String user, String repo) {
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(INTERNAL_SERVER_ERROR_MESSAGE, Status.INTERNAL_SERVER_ERROR))
                .build();
    }

    private record ErrorResponse(String message, Status status) {
        public ErrorResponse() {
            this(INTERNAL_SERVER_ERROR_MESSAGE, Status.INTERNAL_SERVER_ERROR);
        }

        public ErrorResponse(NotFoundException e) {
            this(e.getMessage(), Status.NOT_FOUND);
        }

        public Response toResponse() {
            return Response.status(status).entity(this).build();
        }
    }
}
