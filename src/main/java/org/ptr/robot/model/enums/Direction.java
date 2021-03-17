package org.ptr.robot.model.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Direction {

    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);

    private final Integer value;

    Direction(Integer value) {
        this.value = value;
    }

    public static Direction valueOf(Integer value){

        for (Direction direction : Direction.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        return null;
    }
}
