package io.kjeivers.elorater;

/**
 * User: kjeivers
 * Date: 04.03.2017
 */
public enum Result {
    WHITE("White won"), BLACK("Black won"), DRAW("Draw");

    private String label;

    Result(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
