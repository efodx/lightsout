package lightsout.services;

import lightsout.dtos.PlayerDTO;
import lightsout.models.Player;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for storing and retrieving players.
 */
@ApplicationScoped
public class PlayersService {
    @Inject
    EntityManager em;

    /**
     * @return all players
     */
    public List<PlayerDTO> getPlayers() {
        Query query = em.createQuery("SELECT p FROM Player p", Player.class);
        return ((List<Player>) query.getResultList()).stream()
                .map(p -> new PlayerDTO(p.getUsername(), p.getAge()))
                .collect(Collectors.toList());
    }

    /**
     * @param username username
     * @return player with given username or null if it doesn't exist
     */
    public PlayerDTO getPlayerWithUsername(String username) {
        Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = ?1");
        query.setParameter(1, username);
        return ((List<Player>) query.getResultList()).stream().map(p -> new PlayerDTO(p.getUsername(), p.getAge()))
                .findFirst().orElse(null);
    }

    /**
     * Creates a new player in the database.
     *
     * @param username wanted username
     * @param age      player's age
     * @return persisted player
     * @throws IllegalArgumentException if a player with the given username already exists
     */
    @Transactional
    public PlayerDTO createPlayer(String username, int age) {
        if (age < 1) {
            throw new IllegalArgumentException("Age must be a positive integer.");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username is too short.");
        }
        if (username.length() > 12) {
            throw new IllegalArgumentException("Username is too long.");
        }
        PlayerDTO storedPlayer = getPlayerWithUsername(username);
        if (storedPlayer != null) {
            throw new IllegalArgumentException("Player with given username already exists.");
        }
        Player player = new Player();
        player.setUsername(username);
        player.setAge(age);
        em.persist(player);
        em.flush();
        return new PlayerDTO(player.getUsername(), player.getAge());
    }

    /**
     * Removes the player with username.
     * The action is idempotent and gives no feedback on whether anything was removed.
     *
     * @param username of the player to be removed
     */
    @Transactional
    public void removePlayer(String username) {
        Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = ?1");
        query.setParameter(1, username);
        ((List<Player>) query.getResultList()).stream().findFirst().ifPresent(storedPLayer -> em.remove(storedPLayer));
    }
}
