package com.rarilabs.rarime.contracts.rarimo;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.StaticArray2;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
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
public class FaceRegistry extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_EVENT_ID = "EVENT_ID";

    public static final String FUNC_FACE_PROOF_SIGNALS_COUNT = "FACE_PROOF_SIGNALS_COUNT";

    public static final String FUNC_ROOT_VALIDITY = "ROOT_VALIDITY";

    public static final String FUNC_RULES_PROOF_SIGNALS_COUNT = "RULES_PROOF_SIGNALS_COUNT";

    public static final String FUNC_UPGRADE_INTERFACE_VERSION = "UPGRADE_INTERFACE_VERSION";

    public static final String FUNC___FACEREGISTRY_INIT = "__FaceRegistry_init";

    public static final String FUNC_ADDORACLES = "addOracles";

    public static final String FUNC_ADDOWNERS = "addOwners";

    public static final String FUNC_EVIDENCEREGISTRY = "evidenceRegistry";

    public static final String FUNC_FACEVERIFIER = "faceVerifier";

    public static final String FUNC_GETFEATUREHASH = "getFeatureHash";

    public static final String FUNC_GETMINTHRESHOLD = "getMinThreshold";

    public static final String FUNC_GETNODEBYKEY = "getNodeByKey";

    public static final String FUNC_GETORACLES = "getOracles";

    public static final String FUNC_GETOWNERS = "getOwners";

    public static final String FUNC_GETPROOF = "getProof";

    public static final String FUNC_GETROOT = "getRoot";

    public static final String FUNC_GETRULE = "getRule";

    public static final String FUNC_GETVERIFICATIONNONCE = "getVerificationNonce";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISFEATUREHASHUSED = "isFeatureHashUsed";

    public static final String FUNC_ISORACLE = "isOracle";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_ISROOTLATEST = "isRootLatest";

    public static final String FUNC_ISROOTVALID = "isRootValid";

    public static final String FUNC_ISUSERREGISTERED = "isUserRegistered";

    public static final String FUNC_MINTHRESHOLD = "minThreshold";

    public static final String FUNC_NONCES = "nonces";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REGISTERUSER = "registerUser";

    public static final String FUNC_REMOVEORACLES = "removeOracles";

    public static final String FUNC_REMOVEOWNERS = "removeOwners";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_ROOTS = "roots";

    public static final String FUNC_RULES = "rules";

    public static final String FUNC_RULESVERIFIER = "rulesVerifier";

    public static final String FUNC_SETFACEVERIFIER = "setFaceVerifier";

    public static final String FUNC_SETMINTHRESHOLD = "setMinThreshold";

    public static final String FUNC_SETRULESVERIFIER = "setRulesVerifier";

    public static final String FUNC_UPDATERULE = "updateRule";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final String FUNC_USEDFEATUREHASHES = "usedFeatureHashes";

    public static final String FUNC_USERREGISTRYHASH = "userRegistryHash";

    public static final Event INITIALIZED_EVENT = new Event("Initialized",
            List.of(new TypeReference<Uint64>() {
            }));

    public static final Event MINTHRESHOLDUPDATED_EVENT = new Event("MinThresholdUpdated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event OWNERSADDED_EVENT = new Event("OwnersAdded",
            List.of(new TypeReference<DynamicArray<Address>>() {
            }));

    public static final Event OWNERSREMOVED_EVENT = new Event("OwnersRemoved",
            List.of(new TypeReference<DynamicArray<Address>>() {
            }));

    public static final Event ROOTUPDATED_EVENT = new Event("RootUpdated",
            List.of(new TypeReference<Bytes32>() {
            }));

    public static final Event RULESUPDATED_EVENT = new Event("RulesUpdated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event RULESVERIFIERUPDATED_EVENT = new Event("RulesVerifierUpdated",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));

    public static final Event UPGRADED_EVENT = new Event("Upgraded",
            List.of(new TypeReference<Address>(true) {
            }));

    public static final Event USERREGISTERED_EVENT = new Event("UserRegistered",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event VERIFIERUPDATED_EVENT = new Event("VerifierUpdated",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));

    @Deprecated
    protected FaceRegistry(String contractAddress, Web3j web3j, Credentials credentials,
                           BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FaceRegistry(String contractAddress, Web3j web3j, Credentials credentials,
                           ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FaceRegistry(String contractAddress, Web3j web3j,
                           TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FaceRegistry(String contractAddress, Web3j web3j,
                           TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<InitializedEventResponse> getInitializedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InitializedEventResponse getInitializedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INITIALIZED_EVENT, log);
        InitializedEventResponse typedResponse = new InitializedEventResponse();
        typedResponse.log = log;
        typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
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

    public static List<MinThresholdUpdatedEventResponse> getMinThresholdUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(MINTHRESHOLDUPDATED_EVENT, transactionReceipt);
        ArrayList<MinThresholdUpdatedEventResponse> responses = new ArrayList<MinThresholdUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            MinThresholdUpdatedEventResponse typedResponse = new MinThresholdUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldThreshold = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newThreshold = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static MinThresholdUpdatedEventResponse getMinThresholdUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(MINTHRESHOLDUPDATED_EVENT, log);
        MinThresholdUpdatedEventResponse typedResponse = new MinThresholdUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldThreshold = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newThreshold = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<MinThresholdUpdatedEventResponse> minThresholdUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getMinThresholdUpdatedEventFromLog(log));
    }

    public Flowable<MinThresholdUpdatedEventResponse> minThresholdUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTHRESHOLDUPDATED_EVENT));
        return minThresholdUpdatedEventFlowable(filter);
    }

    public static List<OwnersAddedEventResponse> getOwnersAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSADDED_EVENT, transactionReceipt);
        ArrayList<OwnersAddedEventResponse> responses = new ArrayList<OwnersAddedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnersAddedEventResponse typedResponse = new OwnersAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnersAddedEventResponse getOwnersAddedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSADDED_EVENT, log);
        OwnersAddedEventResponse typedResponse = new OwnersAddedEventResponse();
        typedResponse.log = log;
        typedResponse.newOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<OwnersAddedEventResponse> ownersAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnersAddedEventFromLog(log));
    }

    public Flowable<OwnersAddedEventResponse> ownersAddedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSADDED_EVENT));
        return ownersAddedEventFlowable(filter);
    }

    public static List<OwnersRemovedEventResponse> getOwnersRemovedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSREMOVED_EVENT, transactionReceipt);
        ArrayList<OwnersRemovedEventResponse> responses = new ArrayList<OwnersRemovedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnersRemovedEventResponse typedResponse = new OwnersRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.removedOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnersRemovedEventResponse getOwnersRemovedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSREMOVED_EVENT, log);
        OwnersRemovedEventResponse typedResponse = new OwnersRemovedEventResponse();
        typedResponse.log = log;
        typedResponse.removedOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<OwnersRemovedEventResponse> ownersRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnersRemovedEventFromLog(log));
    }

    public Flowable<OwnersRemovedEventResponse> ownersRemovedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSREMOVED_EVENT));
        return ownersRemovedEventFlowable(filter);
    }

    public static List<RootUpdatedEventResponse> getRootUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROOTUPDATED_EVENT, transactionReceipt);
        ArrayList<RootUpdatedEventResponse> responses = new ArrayList<RootUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RootUpdatedEventResponse typedResponse = new RootUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.root = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RootUpdatedEventResponse getRootUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROOTUPDATED_EVENT, log);
        RootUpdatedEventResponse typedResponse = new RootUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.root = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
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

    public static List<RulesUpdatedEventResponse> getRulesUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(RULESUPDATED_EVENT, transactionReceipt);
        ArrayList<RulesUpdatedEventResponse> responses = new ArrayList<RulesUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RulesUpdatedEventResponse typedResponse = new RulesUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.userAddress = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newState = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RulesUpdatedEventResponse getRulesUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(RULESUPDATED_EVENT, log);
        RulesUpdatedEventResponse typedResponse = new RulesUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.userAddress = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newState = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RulesUpdatedEventResponse> rulesUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRulesUpdatedEventFromLog(log));
    }

    public Flowable<RulesUpdatedEventResponse> rulesUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RULESUPDATED_EVENT));
        return rulesUpdatedEventFlowable(filter);
    }

    public static List<RulesVerifierUpdatedEventResponse> getRulesVerifierUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(RULESVERIFIERUPDATED_EVENT, transactionReceipt);
        ArrayList<RulesVerifierUpdatedEventResponse> responses = new ArrayList<RulesVerifierUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RulesVerifierUpdatedEventResponse typedResponse = new RulesVerifierUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldVerifier = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newVerifier = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RulesVerifierUpdatedEventResponse getRulesVerifierUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(RULESVERIFIERUPDATED_EVENT, log);
        RulesVerifierUpdatedEventResponse typedResponse = new RulesVerifierUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldVerifier = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newVerifier = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RulesVerifierUpdatedEventResponse> rulesVerifierUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRulesVerifierUpdatedEventFromLog(log));
    }

    public Flowable<RulesVerifierUpdatedEventResponse> rulesVerifierUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RULESVERIFIERUPDATED_EVENT));
        return rulesVerifierUpdatedEventFlowable(filter);
    }

    public static List<UpgradedEventResponse> getUpgradedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UPGRADED_EVENT, transactionReceipt);
        ArrayList<UpgradedEventResponse> responses = new ArrayList<UpgradedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UpgradedEventResponse typedResponse = new UpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UpgradedEventResponse getUpgradedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UPGRADED_EVENT, log);
        UpgradedEventResponse typedResponse = new UpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
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

    public static List<UserRegisteredEventResponse> getUserRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(USERREGISTERED_EVENT, transactionReceipt);
        ArrayList<UserRegisteredEventResponse> responses = new ArrayList<UserRegisteredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UserRegisteredEventResponse typedResponse = new UserRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.userAddress = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.featureHash = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UserRegisteredEventResponse getUserRegisteredEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(USERREGISTERED_EVENT, log);
        UserRegisteredEventResponse typedResponse = new UserRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.userAddress = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.featureHash = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<UserRegisteredEventResponse> userRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUserRegisteredEventFromLog(log));
    }

    public Flowable<UserRegisteredEventResponse> userRegisteredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(USERREGISTERED_EVENT));
        return userRegisteredEventFlowable(filter);
    }

    public static List<VerifierUpdatedEventResponse> getVerifierUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(VERIFIERUPDATED_EVENT, transactionReceipt);
        ArrayList<VerifierUpdatedEventResponse> responses = new ArrayList<VerifierUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            VerifierUpdatedEventResponse typedResponse = new VerifierUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldVerifier = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newVerifier = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static VerifierUpdatedEventResponse getVerifierUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(VERIFIERUPDATED_EVENT, log);
        VerifierUpdatedEventResponse typedResponse = new VerifierUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldVerifier = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newVerifier = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<VerifierUpdatedEventResponse> verifierUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getVerifierUpdatedEventFromLog(log));
    }

    public Flowable<VerifierUpdatedEventResponse> verifierUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VERIFIERUPDATED_EVENT));
        return verifierUpdatedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> EVENT_ID() {
        final Function function = new Function(FUNC_EVENT_ID,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> FACE_PROOF_SIGNALS_COUNT() {
        final Function function = new Function(FUNC_FACE_PROOF_SIGNALS_COUNT,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> ROOT_VALIDITY() {
        final Function function = new Function(FUNC_ROOT_VALIDITY,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> RULES_PROOF_SIGNALS_COUNT() {
        final Function function = new Function(FUNC_RULES_PROOF_SIGNALS_COUNT,
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

    public RemoteFunctionCall<TransactionReceipt> __FaceRegistry_init(String evidenceRegistry_,
                                                                      String faceVerifier_, String rulesVerifier_, BigInteger minThreshold_,
                                                                      BigInteger treeHeight_, List<String> oracles_) {
        final Function function = new Function(
                FUNC___FACEREGISTRY_INIT,
                Arrays.asList(new Address(160, evidenceRegistry_),
                        new Address(160, faceVerifier_),
                        new Address(160, rulesVerifier_),
                        new Uint256(minThreshold_),
                        new Uint256(treeHeight_),
                        new DynamicArray<Address>(
                                Address.class,
                                org.web3j.abi.Utils.typeMap(oracles_, Address.class))),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addOracles(List<String> oracles_) {
        final Function function = new Function(
                FUNC_ADDORACLES,
                List.of(new DynamicArray<Address>(
                        Address.class,
                        Utils.typeMap(oracles_, Address.class))),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addOwners(List<String> newOwners_) {
        final Function function = new Function(
                FUNC_ADDOWNERS,
                List.of(new DynamicArray<Address>(
                        Address.class,
                        Utils.typeMap(newOwners_, Address.class))),
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

    public RemoteFunctionCall<String> faceVerifier() {
        final Function function = new Function(FUNC_FACEVERIFIER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getFeatureHash(BigInteger userAddress_) {
        final Function function = new Function(FUNC_GETFEATUREHASH,
                List.of(new Uint256(userAddress_)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getMinThreshold() {
        final Function function = new Function(FUNC_GETMINTHRESHOLD,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Node> getNodeByKey(BigInteger userAddress_) {
        final Function function = new Function(FUNC_GETNODEBYKEY,
                List.of(new Uint256(userAddress_)),
                List.of(new TypeReference<Node>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Node.class);
    }

    public RemoteFunctionCall<List> getOracles() {
        final Function function = new Function(FUNC_GETORACLES,
                List.of(),
                List.of(new TypeReference<DynamicArray<Address>>() {
                }));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getOwners() {
        final Function function = new Function(FUNC_GETOWNERS,
                List.of(),
                List.of(new TypeReference<DynamicArray<Address>>() {
                }));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Proof> getProof(BigInteger userAddress_) {
        final Function function = new Function(FUNC_GETPROOF,
                List.of(new Uint256(userAddress_)),
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

    public RemoteFunctionCall<BigInteger> getRule(BigInteger userAddress_) {
        final Function function = new Function(FUNC_GETRULE,
                List.of(new Uint256(userAddress_)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getVerificationNonce(BigInteger address_) {
        final Function function = new Function(FUNC_GETVERIFICATIONNONCE,
                List.of(new Uint256(address_)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> isFeatureHashUsed(BigInteger featureHash_) {
        final Function function = new Function(FUNC_ISFEATUREHASHUSED,
                List.of(new Uint256(featureHash_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isOracle(String oracle_) {
        final Function function = new Function(FUNC_ISORACLE,
                List.of(new Address(160, oracle_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isOwner(String address_) {
        final Function function = new Function(FUNC_ISOWNER,
                List.of(new Address(160, address_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteFunctionCall<Boolean> isUserRegistered(BigInteger userAddress_) {
        final Function function = new Function(FUNC_ISUSERREGISTERED,
                List.of(new Uint256(userAddress_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> minThreshold() {
        final Function function = new Function(FUNC_MINTHRESHOLD,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> nonces(String owner) {
        final Function function = new Function(FUNC_NONCES,
                List.of(new Address(160, owner)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID,
                List.of(),
                List.of(new TypeReference<Bytes32>() {
                }));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerUser(BigInteger userAddress_,
                                                               BigInteger featureHash_, ProofPoints zkPoints_) {
        final Function function = new Function(
                FUNC_REGISTERUSER,
                Arrays.asList(new Uint256(userAddress_),
                        new Uint256(featureHash_),
                        zkPoints_),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeOracles(List<String> oracles_) {
        final Function function = new Function(
                FUNC_REMOVEORACLES,
                List.of(new DynamicArray<Address>(
                        Address.class,
                        Utils.typeMap(oracles_, Address.class))),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeOwners(List<String> oldOwners_) {
        final Function function = new Function(
                FUNC_REMOVEOWNERS,
                List.of(new DynamicArray<Address>(
                        Address.class,
                        Utils.typeMap(oldOwners_, Address.class))),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP,
                List.of(),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> roots(byte[] rootHash) {
        final Function function = new Function(FUNC_ROOTS,
                List.of(new Bytes32(rootHash)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> rules(BigInteger featureHash) {
        final Function function = new Function(FUNC_RULES,
                List.of(new Uint256(featureHash)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> rulesVerifier() {
        final Function function = new Function(FUNC_RULESVERIFIER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setFaceVerifier(String newVerifier_) {
        final Function function = new Function(
                FUNC_SETFACEVERIFIER,
                List.of(new Address(160, newVerifier_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setMinThreshold(BigInteger newThreshold_) {
        final Function function = new Function(
                FUNC_SETMINTHRESHOLD,
                List.of(new Uint256(newThreshold_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setRulesVerifier(String newVerifier_) {
        final Function function = new Function(
                FUNC_SETRULESVERIFIER,
                List.of(new Address(160, newVerifier_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateRule(BigInteger userAddress_,
                                                             BigInteger newState_, ProofPoints zkPoints_) {
        final Function function = new Function(
                FUNC_UPDATERULE,
                Arrays.asList(new Uint256(userAddress_),
                        new Uint256(newState_),
                        zkPoints_),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCall(String newImplementation,
                                                                   byte[] data, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALL,
                Arrays.asList(new Address(160, newImplementation),
                        new org.web3j.abi.datatypes.DynamicBytes(data)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<Boolean> usedFeatureHashes(BigInteger featureHash) {
        final Function function = new Function(FUNC_USEDFEATUREHASHES,
                List.of(new Uint256(featureHash)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> userRegistryHash(BigInteger userAddress) {
        final Function function = new Function(FUNC_USERREGISTRYHASH,
                List.of(new Uint256(userAddress)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static FaceRegistry load(String contractAddress, Web3j web3j, Credentials credentials,
                                    BigInteger gasPrice, BigInteger gasLimit) {
        return new FaceRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FaceRegistry load(String contractAddress, Web3j web3j,
                                    TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FaceRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FaceRegistry load(String contractAddress, Web3j web3j, Credentials credentials,
                                    ContractGasProvider contractGasProvider) {
        return new FaceRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FaceRegistry load(String contractAddress, Web3j web3j,
                                    TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FaceRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class ProofPoints extends StaticStruct {
        public List<BigInteger> a;

        public List<List<BigInteger>> b;

        public List<BigInteger> c;

        public ProofPoints(List<BigInteger> a, List<List<BigInteger>> b, List<BigInteger> c) {
            super(new StaticArray2<Uint256>(
                            Uint256.class,
                            org.web3j.abi.Utils.typeMap(a, Uint256.class)),
                    new StaticArray2<StaticArray2>(
                            StaticArray2.class,
                            org.web3j.abi.Utils.typeMap(b, StaticArray2.class,
                                    Uint256.class)),
                    new StaticArray2<Uint256>(
                            Uint256.class,
                            org.web3j.abi.Utils.typeMap(c, Uint256.class)));
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public ProofPoints(@Parameterized(type = Uint256.class) StaticArray2<Uint256> a,
                           @Parameterized(type = Uint256.class) StaticArray2<StaticArray2<Uint256>> b,
                           @Parameterized(type = Uint256.class) StaticArray2<Uint256> c) {
            super(a, b, c);
            this.a = a.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
            this.b = b.getValue().stream().map(v -> v.getValue()).map(v1 -> v1.stream().map(v2 -> v2.getValue()).collect(Collectors.toList())).collect(Collectors.toList());
            this.c = c.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
        }
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
            super(new Uint8(nodeType),
                    new Uint64(childLeft),
                    new Uint64(childRight),
                    new Bytes32(nodeHash),
                    new Bytes32(key),
                    new Bytes32(value));
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

    public static class Proof extends DynamicStruct {
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
            super(new Bytes32(root),
                    new DynamicArray<Bytes32>(
                            Bytes32.class,
                            org.web3j.abi.Utils.typeMap(siblings, Bytes32.class)),
                    new Bool(existence),
                    new Bytes32(key),
                    new Bytes32(value),
                    new Bool(auxExistence),
                    new Bytes32(auxKey),
                    new Bytes32(auxValue));
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

    public static class MinThresholdUpdatedEventResponse extends BaseEventResponse {
        public BigInteger oldThreshold;

        public BigInteger newThreshold;
    }

    public static class OwnersAddedEventResponse extends BaseEventResponse {
        public List<String> newOwners;
    }

    public static class OwnersRemovedEventResponse extends BaseEventResponse {
        public List<String> removedOwners;
    }

    public static class RootUpdatedEventResponse extends BaseEventResponse {
        public byte[] root;
    }

    public static class RulesUpdatedEventResponse extends BaseEventResponse {
        public BigInteger userAddress;

        public BigInteger newState;
    }

    public static class RulesVerifierUpdatedEventResponse extends BaseEventResponse {
        public String oldVerifier;

        public String newVerifier;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }

    public static class UserRegisteredEventResponse extends BaseEventResponse {
        public BigInteger userAddress;

        public BigInteger featureHash;
    }

    public static class VerifierUpdatedEventResponse extends BaseEventResponse {
        public String oldVerifier;

        public String newVerifier;
    }
}
