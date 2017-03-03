package io.kjeivers.elorater;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: kjeivers
 * Date: 03.03.2017
 */
public class RatingGroup {
    private final ImmutableMap<UUID, Player> players;
    private final ImmutableSet<Game> games;
    private final RatingMethod ratingMethod;

    public RatingGroup(RatingMethod ratingMethod) {
        this(ImmutableMap.of(), ImmutableSet.of(), ratingMethod);
    }

    public RatingGroup(ImmutableMap<UUID, Player> players, ImmutableSet<Game> games, RatingMethod ratingMethod) {
        this.players = players;
        this.games = games;
        this.ratingMethod = ratingMethod;
    }

    public RatingGroup withPlayers(Player ... players) {
        return withPlayers(Arrays.asList(players));
    }

    public RatingGroup withPlayers(Collection<Player> players) {
        return new RatingGroup(toPlayerMap(players), games, ratingMethod);
    }

    public RatingGroup addGame(Game game) {
        Player white = players.get(game.getWhite());
        Player black = players.get(game.getBlack());
        Player updatedWhite = white.withRating(ratingMethod.calculateNewRating(white, black, game));
        Player updatedBlack = black.withRating(ratingMethod.calculateNewRating(black, white, game));

        ImmutableMap<UUID, Player> updatedPlayers = players.values().stream()
                .map(p -> {
                    if (p.getId().equals(updatedWhite.getId()))
                        return updatedWhite;
                    else if (p.getId().equals(updatedBlack.getId()))
                        return updatedBlack;
                    else
                        return p;
                })
                .collect(ImmutableMap.toImmutableMap(Player::getId, Function.identity()));
        ImmutableSet<Game> updatedGames = ImmutableSet.<Game>builder()
                .addAll(games)
                .add(game)
                .build();

        return new RatingGroup(updatedPlayers, updatedGames, ratingMethod);
    }

    private static ImmutableMap<UUID, Player> toPlayerMap(Collection<Player> players) {
        return ImmutableMap.copyOf(
                players.stream().collect(Collectors.toMap(Player::getId, Function.identity()))
        );
    }

    private static ImmutableMap<UUID, Game> toGameMap(Collection<Game> games) {
        return ImmutableMap.copyOf(
                games.stream().collect(Collectors.toMap(Game::getId, Function.identity()))
        );
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }
}
