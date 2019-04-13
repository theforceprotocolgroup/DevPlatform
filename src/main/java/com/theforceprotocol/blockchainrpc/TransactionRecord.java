package com.theforceprotocol.blockchainrpc;

/**
 * 交易成功
 *
 * @author Mingliang
 * @date 2019/3/1 18:26
 **/
public class TransactionRecord {
    public final static Integer SUCCESS_CODE = 200;
    private final static Integer FAIL_CODE = 500;

    private Integer code;

    private String txHash;

    private String description;

    public static TransactionRecord success(String txHash) {
        TransactionRecord tr = new TransactionRecord();
        tr.setCode(SUCCESS_CODE);
        tr.setTxHash(txHash);
        return tr;
    }

    public static TransactionRecord fail() {
        return fail("转账失败");
    }

    public static TransactionRecord fail(String txHash) {
        TransactionRecord tr = new TransactionRecord();
        tr.setCode(FAIL_CODE);
        tr.setTxHash(txHash);
        return tr;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
