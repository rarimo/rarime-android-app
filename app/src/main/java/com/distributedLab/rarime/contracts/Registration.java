package com.distributedLab.rarime.contracts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;


/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.5.3.
 */
@SuppressWarnings("rawtypes")
public class Registration extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GETPASSPORTINFO = "getPassportInfo";

    public static final String FUNC_GETPROOF = "getProof";

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Tuple2<PassportInfo, IdentityInfo>> getPassportInfo(byte[] passportKey_) {
        final Function function = new Function(FUNC_GETPASSPORTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<PassportInfo>() {}, new TypeReference<IdentityInfo>() {}));
        return new RemoteFunctionCall<Tuple2<PassportInfo, IdentityInfo>>(function,
                new Callable<Tuple2<PassportInfo, IdentityInfo>>() {
                    @Override
                    public Tuple2<PassportInfo, IdentityInfo> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<PassportInfo, IdentityInfo>(
                                (PassportInfo) results.get(0), 
                                (IdentityInfo) results.get(1));
                    }
                });
    }

    public RemoteFunctionCall<Proof> getProof(byte[] key_) {
        final Function function = new Function(FUNC_GETPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(key_)), 
                Arrays.<TypeReference<  ?>>asList(new TypeReference<Proof>() {}));
        return executeRemoteCallSingleValueReturn(function, Proof.class);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class PassportInfo extends StaticStruct {
        public byte[] activeIdentity;

        public BigInteger identityReissueCounter;

        public PassportInfo(byte[] activeIdentity, BigInteger identityReissueCounter) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(activeIdentity), 
                    new org.web3j.abi.datatypes.generated.Uint64(identityReissueCounter));
            this.activeIdentity = activeIdentity;
            this.identityReissueCounter = identityReissueCounter;
        }

        public PassportInfo(Bytes32 activeIdentity, Uint64 identityReissueCounter) {
            super(activeIdentity, identityReissueCounter);
            this.activeIdentity = activeIdentity.getValue();
            this.identityReissueCounter = identityReissueCounter.getValue();
        }
    }

    public static class IdentityInfo extends StaticStruct {
        public byte[] activePassport;

        public BigInteger issueTimestamp;

        public IdentityInfo(byte[] activePassport, BigInteger issueTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(activePassport), 
                    new org.web3j.abi.datatypes.generated.Uint64(issueTimestamp));
            this.activePassport = activePassport;
            this.issueTimestamp = issueTimestamp;
        }

        public IdentityInfo(Bytes32 activePassport, Uint64 issueTimestamp) {
            super(activePassport, issueTimestamp);
            this.activePassport = activePassport.getValue();
            this.issueTimestamp = issueTimestamp.getValue();
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

        public Proof(byte[] root, List<byte[]> siblings, Boolean existence, byte[] key, byte[] value, Boolean auxExistence, byte[] auxKey, byte[] auxValue) {
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

        public Proof(Bytes32 root, @Parameterized(type = Bytes32.class) DynamicArray<Bytes32> siblings, Bool existence, Bytes32 key, Bytes32 value, Bool auxExistence, Bytes32 auxKey, Bytes32 auxValue) {
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
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface Parameterized {
    Class<?> type();
}