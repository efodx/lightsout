package lightsout.resources;

import lightsout.dtos.PlayerDTO;
import lightsout.services.PlayersService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayersResource {
    @Inject
    PlayersService playersService;

    @Operation(description = "Gets all players.",
            summary = "Get all players.")
    @APIResponse(
            responseCode = "200",
            description = "All players.",
            content = @Content(schema = @Schema(implementation = PlayerDTO.class, type = SchemaType.ARRAY))
    )
    @GET
    public List<PlayerDTO> getPlayers() {
        return playersService.getPlayers();
    }

    @Operation(description = "Creates a player with the desired username and age. The username must be between 3 and 12 characters long (inclusive)." +
            "The age must be a positive integer. The id in the body is omitted.",
            summary = "Create a player with the desired username and age.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Created player.",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User with desired username already exists."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Username or age are not in the correct format."
            )
    })
    @POST
    public PlayerDTO createPLayer(PlayerDTO playerDTO) {
        try {
            return playersService.createPlayer(playerDTO.getUsername(), playerDTO.getAge());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build());
        }
    }

    @Operation(description = "Gets the player with the desired username.",
            summary = "Get player with the desired username.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Player with the desired username.",
                    content = @Content(schema = @Schema(implementation = PlayerDTO.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User with the given username was not found."
            )
    })
    @GET
    @Path("/{username}")
    public PlayerDTO getPlayerByUsername(@PathParam("username") String username) {
        PlayerDTO player = playersService.getPlayerWithUsername(username);
        if (player == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity("User with the given username was not found.").build());
        }
        return player;
    }
}