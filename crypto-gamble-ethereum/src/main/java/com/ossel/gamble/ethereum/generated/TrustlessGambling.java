package com.ossel.gamble.ethereum.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class TrustlessGambling extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60006003819055506000600e60006101000a81548160ff0219169083151502179055506000600f81905550610b1a806100496000396000f3006060604052600436106100fc576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630adde0eb146101015780632591ef891461018f5780632a25d913146101f25780632e844205146102215780634427b2731461024a578063534a294c1461027357806363bd1d4a146102a257806366f3b200146102b75780638a21d503146102e05780638d16a06314610309578063d0e30db014610332578063d341e9f11461033c578063db2d641d14610369578063dfbf53ae1461039a578063e75bc258146103c3578063f340fa01146103ec578063f48707741461041a578063feac0a591461047d575b600080fd5b341561010c57600080fd5b6101146104a6565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610154578082015181840152602081019050610139565b50505050905090810190601f1680156101815780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561019a57600080fd5b6101b06004808035906020019091905050610544565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34156101fd57600080fd5b610205610579565b604051808260ff1660ff16815260200191505060405180910390f35b341561022c57600080fd5b61023461057e565b6040518082815260200191505060405180910390f35b341561025557600080fd5b61025d610584565b6040518082815260200191505060405180910390f35b341561027e57600080fd5b61028661058a565b604051808260ff1660ff16815260200191505060405180910390f35b34156102ad57600080fd5b6102b561058f565b005b34156102c257600080fd5b6102ca6107c4565b6040518082815260200191505060405180910390f35b34156102eb57600080fd5b6102f36107ca565b6040518082815260200191505060405180910390f35b341561031457600080fd5b61031c6107d0565b6040518082815260200191505060405180910390f35b61033a6107d6565b005b341561034757600080fd5b61034f6107e1565b604051808215151515815260200191505060405180910390f35b341561037457600080fd5b61037c6107f4565b60405180826000191660001916815260200191505060405180910390f35b34156103a557600080fd5b6103ad6107fa565b6040518082815260200191505060405180910390f35b34156103ce57600080fd5b6103d6610800565b6040518082815260200191505060405180910390f35b610418600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610806565b005b341561042557600080fd5b61043b6004808035906020019091905050610a0e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561048857600080fd5b610490610a43565b6040518082815260200191505060405180910390f35b60008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561053c5780601f106105115761010080835404028352916020019161053c565b820191906000526020600020905b81548152906001019060200180831161051f57829003601f168201915b505050505081565b60048160038110151561055357fe5b016000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600381565b600a5481565b60015481565b600181565b600080600e60009054906101000a900460ff1615156105aa57fe5b600b54431115156105b757fe5b43600181905550600b5440600c81600019169055506000600102600c5460001916141561068e57608060405190810160405280605f81526020017f43616e206e6f74207061796f7574206265636175736520636865636b5061796f81526020017f75742d66756e6374696f6e207761732074726967676572656420746f6f206c6181526020017f74652e2046756e64732061726520616464656420746f206e657720706f742e0081525060009080519060200190610676929190610a49565b50600f6000815480929190600101919050555061079d565b6040805190810160405280601181526020017f5061796f75742074726967676572656421000000000000000000000000000000815250600090805190602001906106d9929190610a49565b50600c5460019004600281905550600360ff16600c54600190048115156106fc57fe5b06600d819055506007600d5460038110151561071457fe5b0160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169150600360ff166103e8029050600f54600360ff166103e80202810190508173ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050151561079457600080fd5b6000600f819055505b6000600e60006101000a81548160ff02191690831515021790555060006003819055505050565b600b5481565b6103e881565b60025481565b6107df33610806565b565b600e60009054906101000a900460ff1681565b600c5481565b600d5481565b600f5481565b6103e83414151561081357fe5b600e60009054906101000a900460ff1615151561082c57fe5b6040805190810160405280600b81526020017f6e6577206465706f73697400000000000000000000000000000000000000000081525060009080519060200190610877929190610a49565b5033600460035460038110151561088a57fe5b0160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508060076003546003811015156108db57fe5b0160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506003600081548092919060010191905055507f5548c837ab068cf56a2c2479df0882a4922fd203edb7517321831d95078c5f62338234604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001828152602001935050505060405180910390a1600360ff166003541415610a0b5743600a81905550600160ff16600a5401600b819055506001600e60006101000a81548160ff0219169083151502179055505b50565b600781600381101515610a1d57fe5b016000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60035481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610a8a57805160ff1916838001178555610ab8565b82800160010185558215610ab8579182015b82811115610ab7578251825591602001919060010190610a9c565b5b509050610ac59190610ac9565b5090565b610aeb91905b80821115610ae7576000816000905550600101610acf565b5090565b905600a165627a7a72305820305b8a2596a580dbf9ce6656c7c415ad778d8edd6491627b58a6b41668f8f6b90029";

    protected TrustlessGambling(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TrustlessGambling(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<DepositEventResponse> getDepositEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Deposit", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<DepositEventResponse> responses = new ArrayList<DepositEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            DepositEventResponse typedResponse = new DepositEventResponse();
            typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DepositEventResponse> depositEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Deposit", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, DepositEventResponse>() {
            @Override
            public DepositEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                DepositEventResponse typedResponse = new DepositEventResponse();
                typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public List<PayoutEventResponse> getPayoutEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Payout", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PayoutEventResponse> responses = new ArrayList<PayoutEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PayoutEventResponse typedResponse = new PayoutEventResponse();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PayoutEventResponse> payoutEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Payout", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PayoutEventResponse>() {
            @Override
            public PayoutEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PayoutEventResponse typedResponse = new PayoutEventResponse();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> _message() {
        Function function = new Function("_message", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> depositAddresses(BigInteger param0) {
        Function function = new Function("depositAddresses", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> NBR_OF_SLOTS() {
        Function function = new Function("NBR_OF_SLOTS", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> closingBlockNumber() {
        Function function = new Function("closingBlockNumber", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> _blockNumberOnPayout() {
        Function function = new Function("_blockNumberOnPayout", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> PAYOUT_BLOCK_OFFSET() {
        Function function = new Function("PAYOUT_BLOCK_OFFSET", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> payout() {
        Function function = new Function(
                "payout", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> payoutBlockNumber() {
        Function function = new Function("payoutBlockNumber", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> EXPECTED_POT_AMOUNT() {
        Function function = new Function("EXPECTED_POT_AMOUNT", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> _blockHashAsInteger() {
        Function function = new Function("_blockHashAsInteger", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> deposit(BigInteger weiValue) {
        Function function = new Function(
                "deposit", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Boolean> potClosed() {
        Function function = new Function("potClosed", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<byte[]> payoutBlockHash() {
        Function function = new Function("payoutBlockHash", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<BigInteger> winner() {
        Function function = new Function("winner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> nbrOfMissedPayouts() {
        Function function = new Function("nbrOfMissedPayouts", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> deposit(String _payout, BigInteger weiValue) {
        Function function = new Function(
                "deposit", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_payout)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> payoutAddresses(BigInteger param0) {
        Function function = new Function("payoutAddresses", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> nbrOfParticipants() {
        Function function = new Function("nbrOfParticipants", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<TrustlessGambling> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TrustlessGambling.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TrustlessGambling> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TrustlessGambling.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static TrustlessGambling load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TrustlessGambling(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TrustlessGambling load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TrustlessGambling(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class DepositEventResponse {
        public String from;

        public String to;

        public BigInteger amount;
    }

    public static class PayoutEventResponse {
        public String to;

        public BigInteger amount;
    }
}
