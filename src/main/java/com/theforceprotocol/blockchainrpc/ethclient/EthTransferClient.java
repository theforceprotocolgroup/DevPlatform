package com.theforceprotocol.blockchainrpc.ethclient;

import com.theforceprotocol.blockchainrpc.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.web3j.abi.datatypes.*;

import java.util.Arrays;
import java.util.List;

/**
 * ETH转账
 *
 * @author Mingliang
 * @date 2019/2/14 18:17
 **/
public class EthTransferClient {
    private static Logger logger = LoggerFactory.getLogger(EthTransferClient.class);
    private static Web3j web3j = Web3JClient.getClient();
    private final static String PRIVATE_KEY = "";
    private static EthTransferClient ethInstance;

    public static EthTransferClient EthInstance() {
        if (ethInstance == null) {
            ethInstance = new EthTransferClient();
        }
        return ethInstance;
    }

    public TransactionRecord transferETHToAddress(String toAddress, String amount) {
        return transferETHToAddress(PRIVATE_KEY, toAddress, amount);
    }

    public TransactionRecord transferETHToAddress(String fromPrivateKey, String toAddress, String amount) {
        Credentials credentials = Credentials.create(fromPrivateKey);
        BigDecimal value = new BigDecimal(amount);
        try {
            return sendCoins(credentials, toAddress, value);
        } catch (Exception e) {
            e.printStackTrace();
            return TransactionRecord.fail();
        }
    }

    public TransactionRecord transferERC20(String fromPrivateKey, String toAddress, String contractAddress, String amount) {
        Credentials credentials = Credentials.create(fromPrivateKey);
        BigDecimal value = new BigDecimal(amount);
        try {
            return sendERC20(credentials, contractAddress, toAddress, value);
        } catch (Exception e) {
            e.printStackTrace();
            return TransactionRecord.fail();
        }
    }

    public static String encodeTransferData(String toAddress, BigInteger sum) {
        Function function = new Function(
                "transfer",  // function we're calling
                Arrays.asList(new Address(toAddress), new Uint256(sum)),  // Parameters to pass as Solidity Types
                Arrays.asList(new org.web3j.abi.TypeReference<Bool>() {}));
        return FunctionEncoder.encode(function);
    }

    private TransactionRecord sendCoins(Credentials credentials, String toAddress, BigDecimal value) throws Exception {
        Convert.Unit unit = Convert.Unit.ETHER;
        String tokenValue = String.valueOf(value);
        BigDecimal weiValue = Convert.toWei(tokenValue, unit);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Wei value provided: " + tokenValue + " " + unit.toString()
                            + " = " + weiValue + " Wei");
        }
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("nonce:" + nonce);
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, DefaultGasProvider.GAS_PRICE, Transfer.GAS_LIMIT, toAddress, weiValue.toBigIntegerExact());

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("hexValue:" + hexValue);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction == null) {
            logger.error("没有获取到ETH交易哈希！");
            return TransactionRecord.fail();
        }
        String transactionHash = ethSendTransaction.getTransactionHash();
        logger.info("此笔转账hash地址为：" + transactionHash);
        if (ethSendTransaction.getError() != null) {
            if (ethSendTransaction.getError().getCode() != 1) {
                logger.error("转账失败！");
                logger.error("error data:" + ethSendTransaction.getError().getData());
                logger.error("error msg:" + ethSendTransaction.getError().getMessage());
                logger.error("error code:" + ethSendTransaction.getError().getCode());
                logger.error(ethSendTransaction.getError().getMessage());
                return TransactionRecord.fail(transactionHash);
            }
        }
        return TransactionRecord.success(transactionHash);
    }

    private TransactionRecord sendERC20(Credentials credentials, String contractAddress, String toAddress, BigDecimal value) throws Exception {
        Convert.Unit unit = Convert.Unit.ETHER;
        String tokenValue = String.valueOf(value);
        BigDecimal weiValue = Convert.toWei(tokenValue, unit);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Wei value provided: " + tokenValue + " " + unit.toString()
                            + " = " + weiValue + " Wei");
        }
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("nonce:" + nonce);
        BigInteger sum = value.toBigIntegerExact(); // amount you want to send
        String data = encodeTransferData(toAddress, sum);
        BigInteger gasLimit = BigInteger.valueOf(120000); // set gas limit here
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, gasLimit, contractAddress, data);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("hexValue:" + hexValue);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction == null) {
            logger.error("没有获取到ETH交易哈希！");
            return TransactionRecord.fail();
        }
        String transactionHash = ethSendTransaction.getTransactionHash();
        logger.info("此笔转账hash地址为：" + transactionHash);
        if (ethSendTransaction.getError() != null) {
            if (ethSendTransaction.getError().getCode() != 1) {
                logger.error("转账失败！");
                logger.error("error data:" + ethSendTransaction.getError().getData());
                logger.error("error msg:" + ethSendTransaction.getError().getMessage());
                logger.error("error code:" + ethSendTransaction.getError().getCode());
                logger.error(ethSendTransaction.getError().getMessage());
                return TransactionRecord.fail(transactionHash);
            }
        }
        return TransactionRecord.success(transactionHash);
    }

}
