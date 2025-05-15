package com.rarilabs.rarime.contracts.rarimo;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.StaticArray2;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
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
public class GuessCelebrity extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_PROOF_SIGNALS_COUNT = "PROOF_SIGNALS_COUNT";

    public static final String FUNC_UPGRADE_INTERFACE_VERSION = "UPGRADE_INTERFACE_VERSION";

    public static final String FUNC___GUESSCELEBRITY_INIT = "__GuessCelebrity_init";

    public static final String FUNC_ADDOWNERS = "addOwners";

    public static final String FUNC_CELEBFEATUREHASH = "celebFeatureHash";

    public static final String FUNC_CLAIMREWARD = "claimReward";

    public static final String FUNC_CLAIMEDFEATUREHASHES = "claimedFeatureHashes";

    public static final String FUNC_FACEVERIFIER = "faceVerifier";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final String FUNC_GETOWNERS = "getOwners";

    public static final String FUNC_GETVERIFICATIONNONCE = "getVerificationNonce";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_MINTHRESHOLD = "minThreshold";

    public static final String FUNC_NONCES = "nonces";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REMOVEOWNERS = "removeOwners";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REWARD = "reward";

    public static final String FUNC_SETCELEBFEATUREHASH = "setCelebFeatureHash";

    public static final String FUNC_SETFACEVERIFIER = "setFaceVerifier";

    public static final String FUNC_SETMINTHRESHOLD = "setMinThreshold";

    public static final String FUNC_SETREWARD = "setReward";

    public static final String FUNC_UNPAUSE = "unpause";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final String FUNC_WITHDRAWALLFUNDS = "withdrawAllFunds";

    public static final String FUNC_WITHDRAWFUNDS = "withdrawFunds";

    public static final Event CELEBFEATUREUPDATED_EVENT = new Event("CelebFeatureUpdated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event FUNDSRECEIVED_EVENT = new Event("FundsReceived",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<Uint256>() {
            }));

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

    public static final Event PAUSED_EVENT = new Event("Paused",
            List.of(new TypeReference<Address>() {
            }));

    public static final Event REWARDCLAIMED_EVENT = new Event("RewardClaimed",
            Arrays.asList(new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event REWARDUPDATED_EVENT = new Event("RewardUpdated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event UNPAUSED_EVENT = new Event("Unpaused",
            List.of(new TypeReference<Address>() {
            }));

    public static final Event UPGRADED_EVENT = new Event("Upgraded",
            List.of(new TypeReference<Address>(true) {
            }));

    public static final Event VERIFIERUPDATED_EVENT = new Event("VerifierUpdated",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));

    @Deprecated
    protected GuessCelebrity(String contractAddress, Web3j web3j, Credentials credentials,
                             BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected GuessCelebrity(String contractAddress, Web3j web3j, Credentials credentials,
                             ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected GuessCelebrity(String contractAddress, Web3j web3j,
                             TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected GuessCelebrity(String contractAddress, Web3j web3j,
                             TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<CelebFeatureUpdatedEventResponse> getCelebFeatureUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CELEBFEATUREUPDATED_EVENT, transactionReceipt);
        ArrayList<CelebFeatureUpdatedEventResponse> responses = new ArrayList<CelebFeatureUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            CelebFeatureUpdatedEventResponse typedResponse = new CelebFeatureUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldFeature = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newFeature = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CelebFeatureUpdatedEventResponse getCelebFeatureUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CELEBFEATUREUPDATED_EVENT, log);
        CelebFeatureUpdatedEventResponse typedResponse = new CelebFeatureUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldFeature = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newFeature = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<CelebFeatureUpdatedEventResponse> celebFeatureUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCelebFeatureUpdatedEventFromLog(log));
    }

    public Flowable<CelebFeatureUpdatedEventResponse> celebFeatureUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CELEBFEATUREUPDATED_EVENT));
        return celebFeatureUpdatedEventFlowable(filter);
    }

    public static List<FundsReceivedEventResponse> getFundsReceivedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(FUNDSRECEIVED_EVENT, transactionReceipt);
        ArrayList<FundsReceivedEventResponse> responses = new ArrayList<FundsReceivedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            FundsReceivedEventResponse typedResponse = new FundsReceivedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static FundsReceivedEventResponse getFundsReceivedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(FUNDSRECEIVED_EVENT, log);
        FundsReceivedEventResponse typedResponse = new FundsReceivedEventResponse();
        typedResponse.log = log;
        typedResponse.sender = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<FundsReceivedEventResponse> fundsReceivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getFundsReceivedEventFromLog(log));
    }

    public Flowable<FundsReceivedEventResponse> fundsReceivedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FUNDSRECEIVED_EVENT));
        return fundsReceivedEventFlowable(filter);
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

    public static List<PausedEventResponse> getPausedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PAUSED_EVENT, transactionReceipt);
        ArrayList<PausedEventResponse> responses = new ArrayList<PausedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            PausedEventResponse typedResponse = new PausedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PausedEventResponse getPausedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PAUSED_EVENT, log);
        PausedEventResponse typedResponse = new PausedEventResponse();
        typedResponse.log = log;
        typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPausedEventFromLog(log));
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(DefaultBlockParameter startBlock,
                                                             DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PAUSED_EVENT));
        return pausedEventFlowable(filter);
    }

    public static List<RewardClaimedEventResponse> getRewardClaimedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REWARDCLAIMED_EVENT, transactionReceipt);
        ArrayList<RewardClaimedEventResponse> responses = new ArrayList<RewardClaimedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RewardClaimedEventResponse typedResponse = new RewardClaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.winner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RewardClaimedEventResponse getRewardClaimedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REWARDCLAIMED_EVENT, log);
        RewardClaimedEventResponse typedResponse = new RewardClaimedEventResponse();
        typedResponse.log = log;
        typedResponse.winner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<RewardClaimedEventResponse> rewardClaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRewardClaimedEventFromLog(log));
    }

    public Flowable<RewardClaimedEventResponse> rewardClaimedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REWARDCLAIMED_EVENT));
        return rewardClaimedEventFlowable(filter);
    }

    public static List<RewardUpdatedEventResponse> getRewardUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REWARDUPDATED_EVENT, transactionReceipt);
        ArrayList<RewardUpdatedEventResponse> responses = new ArrayList<RewardUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RewardUpdatedEventResponse typedResponse = new RewardUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldReward = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newReward = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RewardUpdatedEventResponse getRewardUpdatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REWARDUPDATED_EVENT, log);
        RewardUpdatedEventResponse typedResponse = new RewardUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.oldReward = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newReward = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RewardUpdatedEventResponse> rewardUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRewardUpdatedEventFromLog(log));
    }

    public Flowable<RewardUpdatedEventResponse> rewardUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REWARDUPDATED_EVENT));
        return rewardUpdatedEventFlowable(filter);
    }

    public static List<UnpausedEventResponse> getUnpausedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UNPAUSED_EVENT, transactionReceipt);
        ArrayList<UnpausedEventResponse> responses = new ArrayList<UnpausedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UnpausedEventResponse typedResponse = new UnpausedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UnpausedEventResponse getUnpausedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UNPAUSED_EVENT, log);
        UnpausedEventResponse typedResponse = new UnpausedEventResponse();
        typedResponse.log = log;
        typedResponse.account = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUnpausedEventFromLog(log));
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(DefaultBlockParameter startBlock,
                                                                 DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNPAUSED_EVENT));
        return unpausedEventFlowable(filter);
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

    public RemoteFunctionCall<BigInteger> PROOF_SIGNALS_COUNT() {
        final Function function = new Function(FUNC_PROOF_SIGNALS_COUNT,
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

    public RemoteFunctionCall<TransactionReceipt> __GuessCelebrity_init(String faceVerifier_,
                                                                        BigInteger minThreshold_, BigInteger celebFeatureHash_, BigInteger initialReward_) {
        final Function function = new Function(
                FUNC___GUESSCELEBRITY_INIT,
                Arrays.asList(new Address(160, faceVerifier_),
                        new Uint256(minThreshold_),
                        new Uint256(celebFeatureHash_),
                        new Uint256(initialReward_)),
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

    public RemoteFunctionCall<BigInteger> celebFeatureHash() {
        final Function function = new Function(FUNC_CELEBFEATUREHASH,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> claimReward(String recipient_,
                                                              ProofPoints zkPoints_) {
        final Function function = new Function(
                FUNC_CLAIMREWARD,
                Arrays.asList(new Address(160, recipient_),
                        zkPoints_),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> claimedFeatureHashes(BigInteger param0) {
        final Function function = new Function(FUNC_CLAIMEDFEATUREHASHES,
                List.of(new Uint256(param0)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> faceVerifier() {
        final Function function = new Function(FUNC_FACEVERIFIER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getBalance() {
        final Function function = new Function(FUNC_GETBALANCE,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<BigInteger> getVerificationNonce(String address_) {
        final Function function = new Function(FUNC_GETVERIFICATIONNONCE,
                List.of(new Address(160, address_)),
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

    public RemoteFunctionCall<Boolean> isOwner(String address_) {
        final Function function = new Function(FUNC_ISOWNER,
                List.of(new Address(160, address_)),
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

    public RemoteFunctionCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE,
                List.of(),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> paused() {
        final Function function = new Function(FUNC_PAUSED,
                List.of(),
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

    public RemoteFunctionCall<BigInteger> reward() {
        final Function function = new Function(FUNC_REWARD,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setCelebFeatureHash(BigInteger newFeatureHash_) {
        final Function function = new Function(
                FUNC_SETCELEBFEATUREHASH,
                List.of(new Uint256(newFeatureHash_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> setReward(BigInteger newReward_) {
        final Function function = new Function(
                FUNC_SETREWARD,
                List.of(new Uint256(newReward_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE,
                List.of(),
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

    public RemoteFunctionCall<TransactionReceipt> withdrawAllFunds() {
        final Function function = new Function(
                FUNC_WITHDRAWALLFUNDS,
                List.of(),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawFunds(BigInteger amount_) {
        final Function function = new Function(
                FUNC_WITHDRAWFUNDS,
                List.of(new Uint256(amount_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static GuessCelebrity load(String contractAddress, Web3j web3j, Credentials credentials,
                                      BigInteger gasPrice, BigInteger gasLimit) {
        return new GuessCelebrity(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static GuessCelebrity load(String contractAddress, Web3j web3j,
                                      TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new GuessCelebrity(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static GuessCelebrity load(String contractAddress, Web3j web3j, Credentials credentials,
                                      ContractGasProvider contractGasProvider) {
        return new GuessCelebrity(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static GuessCelebrity load(String contractAddress, Web3j web3j,
                                      TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new GuessCelebrity(contractAddress, web3j, transactionManager, contractGasProvider);
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

    public static class CelebFeatureUpdatedEventResponse extends BaseEventResponse {
        public BigInteger oldFeature;

        public BigInteger newFeature;
    }

    public static class FundsReceivedEventResponse extends BaseEventResponse {
        public String sender;

        public BigInteger amount;
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

    public static class PausedEventResponse extends BaseEventResponse {
        public String account;
    }

    public static class RewardClaimedEventResponse extends BaseEventResponse {
        public String winner;

        public BigInteger amount;
    }

    public static class RewardUpdatedEventResponse extends BaseEventResponse {
        public BigInteger oldReward;

        public BigInteger newReward;
    }

    public static class UnpausedEventResponse extends BaseEventResponse {
        public String account;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }

    public static class VerifierUpdatedEventResponse extends BaseEventResponse {
        public String oldVerifier;

        public String newVerifier;
    }
}
