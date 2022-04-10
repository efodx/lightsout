package lightsout.resources;

import lightsout.dtos.ProblemSolutionDTO;
import lightsout.services.SolutionsService;
import lightsout.utilities.solutionchecker.NotASolutionException;
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

/**
 * Service for adding and retrieving solutions.
 */
@Path("/solutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolutionsResource {
    @Inject
    SolutionsService solutionsService;

    @Operation(description = "Gets all solutions.",
            summary = "Get all solutions.")
    @APIResponse(
            responseCode = "200",
            description = "All solutions.",
            content = @Content(schema = @Schema(implementation = ProblemSolutionDTO.class, type = SchemaType.ARRAY))
    )
    @GET
    public List<ProblemSolutionDTO> getSolutions() {
        return solutionsService.getSolutions();
    }

    @Operation(description = "Gets solutions by desired user.",
            summary = "Get solutions by desired user.")
    @APIResponse(
            responseCode = "200",
            description = "Solutions by the desired user.",
            content = @Content(schema = @Schema(implementation = ProblemSolutionDTO.class, type = SchemaType.ARRAY))
    )
    @GET
    @Path("solver/{solverUsername}")
    public List<ProblemSolutionDTO> getSolutionsByUser(@PathParam("solverUsername") String solverUsername) {
        return solutionsService.getSolutionsByUser(solverUsername);
    }

    @Operation(description = "Gets solutions to the problem with the given id.",
            summary = "Get solutions to the problem with the given id.")
    @APIResponse(
            responseCode = "200",
            description = "Solutions to the problem with the given id.",
            content = @Content(schema = @Schema(implementation = ProblemSolutionDTO.class, type = SchemaType.ARRAY))
    )
    @GET
    @Path("problem/{problemId}")
    public List<ProblemSolutionDTO> getSolutionsForProblem(@PathParam("problemId") Integer problemId) {
        return solutionsService.getSolutionsForProblem(problemId);
    }

    @Operation(description = "Adds a new problem solution. Solution is represented as an array of grid " +
            "field numbers that need to be pressed, to solve the game. The numbering starts with 0 and is row-oriented.",
            summary = "Add a new problem solution.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "ProblemSolution with the given id."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Problem was not not found in the database."
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Given solution is incorrect."
            )
    })
    @POST
    public void addSolutionForProblem(ProblemSolutionDTO problemSolution) {
        try {
            solutionsService.addProblemSolution(problemSolution.getProblemId(),
                    problemSolution.getSolution(), problemSolution.getSolverUsername());
        } catch (NotASolutionException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("The provided solution was incorrect.").build());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("The provided problem does not exist in the database.").build());
        }
    }

}
