package com.msr.rnip.reconciliation.adapter.smev2;

import com.msr.rnip.reconciliation.model.Charge;
import com.msr.rnip.reconciliation.utils.DateUtils;

import com.msr.rnip.reconciliation.utils.XMLHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gosuslugi.smev.rev120315.*;
import ru.roskazna.gisgmp.xsd._116.message.RequestMessageType;
import ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.Holder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

//@author p4r53c
@Component
public class SMEV2Document {

    @Autowired
    XMLHelper xmlHelper;

    private ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory messageDataObjectFactory
            = new ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory();

    //GISGMPTransferMsg Factory
    private ru.roskazna.gisgmp._02000000.smevgisgmpservice.ObjectFactory smevServiceObjectFactory
            = new ru.roskazna.gisgmp._02000000.smevgisgmpservice.ObjectFactory();

    //СМЭВ: Данные о системе-инициаторе взаимодействия (Потребителе)
    private static final String SMEV2_MESSAGE_SENDER_CODE = "RNP000000";
    private static final String SMEV2_MESSAGE_SENDER_NAME = "RNiP";

    //СМЭВ: Данные о системе-получателе сообщения (Поставщике)
    private static final String SMEV2_MESSAGE_RECIPIENT_CODE = "SMEV2_MESSAGE_RECIPIENT_CODE";
    private static final String SMEV2_MESSAGE_RECIPIENT_NAME = "FK";

    //СМЭВ: Мнемоника электронного сервиса
    private static final String SMEV2_MESSAGE_SERVICE_NAME = "GISGMP";
    //СМЭВ: Категория взаимодействия
    private static final String SMEV2_MESSAGE_EXCHANGE_TYPE = "SMEV2_MESSAGE_EXCHANGE_TYPE";
    //СМЭВ: Тип сообщения
    private static final String SMEV2_MESSAGE_TYPE_CODE = "SMEV2_MESSAGE_TYPE_CODE";

    //ГИС ГМП: Данные отправителя в RequestMessage
    private static final String GISGMP_SENDER_ROLE = "ROLE";
    private static final String GISGMP_SENDER_ID = "MNEMONIC";

    //ГИС ГМП: Вид запроса на экспорт
    private static final String GISGMP_REQUEST_KIND = "CHARGESTATUS";

    /**
     * Метод создающий JAXB-объект {@link BaseMessageType}, который кодируется в Byte-массив, пригодный для подписания в ЕКС
     * @param  charges список объектов-начислений.
     * @param  packageID ID пакета для {@link RequestMessageType}.
     * @return Byte-массив для передачи в вызов ЕКС.
     */
    public byte[] createEncodedSMEV2Document(List<Charge> charges, String packageID) throws IOException {

        DataRequest dataRequest = new DataRequest();

        DataRequest.Filter filter = new DataRequest.Filter();
        DataRequest.Filter.Conditions conditions = new DataRequest.Filter.Conditions();
        DataRequest.Filter.Conditions.ChargesIdentifiers chargesIdentifiers = new DataRequest.Filter.Conditions.ChargesIdentifiers();

        for (Charge charge : charges) {
            chargesIdentifiers.getSupplierBillID().add(charge.getBillId());
        }

        conditions.setChargesIdentifiers(chargesIdentifiers);
        filter.setConditions(conditions);
        dataRequest.setKind(GISGMP_REQUEST_KIND);
        dataRequest.setFilter(filter);

        RequestMessageType requestMessageType = new RequestMessageType();
        requestMessageType.setTimestamp(DateUtils.toXMLCalendar(new Date()));
        requestMessageType.setSenderIdentifier(GISGMP_SENDER_ID);
        requestMessageType.setSenderRole(GISGMP_SENDER_ROLE);
        requestMessageType.setId(packageID);
        requestMessageType.setRequestMessageData(messageDataObjectFactory.createExportRequest(dataRequest));


        AppDataType appDataType = new AppDataType();
        appDataType.setRequestMessage(requestMessageType);
        MessageDataType messageDataType = new MessageDataType();
        messageDataType.setAppData(appDataType);

        MessageType message = createMessage().value;

        BaseMessageType unifoMessage = new BaseMessageType();
        unifoMessage.setMessage(message);
        unifoMessage.setMessageData(messageDataType);

        JAXBElement<BaseMessageType> baseGISGMPTransferMsg = smevServiceObjectFactory.createGISGMPTransferMsg(unifoMessage);
        String smev2ChargeStatusRequest = xmlHelper.jaxbToXmlString(baseGISGMPTransferMsg);
        return xmlHelper.createSoapEnvelope(smev2ChargeStatusRequest).getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Метод создающий и сохраняющий {@link MessageType}
     * @return Типизированный объект Holder<MessageType> для использования в строителе основного сообщения
     */
    private Holder<MessageType> createMessage() {
        OrgExternalType sender = new OrgExternalType();
        sender.setCode(SMEV2_MESSAGE_SENDER_CODE);
        sender.setName(SMEV2_MESSAGE_SENDER_NAME);

        OrgExternalType recipient = new OrgExternalType();
        recipient.setCode(SMEV2_MESSAGE_RECIPIENT_CODE);
        recipient.setName(SMEV2_MESSAGE_RECIPIENT_NAME);

        MessageType messageType = new MessageType();
        messageType.setSender(sender);
        messageType.setRecipient(recipient);
        messageType.setServiceName(SMEV2_MESSAGE_SERVICE_NAME);
        messageType.setTypeCode(TypeCodeType.valueOf(SMEV2_MESSAGE_TYPE_CODE));
        messageType.setStatus(StatusType.REQUEST);
        messageType.setDate(DateUtils.toXMLCalendar(new Date()));
        messageType.setExchangeType(SMEV2_MESSAGE_EXCHANGE_TYPE);

        return new Holder<>(messageType);
    }

}
