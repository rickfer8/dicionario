package com.ufla.dicionario.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class DicionarioResponseErroHandler implements ResponseErrorHandler {

	private static String INTERNAL_SERVER_ERROR = "An error has occurred.";

	
	@Override
    public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {

        if (clienthttpresponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {            
            throw new DicionarioException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {

        if (clienthttpresponse.getStatusCode() != HttpStatus.OK) {

            if (clienthttpresponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {                
                return true;
            }
        }
        return false;
    }
}
