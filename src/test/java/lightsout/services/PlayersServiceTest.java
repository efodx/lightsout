package lightsout.services;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import lightsout.dtos.PlayerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class PlayersServiceTest {
    @Inject
    PlayersService playersService;

    @Test
    public void testGetPlayers() {
        Assertions.assertEquals(new ArrayList<PlayerDTO>(), playersService.getPlayers());
    }


    @Test
    @TestTransaction
    public void testCreatePlayer() {
        String username = "testPlayer";
        int age = 12;

        playersService.createPlayer(username, age);

        List<PlayerDTO> players = playersService.getPlayers();
        Assertions.assertEquals(1, players.size());
        PlayerDTO createdPlayer = players.get(0);
        Assertions.assertEquals(createdPlayer.getUsername(), username);
        Assertions.assertEquals(createdPlayer.getAge(), age);
    }

    @Test
    @TestTransaction
    public void testCreatePlayerWithSameUsernameThrowsException() {
        String username = "testPlayer";

        playersService.createPlayer(username, 14);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playersService.createPlayer(username, 12));
    }

    @Test
    @TestTransaction
    public void testCreatePlayerWithUsernameTooShortThrowsException() {
        String username = "ay";

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playersService.createPlayer(username, 12));
    }

    @Test
    @TestTransaction
    public void testCreatePlayerWithUsernameTooLongThrowsException() {
        String username = "ayyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyLmao";

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playersService.createPlayer(username, 12));
    }

    @Test
    @TestTransaction
    public void testCreatePlayerWithNegativeAgeThrowsException() {
        String username = "testPlayer";

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playersService.createPlayer(username, -2));
    }

    @Test
    @TestTransaction
    public void testGetPlayerWithUsername() {
        String username = "testPlayer";
        int age = 12;
        playersService.createPlayer(username, age);

        PlayerDTO receivedPlayer = playersService.getPlayerWithUsername(username);

        Assertions.assertEquals(receivedPlayer.getUsername(), username);
        Assertions.assertEquals(receivedPlayer.getAge(), age);
    }

    @Test
    public void testGetPlayerWithUsernameReturnsNullWhenNotExists() {
        PlayerDTO playerDTO = playersService.getPlayerWithUsername("randomName");

        Assertions.assertNull(playerDTO);
    }
}
