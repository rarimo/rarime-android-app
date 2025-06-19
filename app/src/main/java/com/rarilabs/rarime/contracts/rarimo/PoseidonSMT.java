package com.rarilabs.rarime.contracts.rarimo;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.abi.datatypes.reflection.Parameterized;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Flowable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.0.
 */
@SuppressWarnings("rawtypes")
public class PoseidonSMT extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ROOT_VALIDITY = "ROOT_VALIDITY";

    public static final String FUNC_UPGRADE_INTERFACE_VERSION = "UPGRADE_INTERFACE_VERSION";

    public static final String FUNC___POSEIDONSMT_INIT = "__PoseidonSMT_init";

    public static final String FUNC_ADD = "add";

    public static final String FUNC_EVIDENCEREGISTRY = "evidenceRegistry";

    public static final String FUNC_GETNODEBYKEY = "getNodeByKey";

    public static final String FUNC_GETPROOF = "getProof";

    public static final String FUNC_GETROOT = "getRoot";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISROOTLATEST = "isRootLatest";

    public static final String FUNC_ISROOTVALID = "isRootValid";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_STATEKEEPER = "stateKeeper";

    public static final String FUNC_UPDATE = "update";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final Event INITIALIZED_EVENT = new Event("Initialized",
            List.of(new TypeReference<Uint64>() {
            }));

    public static final Event ROOTUPDATED_EVENT = new Event("RootUpdated",
            List.of(new TypeReference<Bytes32>() {
            }));

    public static final Event UPGRADED_EVENT = new Event("Upgraded",
            List.of(new TypeReference<Address>(true) {
            }));

    @Deprecated
    protected PoseidonSMT(String contractAddress, Web3j web3j, Credentials credentials,
                          BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PoseidonSMT(String contractAddress, Web3j web3j, Credentials credentials,
                          ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PoseidonSMT(String contractAddress, Web3j web3j,
                          TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PoseidonSMT(String contractAddress, Web3j web3j,
                          TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<InitializedEventResponse> getInitializedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InitializedEventResponse getInitializedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INITIALIZED_EVENT, log);
        InitializedEventResponse typedResponse = new InitializedEventResponse();
        typedResponse.log = log;
        typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public static List<RootUpdatedEventResponse> getRootUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROOTUPDATED_EVENT, transactionReceipt);
        ArrayList<RootUpdatedEventResponse> responses = new ArrayList<RootUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RootUpdatedEventResponse typedResponse = new RootUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.root = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RootUpdatedEventResponse getRootUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROOTUPDATED_EVENT, log);
        RootUpdatedEventResponse typedResponse = new RootUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.root = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public static List<UpgradedEventResponse> getUpgradedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UPGRADED_EVENT, transactionReceipt);
        ArrayList<UpgradedEventResponse> responses = new ArrayList<UpgradedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradedEventResponse typedResponse = new UpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UpgradedEventResponse getUpgradedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UPGRADED_EVENT, log);
        UpgradedEventResponse typedResponse = new UpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    @Deprecated
    public static PoseidonSMT load(String contractAddress, Web3j web3j, Credentials credentials,
                                   BigInteger gasPrice, BigInteger gasLimit) {
        return new PoseidonSMT(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PoseidonSMT load(String contractAddress, Web3j web3j,
                                   TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PoseidonSMT(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PoseidonSMT load(String contractAddress, Web3j web3j, Credentials credentials,
                                   ContractGasProvider contractGasProvider) {
        return new PoseidonSMT(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PoseidonSMT load(String contractAddress, Web3j web3j,
                                   TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PoseidonSMT(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInitializedEventFromLog(log));
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventFlowable(filter);
    }

    public Flowable<RootUpdatedEventResponse> rootUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRootUpdatedEventFromLog(log));
    }

    public Flowable<RootUpdatedEventResponse> rootUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROOTUPDATED_EVENT));
        return rootUpdatedEventFlowable(filter);
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUpgradedEventFromLog(log));
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(DefaultBlockParameter startBlock,
                                                                 DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADED_EVENT));
        return upgradedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> ROOT_VALIDITY() {
        final Function function = new Function(FUNC_ROOT_VALIDITY,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> UPGRADE_INTERFACE_VERSION() {
        final Function function = new Function(FUNC_UPGRADE_INTERFACE_VERSION,
                List.of(),
                List.of(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> __PoseidonSMT_init(String stateKeeper_,
                                                                     String evidenceRegistry_, BigInteger treeHeight_) {
        final Function function = new Function(
                FUNC___POSEIDONSMT_INIT,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, stateKeeper_),
                        new org.web3j.abi.datatypes.Address(160, evidenceRegistry_),
                        new org.web3j.abi.datatypes.generated.Uint256(treeHeight_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> add(byte[] keyOfElement_, byte[] element_) {
        final Function function = new Function(
                FUNC_ADD,
                Arrays.asList(new org.web3j.abi.datatypes.generated.Bytes32(keyOfElement_),
                        new org.web3j.abi.datatypes.generated.Bytes32(element_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> evidenceRegistry() {
        final Function function = new Function(FUNC_EVIDENCEREGISTRY,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Node> getNodeByKey(byte[] key_) {
        final Function function = new Function(FUNC_GETNODEBYKEY,
                List.of(new Bytes32(key_)),
                List.of(new TypeReference<Node>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Node.class);
    }

    public RemoteFunctionCall<Proof> getProof(byte[] key_) {
        byte[] index = new byte[32];
        // Fill the index array with zeros
        Arrays.fill(index, (byte) 0);

        // Copy the key array into the index array starting at the correct position
        System.arraycopy(key_, 0, index, 32 - key_.length, key_.length);

        final Function function = new Function(FUNC_GETPROOF,
                List.of(new Bytes32(index)),
                List.of(new TypeReference<Proof>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Proof.class);
    }

    public RemoteFunctionCall<byte[]> getRoot() {
        final Function function = new Function(FUNC_GETROOT,
                List.of(),
                List.of(new TypeReference<Bytes32>() {
                }));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> isRootLatest(byte[] root_) {
        final Function function = new Function(FUNC_ISROOTLATEST,
                List.of(new Bytes32(root_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isRootValid(byte[] root_) {
        final Function function = new Function(FUNC_ISROOTVALID,
                List.of(new Bytes32(root_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID,
                List.of(),
                List.of(new TypeReference<Bytes32>() {
                }));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> remove(byte[] keyOfElement_) {
        final Function function = new Function(
                FUNC_REMOVE,
                List.of(new Bytes32(keyOfElement_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> stateKeeper() {
        final Function function = new Function(FUNC_STATEKEEPER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> update(byte[] keyOfElement_, byte[] newElement_) {
        final Function function = new Function(
                FUNC_UPDATE,
                Arrays.asList(new org.web3j.abi.datatypes.generated.Bytes32(keyOfElement_),
                        new org.web3j.abi.datatypes.generated.Bytes32(newElement_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCall(String newImplementation,
                                                                   byte[] data, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALL,
                Arrays.asList(new org.web3j.abi.datatypes.Address(160, newImplementation),
                        new org.web3j.abi.datatypes.DynamicBytes(data)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public static class Node extends StaticStruct {
        public BigInteger nodeType;

        public BigInteger childLeft;

        public BigInteger childRight;

        public byte[] nodeHash;

        public byte[] key;

        public byte[] value;

        public Node(BigInteger nodeType, BigInteger childLeft, BigInteger childRight,
                    byte[] nodeHash, byte[] key, byte[] value) {
            super(new org.web3j.abi.datatypes.generated.Uint8(nodeType),
                    new org.web3j.abi.datatypes.generated.Uint64(childLeft),
                    new org.web3j.abi.datatypes.generated.Uint64(childRight),
                    new org.web3j.abi.datatypes.generated.Bytes32(nodeHash),
                    new org.web3j.abi.datatypes.generated.Bytes32(key),
                    new org.web3j.abi.datatypes.generated.Bytes32(value));
            this.nodeType = nodeType;
            this.childLeft = childLeft;
            this.childRight = childRight;
            this.nodeHash = nodeHash;
            this.key = key;
            this.value = value;
        }

        public Node(Uint8 nodeType, Uint64 childLeft, Uint64 childRight, Bytes32 nodeHash,
                    Bytes32 key, Bytes32 value) {
            super(nodeType, childLeft, childRight, nodeHash, key, value);
            this.nodeType = nodeType.getValue();
            this.childLeft = childLeft.getValue();
            this.childRight = childRight.getValue();
            this.nodeHash = nodeHash.getValue();
            this.key = key.getValue();
            this.value = value.getValue();
        }
    }

    public static class Proof extends DynamicStruct implements Serializable {
        public byte[] root;

        public List<byte[]> siblings;

        public Boolean existence;

        public byte[] key;

        public byte[] value;

        public Boolean auxExistence;

        public byte[] auxKey;

        public byte[] auxValue;

        public Proof(byte[] root, List<byte[]> siblings, Boolean existence, byte[] key,
                     byte[] value, Boolean auxExistence, byte[] auxKey, byte[] auxValue) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(root),
                    new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                            org.web3j.abi.datatypes.generated.Bytes32.class,
                            org.web3j.abi.Utils.typeMap(siblings, org.web3j.abi.datatypes.generated.Bytes32.class)),
                    new org.web3j.abi.datatypes.Bool(existence),
                    new org.web3j.abi.datatypes.generated.Bytes32(key),
                    new org.web3j.abi.datatypes.generated.Bytes32(value),
                    new org.web3j.abi.datatypes.Bool(auxExistence),
                    new org.web3j.abi.datatypes.generated.Bytes32(auxKey),
                    new org.web3j.abi.datatypes.generated.Bytes32(auxValue));
            this.root = root;
            this.siblings = siblings;
            this.existence = existence;
            this.key = key;
            this.value = value;
            this.auxExistence = auxExistence;
            this.auxKey = auxKey;
            this.auxValue = auxValue;
        }

        public Proof(Bytes32 root,
                     @Parameterized(type = Bytes32.class) DynamicArray<Bytes32> siblings, Bool existence,
                     Bytes32 key, Bytes32 value, Bool auxExistence, Bytes32 auxKey, Bytes32 auxValue) {
            super(root, siblings, existence, key, value, auxExistence, auxKey, auxValue);
            this.root = root.getValue();
            this.siblings = siblings.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
            this.existence = existence.getValue();
            this.key = key.getValue();
            this.value = value.getValue();
            this.auxExistence = auxExistence.getValue();
            this.auxKey = auxKey.getValue();
            this.auxValue = auxValue.getValue();
        }
    }

    public static class InitializedEventResponse extends BaseEventResponse {
        public BigInteger version;
    }

    public static class RootUpdatedEventResponse extends BaseEventResponse {
        public byte[] root;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }
}
