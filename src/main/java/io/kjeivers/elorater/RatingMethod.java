package io.kjeivers.elorater;

/**
 */
public interface RatingMethod {
    /*
         * @param player
         * @param opponent
         * @param game
         * @return
         */
    int calculateNewRating(Player player, Player opponent, Game game);
}
