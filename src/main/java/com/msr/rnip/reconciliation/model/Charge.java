package com.msr.rnip.reconciliation.model;

import javax.persistence.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Charge")
@Table(name = "CHARGES")
public class Charge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "BILL_ID", unique = true, length = 40)
    private String billId;

    //@ManyToOne
    //@JoinColumn(name = "PACKAGE_ID", updatable = false)
    @Column(name = "PACKAGE_ID")
    private String packageId;

    @Column(name = "IS_PROCESSED")
    private Boolean chargeIsProcessed;

    @Column(name = "AMOUNT_TO_PAY")
    private BigInteger chargeAmountToPay;

    @Column(name = "AMOUNT")
    private BigInteger chargeAmount;

    @Column(name = "QUITTANCE_STATUS")
    private Integer chargeQuittanceStatus;

    @Column(name = "CHARGE_STATUS")
    private Integer chargeStatus;

    @Column(name = "IS_REVOKED")
    private Boolean chargeIsRevoked;

    @Column(name = "REVOKED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date chargeIsRevokedDate;

    public Charge() {
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public Boolean isChargeIsProcessed() {
        return chargeIsProcessed;
    }

    public void setChargeIsProcessed(boolean chargeIsProcessed) {
        this.chargeIsProcessed = chargeIsProcessed;
    }

    public BigInteger getChargeAmountToPay() {
        return chargeAmountToPay;
    }

    public void setChargeAmountToPay(BigInteger chargeAmountToPay) {
        this.chargeAmountToPay = chargeAmountToPay;
    }

    public BigInteger getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigInteger chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Integer getChargeQuittanceStatus() {
        return chargeQuittanceStatus;
    }

    public void setChargeQuittanceStatus(Integer chargeQuittanceStatus) {
        this.chargeQuittanceStatus = chargeQuittanceStatus;
    }

    public Integer getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(Integer chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public Boolean isChargeIsRevoked() {
        return chargeIsRevoked;
    }

    public void setChargeIsRevoked(boolean chargeIsRevoked) {
        this.chargeIsRevoked = chargeIsRevoked;
    }

    public Date getChargeIsRevokedDate() {
        return chargeIsRevokedDate;
    }

    public void setChargeIsRevokedDate(Date chargeIsRevokedDate) {
        this.chargeIsRevokedDate = chargeIsRevokedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Charge charge = (Charge) o;
        return isChargeIsProcessed() == charge.isChargeIsProcessed() &&
                isChargeIsRevoked() == charge.isChargeIsRevoked() &&
                getBillId().equals(charge.getBillId()) &&
                Objects.equals(getPackageId(), charge.getPackageId()) &&
                Objects.equals(getChargeAmountToPay(), charge.getChargeAmountToPay()) &&
                Objects.equals(getChargeAmount(), charge.getChargeAmount()) &&
                Objects.equals(getChargeQuittanceStatus(), charge.getChargeQuittanceStatus()) &&
                Objects.equals(getChargeStatus(), charge.getChargeStatus()) &&
                Objects.equals(getChargeIsRevokedDate(), charge.getChargeIsRevokedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBillId(),
                getPackageId(),
                isChargeIsProcessed(),
                getChargeAmountToPay(),
                getChargeAmount(),
                getChargeQuittanceStatus(),
                getChargeStatus(),
                isChargeIsRevoked(),
                getChargeIsRevokedDate());
    }
}
