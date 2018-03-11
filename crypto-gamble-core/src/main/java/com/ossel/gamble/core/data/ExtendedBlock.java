package com.ossel.gamble.core.data;

public class ExtendedBlock extends Block {

    private String prefix;
    private String suffix;

    public ExtendedBlock(String blockHash) {
        super(blockHash, -1);
        int position = blockHash.length() - 1;
        while (position > 0) {
            char c = blockHash.charAt(position);
            int value = (int) c;
            if (value >= 48 && value <= 57) {// numeric
                this.winner = Integer.parseInt(String.valueOf(c));
                break;
            }
            position--;
        }
        this.prefix = blockHash.substring(0, position);
        this.suffix = blockHash.substring(position + 1, blockHash.length());
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "Block [blockHash=" + blockHash + ", prefix=" + prefix + ", winner=" + winner
                + ", suffix=" + suffix + "]";
    }

}
