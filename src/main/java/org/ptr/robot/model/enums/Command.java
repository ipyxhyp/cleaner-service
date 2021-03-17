package org.ptr.robot.model.enums;

import lombok.Getter;

@Getter
public enum Command {

    ADVANCE("ADVANCE_TO_NEXT", 2),
    BACK("BACK_TO_PREVIOUS", 3),
    CLEAN("CLEAN_CURRENT", 5),
    TL("TURN_LEFT", 1),
    TR("TURN_RIGHT", 1);

    private String name;
    private Integer value;

    Command(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
