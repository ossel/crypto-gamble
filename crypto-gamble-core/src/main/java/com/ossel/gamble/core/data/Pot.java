package com.ossel.gamble.core.data;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ossel.gamble.core.data.enums.CryptoCurrency;
import com.ossel.gamble.core.utils.ReceiveTimeComparator;

public class Pot {

    // private static final Logger log = Logger.getLogger(Pot.class);

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public final static String LABEL_OPEN_SLOT = "open slot ";

    private CryptoCurrency currency;

    private Date createTime = new Date();
    private Date closingTime;

    private String closingBlockHash;

    private int closingBlockHeight;

    private int payoutBlockOffset = 1;
    private int payoutBlockHeight;

    private String payoutTxnId;
    private Exception payoutError;

    private Block tmpPayoutBlock;
    private Block finalPayoutBlock;

    private List<Participant> participants = new ArrayList<Participant>();
    private Participant winner;
    private int winnerIndex = -1;
    private int expectedParticipants;

    private long expectedBettingAmount;

    private boolean payoutStarted;

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


    public void close(Date time, String closingBlockHash, int closingBlockHeight) {
        this.closingTime = time;
        this.closingBlockHash = closingBlockHash;
        this.closingBlockHeight = closingBlockHeight;
        this.payoutBlockHeight = this.closingBlockHeight + this.payoutBlockOffset;
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

    public Block getTmpPayoutBlock() {
        if (finalPayoutBlock != null)
            return finalPayoutBlock;
        return tmpPayoutBlock;
    }

    public void setTmpPayoutBlock(Block tmpPayoutBlock) {
        this.tmpPayoutBlock = tmpPayoutBlock;
    }

    public Block getFinalPayoutBlock() {
        return finalPayoutBlock;
    }

    private Participant decideWinner(int winner) {
        List<Participant> actualParticipants = getParticipants();
        actualParticipants.sort(new ReceiveTimeComparator());
        winnerIndex = winner % actualParticipants.size();
        return actualParticipants.get(winnerIndex);
    }

    public Participant selectWinner() {
        if (finalPayoutBlock != null) {
            this.winner = this.decideWinner(finalPayoutBlock.getWinner());
        } else if (tmpPayoutBlock != null) {
            this.winner = this.decideWinner(tmpPayoutBlock.getWinner());
        }
        return this.winner;
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
        if (payoutTxnId != null && payoutError == null && winner != null) {
            return "Pot closed. Winner is " + winner.getDisplayName() + ".";
        }
        if (payoutError != null) {
            return "Pot closed. Winner is " + winner.getDisplayName() + ". "
                    + payoutError.getMessage();
        }

        if (winner != null && winner.isBankParticipant()) {
            return "Pot closed. Winner is the bank: " + winner.getPseudonym();
        }

        if (finalPayoutBlock != null && winner != null) {
            return "Pot closed. Winner is " + winner.getDisplayName() + ". Triggering payout!";
        }

        if (tmpPayoutBlock != null && winner != null) {
            return "Pot closed. Winner is " + winner.getDisplayName()
                    + ". Waiting for another block to be sure before triggering payout.";
        }

        if (closingTime != null) {
            return "Pot closed. Waiting for block " + payoutBlockHeight + " to select the winner.";
        }
        return "Pot open. Waiting for " + getNbrOfOpenSlots() + " more participant"
                + (getNbrOfOpenSlots() == 1 ? "." : "s.");
    }

    public int getNbrOfOpenSlots() {
        return (expectedParticipants - getParticipants().size());
    }

    public int getClosingBlockHeight() {
        return closingBlockHeight;
    }

    public int getPayoutBlockOffset() {
        return payoutBlockOffset;
    }

    @Override
    public String toString() {
        return "Pot [createTime=" + createTime + ", closingTime=" + closingTime
                + ", closingBlockHash=" + closingBlockHash + ", closingBlockHeight="
                + closingBlockHeight + ", payoutBlockOffset=" + payoutBlockOffset
                + ", payoutBlockHeight=" + payoutBlockHeight + ", payoutTxnId=" + payoutTxnId
                + ", payoutError=" + payoutError + ", tmpPayoutBlock=" + tmpPayoutBlock
                + ", finalPayoutBlock=" + finalPayoutBlock + ", participants=" + participants
                + ", winner=" + winner + ", expectedParticipants=" + expectedParticipants
                + ", expectedBettingamount=" + expectedBettingAmount + "]";
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
}
