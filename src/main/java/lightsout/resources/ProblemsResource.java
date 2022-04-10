package lightsout.resources;

import lightsout.dtos.ProblemDTO;
import lightsout.services.ProblemsService;
import lightsout.utilities.solver.UnsolvableException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/problems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProblemsResource {

    @Inject
    ProblemsService problemsService;

    @Operation(description = "Gets all problems.",
            summary = "Get all problems.")
    @APIResponse(
            responseCode = "200",
            description = "All problems.",
            content = @Content(schema = @Schema(implementation = ProblemDTO.class, type = SchemaType.ARRAY))
    )
    @GET
    public List<ProblemDTO> getProblems() {
        return problemsService.getProblems();
    }

    @Operation(description = "Gets all problems created by user with given username.",
            summary = "Get all problems created by user with given username.")
    @APIResponse(
            responseCode = "200",
            description = "All problems created by user with given username.",
            content = @Content(schema = @Schema(implementation = ProblemDTO.class, type = SchemaType.ARRAY))
    )
    @Path("creator/{username}")
    @GET
    public List<ProblemDTO> getProblemsByCreator(@PathParam("username") String username) {
        return problemsService.getProblemsCreatedBy(username);
    }

    @Operation(description = "Gets problem with the given id.",
            summary = "Get problem with the given id.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Problem with the given id.",
                    content = @Content(schema = @Schema(implementation = ProblemDTO.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Problem with the given id was not found."
            )
    })
    @Path("{id}")
    @GET
    public ProblemDTO getProblemById(@PathParam("id") int id) {
        ProblemDTO problem = problemsService.getProblemById(id);
        if (problem == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Problem with given id was not found.").build());
        }
        return problem;
    }

    @Operation(description = "Adds a new problem. The problem must be given as a nxn grid of 0s and 1s, " +
            "where n > 2 and n < 9. The id in body is omitted.",
            summary = "Add a new problem.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Persisted problem.",
                    content = @Content(schema = @Schema(implementation = ProblemDTO.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problem was not in the correct format."
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problem was not solvable."
            )
    })
    @POST
    public ProblemDTO addProblem(ProblemDTO problemDTO) {
        try {
            return problemsService.addProblem(problemDTO.getGrid(), problemDTO.getCreatedByUsername());
        } catch (UnsolvableException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("The given problem is unsolvable.").build());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build());
        }
    }


}
