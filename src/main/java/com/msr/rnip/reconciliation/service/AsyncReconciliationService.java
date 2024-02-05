package com.msr.rnip.reconciliation.service;

import com.msr.rnip.reconciliation.adapter.jinn.JINNDocument;
import com.msr.rnip.reconciliation.adapter.jinn.JINNServiceAdapter;
import com.msr.rnip.reconciliation.adapter.smev2.SMEV2Document;
import com.msr.rnip.reconciliation.adapter.smev2.SMEV2ServiceAdapter;
import com.msr.rnip.reconciliation.model.Charge;
import com.msr.rnip.reconciliation.model.Package;
import com.msr.rnip.reconciliation.types.jinn.SigningRequestType;
import com.msr.rnip.reconciliation.utils.DateUtils;
import com.msr.rnip.reconciliation.utils.XMLHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.xml.sax.SAXException;

import ru.gosuslugi.smev.rev120315.BaseMessageType;
import ru.roskazna.gisgmp.xsd._116.charge.ChargeType;
import ru.roskazna.gisgmp.xsd._116.pgu_chargesresponse.ExportChargesResponseType;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//@author p4r53c
@Service
public class AsyncReconciliationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncReconciliationService.class);

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private SMEV2Document smev2Document;

    @Autowired
    private JINNDocument jinnDocument;

    @Autowired
    private XMLHelper xmlHelper;

    @Autowired
    private SMEV2ServiceAdapter smev2ServiceAdapter;

    @Autowired
    private JINNServiceAdapter jinnServiceAdapter;


    /**
     * Основной метод реализующий асинхронную обработку начислений.
     *
     * @param charges Список объектов начислений из БД.
     * @param taskId ID текущей задачи в рамках которой выполняется сверка
     * @return CompletableFuture<Boolean> TRUE - если пакет успешно отправлен и получен ответ, иначе FALSE.
     */
    @Async("reconciliationThreadPoolTaskExecutor")
    public CompletableFuture<Boolean> doReconcile(List<Charge> charges, String taskId) throws IOException, ParserConfigurationException, SAXException {

        String packageId = getPackageId();
        Package requestPackage = new Package(packageId, taskId);
        this.packageService.save(requestPackage);
        LOGGER.debug("PACKAGE [{}] created", packageId);

        for (Charge charge : charges) {
            charge.setPackageId(packageId);
            charge.setChargeIsProcessed(true);
            this.chargeService.save(charge);
            LOGGER.trace("CHARGE [{}] added in to PACKAGE [{}]", charge.getBillId(), packageId);
        }

        byte[] requestString = this.smev2Document.createEncodedSMEV2Document(charges, packageId);
        SigningRequestType signingRequest = this.jinnDocument.createSigningRequestDocument(requestString);
        LOGGER.debug("Creating outgoing JINNDocument message for PACKAGE [{}]", packageId);
        byte[] signingResult = this.jinnServiceAdapter.signMessage(signingRequest);
        LOGGER.debug("Sending message to SMEV2 for PACKAGE [{}]", packageId);
        String result = this.smev2ServiceAdapter.callSMEV2WebService(new String(signingResult));

        BaseMessageType resp = this.xmlHelper.xmlStringToJaxb(result);
        LOGGER.debug("Inbound Message from SMEV2 for PACKAGE [{}] was received", packageId);

        JAXBElement<?> chargesResponseType = resp.getMessageData().getAppData().getResponseMessage().getResponseMessageData();
        Object responseMessageType = chargesResponseType.getValue();

        // FIXME: Тут вероятно нужно сделать по другому и эта проерка вообще не нужна
        if (responseMessageType instanceof ExportChargesResponseType) {
            ExportChargesResponseType.Charges chargesResponse = ((ExportChargesResponseType) responseMessageType).getCharges();

            LOGGER.debug("Extracting and parsing ChargeInfo Type for PACKAGE [{}]", packageId);
            List<ExportChargesResponseType.Charges.ChargeInfo> chargeInfos = chargesResponse.getChargeInfo();
            for (ExportChargesResponseType.Charges.ChargeInfo chargeInfo : chargeInfos) {
                String chargeDataString = new String(chargeInfo.getChargeData());
                ChargeType charge = this.xmlHelper.xmlStringToJaxb(chargeDataString);
                String supplierBillId = charge.getSupplierBillID();
                BigInteger totalAmount = charge.getTotalAmount();
                String chargeStatusMeaning = charge.getChangeStatus().getMeaning();
                long amountToPay = chargeInfo.getAmountToPay();
                boolean isRevoked = chargeInfo.getIsRevoked().isValue();
                XMLGregorianCalendar isRevokedDate = chargeInfo.getIsRevoked().getDate();
                String quittanceWithPaymentStatus = chargeInfo.getQuittanceWithPaymentStatus();

                Charge localCharge = this.chargeService.getCharge(supplierBillId).orElse(null);
                assert localCharge != null;
                LOGGER.trace("Processing CHARGE [{}]", supplierBillId);

                localCharge.setChargeAmount(totalAmount);
                localCharge.setPackageId(packageId);
                localCharge.setChargeIsRevoked(isRevoked);
                localCharge.setChargeIsRevokedDate(DateUtils.toDate(isRevokedDate));
                localCharge.setChargeAmountToPay(BigInteger.valueOf(amountToPay));
                localCharge.setChargeStatus(Integer.parseInt(chargeStatusMeaning));
                localCharge.setChargeQuittanceStatus(Integer.parseInt(quittanceWithPaymentStatus));
                this.chargeService.save(localCharge);

            }
            Package localPackage = this.packageService.getPackage(packageId).get();
            localPackage.setPackageIsProcessed(true);
            this.packageService.save(localPackage);
            LOGGER.debug("PACKAGE [{}] processed", packageId);
        } else {
            requestPackage.setPackageIsProcessed(false);
            String errorMessage = "PACKAGE are not processed due to Incorrect response message";
            LOGGER.error(errorMessage);
            requestPackage.setPackageResultMessage(errorMessage);
            this.packageService.save(requestPackage);
        }

            return CompletableFuture.completedFuture(true);
        }

    /**
     * Метод формирующий ID пакета, в котором отправляются начисления
     * @return String - Префикс + Random UUID.
     */
    private String getPackageId() {
            return "I_" + UUID.randomUUID().toString();
        }

    }



