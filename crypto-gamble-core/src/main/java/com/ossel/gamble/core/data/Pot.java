package com.ossel.gamble.core.data;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.utils.CoreUtil;

public class Pot {

    public final static String LABEL_OPEN_SLOT = "open slot ";

    private CryptoCurrency currency;

    private int expectedParticipants;

    private long expectedBettingAmount;

    private Date createTime = new Date();

    private List<Participant> participants = new ArrayList<Participant>();

    private Date closingTime;

    private int closingBlockHeight;

    private String closingBlockHash;

    private int payoutBlockHeight;

    private Block finalPayoutBlock;

    private String payoutTxnId;

    private boolean payoutStarted;

    private Exception payoutError;

    private Participant winner;

    private int winnerIndex = -1;

    public Pot(CryptoCurrency currency, int expectedParticipants, long expectedBettingamount) {
        super();
        this.expectedBettingAmount = expectedBettingamount;
        this.expectedParticipants = expectedParticipants;
        this.currency = currency;
    }


    public CryptoCurrency getCurrency() {
        return currency;
    }



    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public long getExpectedBettingamount() {
        return expectedBettingAmount;
    }

    public void setExpectedBettingamount(long expectedBettingamount) {
        this.expectedBettingAmount = expectedBettingamount;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public long getCurrentAmount() {
        long amount = 0;
        for (Participant participant : participants) {
            amount += participant.getReceivedAmount();
        }
        return amount;
    }

    public boolean isFull() {
        return participants.size() >= expectedParticipants;
    }

    public long getPotLimit() {
        return expectedParticipants * expectedBettingAmount;
    }

    public int getPayoutBlockHeight() {
        return payoutBlockHeight;
    }

    public String getClosingBlockHash() {
        return closingBlockHash;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public Block getFinalPayoutBlock() {
        return finalPayoutBlock;
    }

    public Participant getWinner() {
        return winner;
    }

    public String getPayoutTxnId() {
        return payoutTxnId;
    }

    public void setPayoutTxnId(String payoutTxnId) {
        this.payoutTxnId = payoutTxnId;
    }

    public Exception getPayoutError() {
        return payoutError;
    }

    public void setPayoutError(Exception payoutError) {
        this.payoutError = payoutError;
    }

    public String getState() {
        return CoreUtil.getPotState(this);
    }

    public int getNbrOfOpenSlots() {
        return (expectedParticipants - getParticipants().size());
    }

    public int getClosingBlockHeight() {
        return closingBlockHeight;
    }

    public boolean payoutFinished() {
        return (payoutTxnId != null && !payoutTxnId.isEmpty()) || payoutError != null;
    }

    public long getId() {
        return createTime.getTime();
    }

    public Integer getIndex(Participant participant) {
        for (int i = 0; i < participants.size(); i++) {
            if (participant.isBankParticipant()) {
                if (participant.getReceiveTime().equals(participants.get(i).getReceiveTime())) {
                    return i;
                }
            } else {
                if (participant.getDepositAddress()
                        .equals(participants.get(i).getDepositAddress())) {
                    return i;
                }
            }

        }
        return null;
    }

    public int getNbrOfParticipants() {
        return participants.size();
    }

    public void setWinnerIndex(int i) {
        this.winnerIndex = i;
    }

    public void setWinner(Participant participant) {
        this.winner = participant;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public void setFinalPayoutBlock(Block finalPayoutBlock) {
        this.finalPayoutBlock = finalPayoutBlock;
    }

    public boolean isEmpty() {
        return participants.size() == 0;
    }

    public void setPayoutStarted(boolean b) {
        payoutStarted = b;
    }

    public boolean isPayoutStarted() {
        return payoutStarted;
    }

    public int getExpectedParticipants() {
        return expectedParticipants;
    }

    public void setclosingTime(Date time) {
        this.closingTime = time;
    }

    public void setClosingBlockHash(String closingBlockHash) {
        this.closingBlockHash = closingBlockHash;
    }

    public void setClosingBlockHeight(int closingBlockHeight) {
        this.closingBlockHeight = closingBlockHeight;
    }

    public void setPayoutBlockHeight(int height) {
        payoutBlockHeight = height;
    }

    @Override
    public String toString() {
        return "Pot [createTime=" + createTime + ", closingTime=" + closingTime
                + ", closingBlockHash=" + closingBlockHash + ", closingBlockHeight="
                + closingBlockHeight + ", payoutBlockHeight=" + payoutBlockHeight + ", payoutTxnId="
                + payoutTxnId + ", payoutError=" + payoutError + ", finalPayoutBlock="
                + finalPayoutBlock + ", participants=" + participants + ", winner=" + winner
                + ", expectedParticipants=" + expectedParticipants + ", expectedBettingamount="
                + expectedBettingAmount + "]";
    }


}
