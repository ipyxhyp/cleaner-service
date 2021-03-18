package org.ptr.robot.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.ptr.robot.dto.request.CleanerRequest;
import org.ptr.robot.dto.response.CleanerResponse;
import org.ptr.robot.service.CleanerService;
import org.ptr.robot.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cleaner")
public class CleanerController {

    private CleanerService cleanerService;

    @Autowired
    public void setCleanerService(CleanerService cleanerService) {
        this.cleanerService = cleanerService;
    }

    @ApiOperation(value = "Starts cleaner task and returns the response/result of cleaner operation task", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(path="/{start}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CleanerResponse startCleanTask(@RequestBody CleanerRequest cleanerRequest) {

        log.info("Call startCleanTask based on input : {} ", cleanerRequest);
        ValidationUtils.validateCleanerRequest(cleanerRequest);
        CleanerResponse response = this.cleanerService.processCommandList(cleanerRequest.getCommands(), cleanerRequest.getStart(),
                cleanerRequest.getBattery(), cleanerRequest.getMap());
        log.info(" result of cleaner task execution : {} ", response);
        return response;
        }

}
