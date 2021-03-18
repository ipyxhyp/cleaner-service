package org.ptr.robot.test;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ptr.robot.config.CleanerConfig;
import org.ptr.robot.model.BackOffStrategy;
import org.ptr.robot.model.BatteryCharge;
import org.ptr.robot.model.Position;
import org.ptr.robot.model.enums.Command;
import org.ptr.robot.model.enums.Direction;
import org.ptr.robot.model.enums.Surface;
import org.ptr.robot.service.CleanerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@DisplayName("Test of cleaner robot service")
@ContextConfiguration(classes = {
    CleanerConfig.class
})
@ExtendWith(SpringExtension.class)
public class CleanerRobotTest {

    @Autowired
    private CleanerService cleanerService;

    @Autowired
    private BackOffStrategy backOffStrategy;

    @Test
    @DisplayName("Input surface trace test")
    public void inputSurfaceTest() throws Exception {

        Surface inputSurface[][] = {
            {Surface.WALL, Surface.SPACE, Surface.WALL, Surface.SPACE},
            {Surface.SPACE, Surface.SPACE, Surface.SPACE, Surface.COLUMN},
            {Surface.SPACE, Surface.COLUMN, Surface.SPACE, Surface.SPACE},
            {Surface.SPACE, Surface.SPACE, Surface.SPACE, Surface.WALL},
        };

        Direction facing = Direction.NORTH;
        int startX = 2, startY = 3;
        Position position = new Position(startX, startY, facing);
        BatteryCharge batteryCharge = new BatteryCharge(50);
        List<Command> commandsList = Arrays.asList(
            Command.ADVANCE, Command.CLEAN, Command.ADVANCE,
            Command.CLEAN, Command.TL, Command.TL,
            Command.ADVANCE, Command.CLEAN, Command.TR,
            Command.ADVANCE, Command.ADVANCE, Command.ADVANCE
        );

        cleanerService.processCommandList(commandsList, position, batteryCharge, inputSurface);
        assertThat(cleanerService.getCleanerResponse().getVisited()).isNotEmpty();
        assertThat(cleanerService.getCleanerResponse().getCleaned()).isNotEmpty();
        assertThat(cleanerService.getCleanerResponse().getResultPosition()).isNotNull();

    }

}
