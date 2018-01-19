package com.ossel.gamble.core.data;

import java.util.Date;

public class Block {

    protected Date createTime = new Date();
    protected String blockHash;
    protected int winner;

    public Block(String blockHash, int winner) {
        super();
        this.blockHash = blockHash;
        this.winner = winner;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

}
