package org.ptr.robot.validation;

import java.net.HttpURLConnection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ptr.robot.exception.Failure;
import org.ptr.robot.dto.request.CleanerRequest;

@Slf4j
public class ValidationUtils {


    public static boolean validateCleanerRequest(CleanerRequest request) {

        if(request == null){
            log.warn(" Cleaner request is empty or null");
            ValidationUtils.throwBadRequestError(" Cleaner request is empty or null");
        }

        return true;
    }

    @SneakyThrows
    private static void throwBadRequestError(String message) {
        throw new Failure(message, HttpURLConnection.HTTP_BAD_REQUEST);
    }

}
