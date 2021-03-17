package org.ptr.robot.service;

import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ptr.robot.dto.CleanerResult;
import org.ptr.robot.model.BackOffStrategy;
import org.ptr.robot.model.BatteryCharge;
import org.ptr.robot.model.Position;
import org.ptr.robot.model.enums.Command;
import org.ptr.robot.model.enums.Direction;
import org.ptr.robot.model.enums.Surface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Service clean robot , performs  the commands executions based on input (commands, input surfaces area)
 *
 * */
@Service
@Slf4j
public class CleanerService {

    @Getter
    private final CleanerResult cleanerResult = new CleanerResult();

    private BackOffStrategy backOffStrategy;

    @Autowired
    public void setBackOffStrategy(BackOffStrategy backOffStrategy) {
        this.backOffStrategy = backOffStrategy;
    }

    /**
     * 0
     *
     *
     * */
    public void processCommandList(List<Command> commandList, Position position,
        BatteryCharge batteryCharge, Surface[][] inputSurface) {
        // move from start and unless commandsList  is not empty - execute command
        for (Command command : commandList) {
            if (!isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                log.info(" Cleaner robot is out of charge {},  for next command : {} ", batteryCharge.getCharge(),
                    command.getValue());
                break;
            }
            Surface cell = inputSurface[position.getX()][position.getY()];
            if (Surface.WALL.equals(cell) || Surface.COLUMN.equals(cell)) {
                log.info(
                    " cleaner is facing the wall or column : {} , need to execute backoff strategy, command to exec : {}",
                    cell, command);
                if (!executeBackOffStrategy(inputSurface, position, batteryCharge, backOffStrategy)) {
                    log.info(" cleaner is considered as Stuck, break the command executions and exit ");
                    break;
                }
            } else {
                log.info(" cleaner is on the surface : {} , execute command : {}", cell, command);
                processCommand(inputSurface, position, batteryCharge, command);
            }
        }
        this.cleanerResult.setBatteryCharge(batteryCharge);
        this.cleanerResult.setResultPosition(position);
        log.info(
            " final position of cleaner robot : {} , battery charge left : {} , cells visited : {} , cells cleaned : {}",
            this.cleanerResult.getResultPosition(),
            this.cleanerResult.getBatteryCharge(),
            this.cleanerResult.getVisited(),
            this.cleanerResult.getCleaned()
        );

    }

    private void processCommand(Surface[][] inputSurface, Position cellPosition,
        BatteryCharge battery, Command command) {

        if (command != null && cellPosition != null && inputSurface != null) {
            int rows = inputSurface.length;
            int columns = inputSurface[--rows].length - 1;
            log.info(" inputSurface limits : {} , {} ", rows, columns);
            if (cellPosition.getX() > rows || cellPosition.getY() > columns) {
                log.info(" x {} and y {}  are out of the bounds of {} , {} ", cellPosition.getX(), cellPosition.getY(),
                    rows, columns);
            }
            Surface cell = inputSurface[cellPosition.getX()][cellPosition.getY()];
            log.info(" cell: {},  on x,y : {} ", cell, cellPosition);
            Boolean isCommandExecuted = executeCommand(inputSurface, cellPosition, battery, command, columns);
            if (!isCommandExecuted) {
                log.warn(" command was not executed, {} , because of obstacle faced, execute backOffStrategy ",
                    command);
                boolean isBackOffSucceed = executeBackOffStrategy(inputSurface, cellPosition, battery, backOffStrategy);
                if (!isBackOffSucceed) {
                    log.warn(" BackOffStrategy Failed, robot is stuck");
                    throw new RuntimeException(" BackOffStrategy Failed, robot is stuck");
                }
            }
        } else {
            log.warn(" some input parameter is empty or null ");
        }
    }


    /**
     * Executes one command based on type
     *
     * @param columns - size of area
     * @param surfaces - size of area
     * @param cellPosition - size of area
     * @param command - size of area
     * @return result of command execution, if next surface is not accessible - return false, (same as for not enough
     * charge) otherwise returns true
     */
    private Boolean executeCommand(Surface[][] surfaces, Position cellPosition, BatteryCharge batteryCharge,
        Command command, int columns) {

        Boolean isCommandExecuted = Boolean.FALSE;
        if (command != null) {
            log.info(" command execution started : {} , batteryCharge : {} ", command, batteryCharge);
            switch (command) {
                case CLEAN: {
                    if (isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                        consumeCharge(batteryCharge, command.getValue());
                        // stay here consume only battery charge, mark cell as Cleaned
                        Position newCellPosition = cellPosition.toBuilder().build();
                        this.cleanerResult.addCleaned(newCellPosition);
                        log.info(" cell position after CLEAN : {} ", cellPosition);
                        isCommandExecuted = Boolean.TRUE;
                    } else {
                        log.warn(" battery charge level : {} is not enough for command execution: {} ", batteryCharge,
                            command.getValue());
                        return Boolean.FALSE;
                    }
                    break;
                }
                case ADVANCE: {
                    if (isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                        isCommandExecuted = changePosition(surfaces, cellPosition, columns, Boolean.TRUE);
                        if (isCommandExecuted) {
                            consumeCharge(batteryCharge, command.getValue());
                            log.info(" cell position after ADVANCE : {} ", cellPosition);
                        }
                    } else {
                        log.warn(" battery charge level : {} is not enough for command execution: {} ", batteryCharge,
                            command.getValue());
                        return Boolean.FALSE;
                    }
                    break;
                }
                case BACK: {
                    if (isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                        isCommandExecuted = changePosition(surfaces, cellPosition, columns, Boolean.FALSE);
                        if (isCommandExecuted) {
                            consumeCharge(batteryCharge, command.getValue());
                            log.info(" cell position after BACK : {} ", cellPosition);
                        }
                    } else {
                        log.warn(" battery charge level : {} is not enough for command execution: {} ", batteryCharge,
                            command.getValue());
                        return Boolean.FALSE;
                    }
                    break;
                }

                case TL: {
                    if (isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                        turnLeft(cellPosition);
                        consumeCharge(batteryCharge, command.getValue());
                        isCommandExecuted = Boolean.TRUE;
                    } else {
                        log.warn(" battery charge level : {} is not enough for command execution: {} ", batteryCharge,
                            command.getValue());
                        return Boolean.FALSE;
                    }
                    break;
                }
                case TR: {
                    if (isChargeEnough(batteryCharge.getCharge(), command.getValue())) {
                        turnRight(cellPosition);
                        consumeCharge(batteryCharge, command.getValue());
                        isCommandExecuted = Boolean.TRUE;
                    } else {
                        log.warn(" battery charge level : {} is not enough for command execution: {} ", batteryCharge,
                            command.getValue());
                        return Boolean.FALSE;
                    }
                    break;
                }
            }
            log.info(" cell position on x,y : {}, is command executed successfully : {} ", cellPosition,
                isCommandExecuted);
        }
        return isCommandExecuted;
    }

    private void turnLeft(Position cellPosition) {
        // change direction anticlockwise, no change of position
        // Direction--
        Integer directionValue = cellPosition.getDirection().getValue();
        if (cellPosition.getDirection().equals(Direction.NORTH)) {
            cellPosition.setDirection(Direction.WEST);
        } else {
            cellPosition.setDirection(Direction.valueOf(--directionValue));
        }
        log.info("TL <- direction updated : {} ", cellPosition.getDirection());
    }

    private void turnRight(Position cellPosition) {
        // change direction clockwise
        // Direction++ , no change of position
        Integer directionValue = cellPosition.getDirection().getValue();
        if (cellPosition.getDirection().equals(Direction.WEST)) {
            cellPosition.setDirection(Direction.NORTH);
        } else {
            cellPosition.setDirection(Direction.valueOf(++directionValue));
        }
        log.info("TR -> direction updated : {} ", cellPosition.getDirection());
    }

    /**
     *
     */
    private Boolean changePosition(Surface[][] surfaces, Position cellPosition, int cells, boolean isForward) {
        Boolean isPositionUpdated = Boolean.FALSE;
        if (cellPosition != null) {
            Integer x = cellPosition.getX();
            Integer y = cellPosition.getY();
            Direction moveDirection = cellPosition.getDirection();
            if ((x > cells || x < 0) || (y > cells || y < 0)) {
                log.warn(" cannot move to cell {} , {} as its off the area bounds {}", x, y, cells);
            } else {
                switch (moveDirection) {
                    case NORTH: {
                        if (isForward) {
                            --x; // only move to forward position on X, up / north direction
                        } else {
                            if (x == cells) {
                                // we are next to the wall of area
                                log.warn(" cannot move outside of the area, the cleaner is next to the wall {}, {}", x,
                                    cells);
                                return Boolean.FALSE;
                            }
                            ++x; // only move to back position on X, down / south direction
                        }
                        // check next surface on x, y
                        isPositionUpdated = isSurfaceAccessible(surfaces[x][y]);
                        updateCellPositionX(cellPosition, isPositionUpdated, x);
                        break;
                    }
                    case EAST: {
                        if (isForward) {
                            if (y == cells) {
                                // we are next to the wall of area
                                log.warn(" cannot move outside of the area, the cleaner is next to the wall {}, {}", y,
                                    cells);
                                return Boolean.FALSE;
                            }
                            ++y; // only move to forward position on Y, right / east direction
                        } else {
                            --y; // only move to back position on Y, left / west direction
                        }
                        // check next surface on x, y
                        isPositionUpdated = isSurfaceAccessible(surfaces[x][y]);
                        updateCellPositionY(cellPosition, isPositionUpdated, y);
                        break;
                    }
                    case SOUTH: {
                        if (isForward) {
                            if (x == cells) {
                                // we are next to the wall of area
                                log.warn(" cannot move outside of the area, the cleaner is next to the wall {}, {}", x,
                                    cells);
                                return Boolean.FALSE;
                            }
                            ++x; // only move to forward position on X, down / south direction
                        } else {
                            --x; // only move to back position on X, up / north direction
                        }
                        isPositionUpdated = isSurfaceAccessible(surfaces[x][y]);
                        updateCellPositionX(cellPosition, isPositionUpdated, x);
                        break;
                    }
                    case WEST: {
                        if (isForward) {
                            --y; // only move to forward position on Y, left / west direction
                        } else {
                            if (y == cells) {
                                // we are next to the wall of area
                                log.warn(" cannot move outside of the area, the cleaner is next to the wall {}, {}", y,
                                    cells);
                                return Boolean.FALSE;
                            }
                            ++y; // only move to back position on Y, right / east direction
                        }
                        isPositionUpdated = isSurfaceAccessible(surfaces[x][y]);
                        updateCellPositionY(cellPosition, isPositionUpdated, y);
                        break;
                    }
                }
            }
        } else {
            log.warn(" cell position is null ");
        }
        return isPositionUpdated;
    }


    /**
     * Perform [TR, A, TL]. If an obstacle is hit, drop the rest of the sequence and perform [TR, A, TR]. If an obstacle
     * is hit, drop the rest of the sequence and perform [TR, A, TR]. If an obstacle is hit, drop the rest of the
     * sequence and perform [TR, B, TR, A]. If an obstacle is hit, drop the rest of the sequence and perform [TL, TL,
     * A]. If an obstacle is hit, the robot is considered stuck. Skip all the remaining commands and finish the
     * program.
     *
     * @return result of backoff strategy exec, false if its stuck or impossible to exec or find the way
     **/

    private Boolean executeBackOffStrategy(Surface[][] inputSurface, Position cellPosition,
        BatteryCharge charge, BackOffStrategy backOffStrategy) {
        Boolean canMoveNext = Boolean.FALSE;
        log.info(" Backoff strategy execution initiated ");
        for (List<Command> commandList : backOffStrategy.getBackOffStrategy()) {
            canMoveNext = backOffStep(inputSurface, cellPosition, charge, commandList);
            if (canMoveNext) {
                log.info(" BackOffStep : {} execution succeed, exiting from backOffStrategy ", commandList);
                break;
            } else {
                log.info(" BackOffStep : {} execution failed,  continuing backOffStrategy ", commandList);
            }
        }
        log.info(" Can robot move next ? {} ", canMoveNext);
        return canMoveNext;
    }

    private Boolean backOffStep(Surface[][] inputSurface, Position cellPosition,
        BatteryCharge charge, List<Command> commandList) {
        Boolean result = Boolean.FALSE;
        int rows = inputSurface.length;
        int columns = inputSurface[--rows].length - 1;
        for (Command command : commandList) {
            result = executeCommand(inputSurface, cellPosition, charge, command, columns);
            if (!result) {
                log.warn(" BackOff command : {} execution failed, canceling the sequence {} ", command, commandList);
                break;
            }
        }
        log.info(" BackOff commandList : {} execution succeed : {} ", commandList, result);
        return result;
    }

    private Boolean isSurfaceAccessible(Surface nextSurface) {
        if (Surface.COLUMN.equals(nextSurface) || Surface.WALL.equals(nextSurface)) {
            log.warn(" Next surface is column or wall : {}, cannot move on that ", nextSurface);
            return Boolean.FALSE;
        } else {
            log.info(" Next surface is accessible surface : {} ", nextSurface);
            return Boolean.TRUE;
        }
    }


    private void updateCellPositionX(Position cellPosition, Boolean isPositionUpdated, Integer x) {
        if (isPositionUpdated) {
            Position newCellPosition = cellPosition.toBuilder().build();
            cellPosition.setX(x);
            this.cleanerResult.addVisited(newCellPosition);
            log.info(" cell position updated {}", cellPosition);
        }
    }

    private void updateCellPositionY(Position cellPosition, Boolean isPositionUpdated, Integer y) {
        if (isPositionUpdated) {
            Position newCellPosition = cellPosition.toBuilder().build();
            cellPosition.setY(y);
            this.cleanerResult.addVisited(newCellPosition);
            log.info(" cell position updated {}", cellPosition);
        }
    }

    private Boolean isChargeEnough(int batteryCharge, int commandCharge) {
        return batteryCharge >= commandCharge;
    }

    private void consumeCharge(BatteryCharge battery, int i) {
        battery.setCharge(Math.subtractExact(battery.getCharge(), i));
    }
}
