package org.ptr.robot.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ptr.robot.model.BatteryCharge;
import org.ptr.robot.model.Position;
import org.ptr.robot.model.enums.Command;
import org.ptr.robot.model.enums.Surface;

@Data
@JsonIgnoreProperties
@NoArgsConstructor
@AllArgsConstructor
public class CleanerRequest implements Serializable {

    private Surface[][] map;
    private Position start;
    private List<Command> commands;
    private BatteryCharge battery;

}
