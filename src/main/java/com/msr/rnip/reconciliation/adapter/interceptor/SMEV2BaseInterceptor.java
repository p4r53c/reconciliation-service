package com.msr.rnip.reconciliation.adapter.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//@author p4r53c
@Component
public class SMEV2BaseInterceptor implements ClientInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMEV2BaseInterceptor.class);

    /**
     * Метод перехватывающий и логирующий исходящее сообщение.
     *
     * @param messageContext Spring-WS {@link org.springframework.ws.WebServiceMessage} Контекст сообщения.
     * @return True в случае успешного перехвата сообщения.
     * @throws WebServiceClientException в случае если перехват не удался.
     */
    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            messageContext.getRequest().writeTo(buffer);
            LOGGER.debug(buffer.toString(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Метод перехватывающий и логирующий входящее сообщение (ответ на запрос).
     *
     * @param messageContext Spring-WS {@link org.springframework.ws.WebServiceMessage} Контекст сообщения.
     * @return True в случае успешного перехвата ответа.
     * @throws WebServiceClientException в случае если перехват не удался.
     */
    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            messageContext.getResponse().writeTo(buffer);
            LOGGER.debug(buffer.toString(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Метод перехватывающий, извлекающий и логирующий SOAPFault (Ошибку вызываемого сервиса).
     *
     * @param messageContext Spring-WS {@link org.springframework.ws.WebServiceMessage} Контекст сообщения.
     * @return True в случае успешного перехвата Fault.
     * @throws WebServiceClientException в случае если перехват не удался.
     */
    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        SoapMessage soapMessage = (SoapMessage) messageContext.getResponse();
        SoapEnvelope soapEnvelope = soapMessage.getEnvelope();
        SoapBody soapBody = soapEnvelope.getBody();
        SoapFault soapFault = soapBody.getFault();
        LOGGER.error(soapFault.getFaultStringOrReason());
        return false;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }

}
