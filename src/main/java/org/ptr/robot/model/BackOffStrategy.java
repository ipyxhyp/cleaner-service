package org.ptr.robot.model;

import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import org.ptr.robot.model.enums.Command;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
public class BackOffStrategy {

    private List<Command> backOffCommandList1 = Arrays.asList(Command.TR, Command.ADVANCE, Command.TL);
    private List<Command> backOffCommandList2 = Arrays.asList(Command.TR, Command.ADVANCE, Command.TR);
    private List<Command> backOffCommandList3 = Arrays.asList(Command.TR, Command.BACK, Command.TR, Command.ADVANCE);
    private List<Command> backOffCommandList4 = Arrays.asList(Command.TL, Command.TL, Command.ADVANCE);

    private List<List<Command>> backOffStrategy = Arrays.asList(backOffCommandList1, backOffCommandList2,
        backOffCommandList2, backOffCommandList3, backOffCommandList4);
}
