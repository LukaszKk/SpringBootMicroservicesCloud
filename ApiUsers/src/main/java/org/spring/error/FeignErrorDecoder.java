package org.spring.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey,
                            Response response) {
        return switch (response.status()) {
            case 400, 404 -> new ResponseStatusException(HttpStatus.valueOf(response.status()), "Users albums are not found");
            default -> new Exception(response.reason());
        };
    }
}
