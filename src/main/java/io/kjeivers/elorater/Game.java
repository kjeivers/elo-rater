package io.kjeivers.elorater;

import java.time.LocalDate;
import java.util.UUID;

/**
 * User: kjeivers
 * Date: 03.03.2017
 */
public class Game {
    private final UUID id;
    private final LocalDate date;
    private final UUID white;
    private final UUID black;
    private final Result result;

    public Game(UUID id, LocalDate date, UUID white, UUID black, Result result) {
        this.id = id;
        this.date = date;
        this.white = white;
        this.black = black;
        this.result = result;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public UUID getWhite() {
        return white;
    }

    public UUID getBlack() {
        return black;
    }

    public Result getResult() {
        return result;
    }

    public boolean isWhite(Player player) {
        return white.equals(player.getId());
    }

    public boolean isBlack(Player player) {
        return black.equals(player.getId());
    }

    public static GameBuilder builder() {
        return new GameBuilder();
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", date=" + date +
                ", white=" + white +
                ", black=" + black +
                ", result=" + result +
                '}';
    }

    public static class GameBuilder {
        private LocalDate date = LocalDate.now();
        private Player white;
        private Player black;
        private Result result;

        public GameBuilder withWhite(Player player) {
            this.white = player;
            return this;
        }

        public GameBuilder withBlack(Player player) {
            this.black = player;
            return this;
        }

        public GameBuilder withResult(Result result) {
            this.result = result;
            return this;
        }

        public Game build() {
            return new Game(UUID.randomUUID(), date, white.getId(), black.getId(), result);
        }
    }
}
