package io.kjeivers.elorater;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

/**
 */
public class RatingGroupTest {

    static Logger log = Logger.getLogger("RatingGroupTest") ;

    Player p1 = new Player(UUID.fromString("0d9ab01f-f76d-45d4-8ac3-3423cd2b9555"),
            "Player 1", 1000);
    Player p2 = new Player(UUID.fromString("ff1d6120-ab0f-4e52-87c7-d2bb2d0a3502"),
            "Player 2", 1000);
    Player p3 = new Player(UUID.fromString("0d21b461-d4e3-4797-b618-0b395f8a2c42"),
            "Player 3", 1000);

    @Test
    public void testEloRating() {
        Player a = p1.withRating(1583);
        Player b = p2.withRating(1572);
        log.info(String.format("Before a : %s", a.getRating()));
        log.info(String.format("Before b : %s", b.getRating()));

        Game game = Game.builder()
                .withWhite(a)
                .withBlack(b)
                .withResult(Result.WHITE)
                .build();
        RatingGroup group = new RatingGroup(new EloRating(15))
                .withPlayers(a, b)
                .addGame(game);
        log.info(String.format("Game result: %s", game.getResult())) ;
        Player a_updated = group.getPlayer(p1.getId());
        Player b_updated = group.getPlayer(p2.getId());
        log.info(String.format("After a : %s", a_updated.getRating()));
        log.info(String.format("After b : %s", b_updated.getRating()));
        Assertions.assertThat(a_updated.getRating()).isEqualTo(1590);
        Assertions.assertThat(b_updated.getRating()).isEqualTo(1565);
    }
}
