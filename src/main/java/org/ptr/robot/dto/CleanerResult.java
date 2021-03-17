package org.ptr.robot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ptr.robot.model.BatteryCharge;
import org.ptr.robot.model.Position;

@Data
@JsonIgnoreProperties
@NoArgsConstructor
@AllArgsConstructor
public class CleanerResult {

    private final List<Position> visited = new ArrayList<>();
    private final Set<Position> cleaned = new HashSet<>();
    private Position resultPosition;
    private BatteryCharge batteryCharge;

    public Boolean addVisited(Position position){
        return this.getVisited().add(position);
    }

    public Boolean addCleaned(Position position){
        return this.getCleaned().add(position);
    }

    public void clearVisited(){
        this.getVisited().clear();
    }

    public void clearCleaned(){
        this.getCleaned().clear();
    }

}
