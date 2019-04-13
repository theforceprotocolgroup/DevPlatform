package com.theforceprotocol.blockchainrpc.ethclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class AccountHelper {
    private Logger logger = LoggerFactory.getLogger(AccountHelper.class);
    private String keystoreDir = "C:\\keystore";

    private void logAccount(String[] tuple) {
        logger.info("Private Key: " + tuple[0]);
        logger.info("Public Key: " + tuple[1]);
        logger.info("address: " + tuple[2]);
    }

    private String[] getAccountTuple(ECKeyPair keyPair) {
        return new String[]{
                keyPair.getPrivateKey().toString(16),
                keyPair.getPublicKey().toString(16),
                Keys.getAddress(keyPair)
        };
    }

    public String[] newAccount() throws Exception {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    public String[] importPrivateKey(String privateKey) throws Exception {
        BigInteger key = new BigInteger(privateKey, 16);
        ECKeyPair keyPair = ECKeyPair.create(key);
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    public String newWalletFile(String password) throws Exception {
        File dest = new File(keystoreDir);
        String walletFileName = WalletUtils.generateNewWalletFile(password, dest, true);
        logger.info("Wallet file: " + walletFileName);
        return walletFileName;
    }

    public String[] loadWalletFile(String password, String walletFileName) throws Exception {
        String src = keystoreDir + "/" + walletFileName;
        Credentials credentials = WalletUtils.loadCredentials(password, src);
        ECKeyPair keyPair = credentials.getEcKeyPair();
        String[] tuple = getAccountTuple(keyPair);
        logAccount(tuple);
        return tuple;
    }

    private void logBip39Wallet(Bip39Wallet wallet) {
        logger.info("Bip39 wallet file: " + wallet.getFilename());
        logger.info("Bip39 wallet mnemonic: " + wallet.getMnemonic());
    }

    public String[] newBip39Wallet(String password) throws Exception {
        File dest = new File(keystoreDir);
        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(password, dest);
        logBip39Wallet(wallet);
        return new String[]{wallet.getFilename(), wallet.getMnemonic()};
    }

    private void logAccounts(List<String> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            logger.info("account " + i + ": " + accounts.get(i));
        }
    }

    public String[] getNodeAccounts() throws Exception {
        Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/opPa"));
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        logAccounts(accounts);
        return accounts.toArray(new String[accounts.size()]);
    }

    public static void main(String[] args) throws Exception {
        AccountHelper accountHelper = new AccountHelper();
        //创建密钥对
        String[] tuple = accountHelper.newAccount();
        //导入私钥
        String[] tuple2 = accountHelper.importPrivateKey(tuple[0]);
        //创建钱包文件
        String walletFileName = accountHelper.newWalletFile("123");
        //载入钱包文件，创建账户凭证
        accountHelper.loadWalletFile("123", walletFileName);
        //查看节点账户
        accountHelper.getNodeAccounts();
    }
}
