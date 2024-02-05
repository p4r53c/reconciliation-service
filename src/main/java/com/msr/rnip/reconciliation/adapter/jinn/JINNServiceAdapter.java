package com.msr.rnip.reconciliation.adapter.jinn;

import com.msr.rnip.reconciliation.types.jinn.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.xml.bind.JAXBElement;

//@author p4r53c
public class JINNServiceAdapter extends WebServiceGatewaySupport {

    @Value("${jinn.sign.service.endpoint}")
    private String signServiceEndpoint;

    @Value("${jinn.validation.service.endpoint}")
    private String validationServiceEndpoint;

    private static final Logger log = LoggerFactory.getLogger(JINNServiceAdapter.class);

    private static final String SIGN_SOAP_ACTION = "http://www.roskazna.ru/eb/sign/types/sgv/Sign";
    private static final String VALIDATE_SOAP_ACTION = "http://www.roskazna.ru/eb/sign/types/sgv/Validate";

    public JINNServiceAdapter() {
    }

    /**
     * Метод реализующий наложение ЭП-ОВ на исходящий документ через вызов JINN.
     *
     * @param request Запрос {@link SigningRequestType} содержащий документ {@link JINNDocument} для наложения ЭП-ОВ с набором параметров.
     * @return Массив байт содержащий подписанный документ.
     */
    @SuppressWarnings("unchecked")
    public byte[] signMessage(SigningRequestType request) {
        JAXBElement<SigningRequestType> message = new ObjectFactory().createSigningRequestType(request);
        final Object signingResponse = getWebServiceTemplate().marshalSendAndReceive(signServiceEndpoint, message, new SoapActionCallback(SIGN_SOAP_ACTION));
        // TODO: Как-то отрефакторить этот cast в будущем
        return ((JAXBElement<byte[]>) signingResponse).getValue();
    }

    /**
     * Метод реализующий простую проверку ЭП-ОВ через вызов JINN.
     *
     * @param request Запрос {@link ValidationRequestType} содержащий документ {@link JINNDocument} с ЭП-ОВ
     * @return boolean True если ЭП-ОВ валидна, иначе взовращается False.
     */
    public boolean validateSignature(ValidationRequestType request) {
        JAXBElement<ValidationRequestType> message = new ObjectFactory().createValidationRequestType(request);
        final ValidationRes validationResponse = (ValidationRes) getWebServiceTemplate()
                .marshalSendAndReceive(validationServiceEndpoint, message, new SoapActionCallback(VALIDATE_SOAP_ACTION));
        GlobalStatus globalStatus = validationResponse.getGlobalStatus();
        return globalStatus == GlobalStatus.VALID;
    }

}