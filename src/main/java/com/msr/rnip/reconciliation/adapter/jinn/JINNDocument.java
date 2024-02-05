package com.msr.rnip.reconciliation.adapter.jinn;

import com.msr.rnip.reconciliation.types.jinn.SignatureType;
import com.msr.rnip.reconciliation.types.jinn.SigningRequestType;

import com.msr.rnip.reconciliation.types.jinn.ValidationRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


//@author p4r53c
@Component
@Qualifier("jinnDocument")
public class JINNDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(JINNDocument.class);

    @Value("${jinn.sign.service.bp.id}")
    private String BUSINESS_PROCESS_ID;

    private static final String SMEV_ACTOR = "http://smev.gosuslugi.ru/actors/smev";

    /**
     * Метод реализующий формирование документа {@link SigningRequestType) для JINN (Запрос на наложение ЭП).
     *
     * @param data Документ СМЭВ закодированный в Base64
     * @return SigningRequestType документ для отправки в Jinn.
     */
    public SigningRequestType createSigningRequestDocument(byte [] data) {
        SigningRequestType signingRequestType = new SigningRequestType();
        signingRequestType.setData(data);
        signingRequestType.setSignatureType(SignatureType.WSSEC_BES);
        signingRequestType.setActor(SMEV_ACTOR);
        signingRequestType.setBusinessProcessId(BUSINESS_PROCESS_ID);
        LOGGER.debug("SigningRequestType message created with params [SigType: {}], [Actor: {}], [BPId: {}]", SignatureType.WSSEC_BES, SMEV_ACTOR, BUSINESS_PROCESS_ID);
        return signingRequestType;
    }

    /**
     * Метод реализующий формирование документа {@link ValidationRequestType} для JINN (Запрос на проверку подписи).
     *
     * @param data Подписанный документ СМЭВ закодированный в Base64
     * @return ValidationRequestType документ для отправки в Jinn.
     */
    public ValidationRequestType createValidationRequestDocument (byte [] data) {
        ValidationRequestType validationRequestType = new ValidationRequestType();
        validationRequestType.setSignedData(data);
        return validationRequestType;
    }

}
