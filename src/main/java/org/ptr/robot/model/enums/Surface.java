package org.ptr.robot.model.enums;

import lombok.Getter;

@Getter
public enum Surface {

    SPACE("SPACE"),
    COLUMN("COLUMN"),
    WALL(null);

    private final String name;

    Surface(String name) {
        this.name = name;
    }
}
