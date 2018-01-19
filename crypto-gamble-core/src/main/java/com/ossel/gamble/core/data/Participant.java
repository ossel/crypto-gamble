package com.ossel.gamble.core.data;


import java.util.Date;

public class Participant {

    private String pseudonym;
    private String depositAddress;
    private String payoutAddress;
    private long receivedAmount;
    private Date receiveTime;
    private boolean bankParticipant;

    public Participant(String depositAddress, String payoutAddress) {
        super();
        this.depositAddress = depositAddress;
        this.payoutAddress = payoutAddress;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getDepositAddress() {
        return depositAddress;
    }

    public void setDepositAddress(String depositAddress) {
        this.depositAddress = depositAddress;
    }

    public String getPayoutAddress() {
        return payoutAddress;
    }

    public void setPayoutAddress(String payoutAddress) {
        this.payoutAddress = payoutAddress;
    }

    public long getReceivedAmount() {
        return receivedAmount;
    }

    public Date setReceivedAmount(long receivedAmount) {
        this.receivedAmount = receivedAmount;
        if (this.receiveTime != null) {
            System.out.println("Warning: already received coins at " + this.receiveTime);
        }
        this.receiveTime = new Date();
        return this.receiveTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public boolean hasPayed() {
        return receiveTime != null;
    }

    public String getDisplayName() {
        if (isBankParticipant()) {
            return pseudonym;
        }
        if (pseudonym != null && !pseudonym.isEmpty()) {
            return pseudonym + " (" + depositAddress + ")";
        }
        return depositAddress;
    }

    public boolean isBankParticipant() {
        return bankParticipant;
    }

    public void setBankParticipant(boolean bankParticipant) {
        this.bankParticipant = bankParticipant;
    }

    @Override
    public String toString() {
        return "Participant [depositAddress=" + depositAddress + ", payoutAddress=" + payoutAddress
                + ", receivedAmount=" + receivedAmount + " duffs, receiveTime=" + receiveTime + "]";
    }

}
