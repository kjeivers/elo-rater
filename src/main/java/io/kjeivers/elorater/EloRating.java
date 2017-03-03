package io.kjeivers.elorater;

/**
 function get_new_rating( $my_old, $opp_old, &$my_new, $s )
 {
 //print( "\n\nmy_old = $my_old\nopp_old = $opp_old\ns = $s\n");

 $dr = $opp_old - $my_old;
 $s_expected = 1.0 / ( pow(10, ($dr / 400.0)) + 1 );

 $k = 16;

 $change = round( $k * ($s - $s_expected) );
 $my_new = $my_old + $change;

 return true;
 }
 */

public class EloRating implements RatingMethod {

    private final double k_factor;

    public EloRating(double k_factor) {
        this.k_factor = k_factor;
    }

    /*
     * @param player
     * @param opponent
     * @param game
     * @return
     */
    @Override
    public int calculateNewRating(Player player, Player opponent, Game game) {
        int diff = opponent.getRating() - player.getRating();

        double expectedResult = 1.0 / (1.0 + Math.pow(10, (diff / 400.0))); // percent chance of win
        double roundedExpectedResult = Math.round(expectedResult * 100) / 100.0;
        double result;
        switch (game.getResult()) {
            case WHITE:
                result = game.isWhite(player) ?  1 : 0;
                break;
            case BLACK:
                result = game.isBlack(player) ?  1 : 0;
                break;
            case DRAW:
                result = 0.5;
                break;
            default:
                throw new RuntimeException("Could not calculate victor of game");
        }
        double change = k_factor * (result - roundedExpectedResult);
        double newRating = player.getRating() + change;
        return (int)(Math.round(newRating));
    }
}
