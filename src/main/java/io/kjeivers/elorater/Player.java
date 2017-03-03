package io.kjeivers.elorater;

import java.util.Objects;
import java.util.UUID;

/**
 * User: kjeivers
 * Date: 03.03.2017
 */
public class Player {
    private final UUID id;
    private final String name;
    private final int rating;

    public Player(UUID id, String name, int rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public Player withRating(int rating) {
        return new Player(id, name, rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(name, player.name) &&
                Objects.equals(rating, player.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, rating);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                '}';
    }
}
