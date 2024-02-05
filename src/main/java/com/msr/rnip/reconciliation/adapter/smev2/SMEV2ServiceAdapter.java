package com.msr.rnip.reconciliation.adapter.smev2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

//@author p4r53c
public class SMEV2ServiceAdapter extends WebServiceGatewaySupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMEV2ServiceAdapter.class);
    private static final String SMEV2_SOAP_ACTION = "http://roskazna.ru/gisgmp/02000000/SmevGISGMPService/GISGMPTransferMsg";

    /**
     * Метод вызывающий удаленный веб-сервис СМЭВ-2 и выполняющий трансформацию SOAP-заголовка + добавление SOAP-Action перед отправкой.
     * @param request Готовый запрос к СМЭВ-2
     * @return String ответ для последующей нонвертации в JAXB-объект.
     */
    public String callSMEV2WebService(String request) {
        StreamSource source = new StreamSource(new StringReader(request));

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        getWebServiceTemplate().sendSourceAndReceiveToResult(source, webServiceMessage -> {
            LOGGER.debug("SAAJ: Modify outgoing SOAP-Headers and send message to SMEV2");
            SaajSoapMessage saajSoapMessage = (SaajSoapMessage) webServiceMessage;
            try {
                SOAPPart soapPart = saajSoapMessage.getSaajMessage().getSOAPPart();
                SOAPBody soapBody = soapPart.getEnvelope().getBody();
                Document innerSoapBody = soapBody.extractContentAsDocument();

                MessageFactory soapMessageFactory = MessageFactory.newInstance();
                SOAPMessage soapMessage = soapMessageFactory.createMessage();
                soapMessage.getSOAPPart().setContent(new DOMSource(innerSoapBody));

                ((SaajSoapMessage) webServiceMessage).setSaajMessage(soapMessage);
            } catch (SOAPException e) {
                LOGGER.error("Can't rebuild WebServiceMessage in SMEV2 invoke!");
                e.printStackTrace();
            }

            saajSoapMessage.setSoapAction(SMEV2_SOAP_ACTION);
        }, result);

    return writer.toString();
    }

}
