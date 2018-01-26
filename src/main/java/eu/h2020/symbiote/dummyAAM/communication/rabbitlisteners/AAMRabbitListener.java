package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.security.commons.Certificate;
import eu.h2020.symbiote.security.commons.enums.ManagementStatus;
import eu.h2020.symbiote.security.commons.enums.OperationType;
import eu.h2020.symbiote.security.commons.enums.UserRole;
import eu.h2020.symbiote.security.communication.payloads.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import java.security.*;
import java.util.*;

@Component
public class AAMRabbitListener {
    private static Log log = LogFactory.getLog(AAMRabbitListener.class);

    public AAMRabbitListener() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamPlatformManagementRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.manage.platform.request}")
    )
    public PlatformManagementResponse platformManagementRequest(PlatformManagementRequest platformManagementRequest) {

        log.info("platformManagementRequest: "+ ReflectionToStringBuilder.toString(platformManagementRequest));

        if (platformManagementRequest.getPlatformInstanceId().isEmpty())
            platformManagementRequest.setPlatformInstanceId("EmptyId");


        if (platformManagementRequest.getPlatformOwnerCredentials() == null ||
                (platformManagementRequest.getPlatformOwnerCredentials().getUsername() == null ||
                        platformManagementRequest.getPlatformOwnerCredentials().getPassword() == null))
            return new PlatformManagementResponse(null, ManagementStatus.ERROR);
        else if (platformManagementRequest.getOperationType() == OperationType.CREATE) {
            log.info("OperationType.CREATE");
            if (!platformManagementRequest.getPlatformInstanceId().equals("exists") &&
                    !platformManagementRequest.getPlatformInstanceId().equals("error")) {
                return new PlatformManagementResponse(platformManagementRequest.getPlatformInstanceId(), ManagementStatus.OK);
            } else if (platformManagementRequest.getPlatformInstanceId().equals("exists")) {
                return new PlatformManagementResponse(null, ManagementStatus.PLATFORM_EXISTS);
            } else if (platformManagementRequest.getPlatformInstanceId().equals("error")) {
                return new PlatformManagementResponse(null, ManagementStatus.ERROR);
            }
        } else if (platformManagementRequest.getOperationType() == OperationType.UPDATE) {
            log.info("OperationType.UPDATE");
            if (!platformManagementRequest.getPlatformInstanceId().equals("reg401") &&
                    !platformManagementRequest.getPlatformInstanceId().equals("validPO2Platform1")) {
                log.info("UPDATE was accepted");
                return new PlatformManagementResponse(platformManagementRequest.getPlatformInstanceId(), ManagementStatus.OK);

            } else {
                log.info("UPDATE was rejected");
                return new PlatformManagementResponse(null, ManagementStatus.ERROR);
            }
        } else if (platformManagementRequest.getOperationType() == OperationType.DELETE) {
            log.info("OperationType.DELETE");
            if (!platformManagementRequest.getPlatformInstanceId().equals("reg401") &&
                    !platformManagementRequest.getPlatformInstanceId().equals("validPO2Platform1")) {
                log.info("DELETE was accepted");
                return new PlatformManagementResponse(platformManagementRequest.getPlatformInstanceId(), ManagementStatus.OK);

            } else {
                log.info("DELETE was rejected");
                return new PlatformManagementResponse(null, ManagementStatus.ERROR);
            }
        }


        return new PlatformManagementResponse(null, null);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamOwnedPlatformDetailsRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.ownedplatformdetails.request}")
    )
    public Set<OwnedPlatformDetails> ownedPlatformDetailsRequest(UserManagementRequest request) {

        log.info("ownedPlatformDetailsRequest, request: " + ReflectionToStringBuilder.toString(request));

        if(request != null) {
            String username = request.getUserCredentials().getUsername();
            Set<OwnedPlatformDetails> set = new HashSet<>();

            if (username.equals("valid")) {
                return set;
            }

            if (username.contains("validPO")) {

                OwnedPlatformDetails details = new OwnedPlatformDetails(username + "Platform1",
                        "http://" + username + "Platform1.com",
                        username + "PlatformFriendlyName", new Certificate(), new HashMap<>());

                set.add(details);
            }

            if (username.equals("validPO2")) {
                set.add(new OwnedPlatformDetails(username + "Platform2",
                        "http://" + username + "Platform2.com:8102",
                        username + "Platform2FriendlyName", new Certificate(), new HashMap<>()));

                set.add(new OwnedPlatformDetails(username + "Platform3",
                        "http://" + username + "Platform3.com:8103/",
                        username + "Platform3FriendlyName", new Certificate(), new HashMap<>()));

                set.add(new OwnedPlatformDetails(username + "Platform4",
                        "http://" + username + "Platform4.com:8104/test",
                        username + "Platform4FriendlyName", new Certificate(), new HashMap<>()));

                set.add(new OwnedPlatformDetails(username + "Platform-5",
                        "http://" + username + "Platform-5.com",
                        username + "Platform-5FriendlyName", new Certificate(), new HashMap<>()));
            }
            return set;

        }

        return null;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamUserManagementRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.manage.user.request}")
    )
    public ManagementStatus userManagementRequest(UserManagementRequest userManagementRequest) {

        log.info("userManagementRequest: "+ ReflectionToStringBuilder.toString(userManagementRequest));
        log.info("UserDetails: " + ReflectionToStringBuilder.toString(userManagementRequest.getUserCredentials()));

        if (userManagementRequest.getUserCredentials().getUsername().equals("valid"))
            return ManagementStatus.OK;
        else if (userManagementRequest.getUserCredentials().getUsername().equals("exists"))
            return ManagementStatus.USERNAME_EXISTS;
        else
            return ManagementStatus.ERROR;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamGetUserDetailsRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.get.user.details}")
    )
    public UserDetailsResponse getUserDetails(UserManagementRequest userManagementRequest) {

        log.info("getUserDetails: "+ ReflectionToStringBuilder.toString(userManagementRequest));

        String username = userManagementRequest.getUserCredentials().getUsername();
        if (username.equals("valid"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails());
        if (username.equals("validPO"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPO", ""),
                    "", username + "email", UserRole.PLATFORM_OWNER, new HashMap<>(), new HashMap<>()));
        if (username.equals("validPO2"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPO2", ""),
                    "", username + "email", UserRole.PLATFORM_OWNER, new HashMap<>(), new HashMap<>()));
        else if (username.equals("wrongUsername"))
            return new UserDetailsResponse(HttpStatus.BAD_REQUEST, new UserDetails());
        else if (username.equals("wrongUserPassword"))
            return new UserDetailsResponse(HttpStatus.UNAUTHORIZED, new UserDetails());
        else if (username.equals("wrongAdminPassword"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails());

        return null;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamFederationRuleManagementRequestConsumerService", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.manage.federation.rule}")
    )
    public Map<String, FederationRule> federationRuleManagementRequestConsumerService(FederationRuleManagementRequest request) {

        log.info("federationRuleManagementRequestConsumerService: "+ ReflectionToStringBuilder.toString(request));
        Map<String, FederationRule> response = new HashMap<>();

        if (request.getOperationType() == FederationRuleManagementRequest.OperationType.CREATE) {
            if (request.getFederationRuleId().equals("error"))
                return null;
            else {
                FederationRule federationRule = new FederationRule(request.getFederationRuleId(),
                        request.getPlatformIds());
                response.put(request.getFederationRuleId(), federationRule);
                return response;
            }
        } else if (request.getOperationType() == FederationRuleManagementRequest.OperationType.READ) {
            if (request.getFederationRuleId().isEmpty()) {
                String federationRuleId1 = "exampleFedId1";
                String federationRuleId2 = "exampleFedId2";
                Set<String> platforms = new HashSet<>();
                platforms.add("FedPlatform1");
                platforms.add("FedPlatform2");
                FederationRule federationRule1 = new FederationRule(federationRuleId1,
                        platforms);
                FederationRule federationRule2 = new FederationRule(federationRuleId2,
                        platforms);
                response.put(federationRuleId1, federationRule1);
                response.put(federationRuleId2, federationRule2);
                return response;
            } else
                return null;
        } else if (request.getOperationType() == FederationRuleManagementRequest.OperationType.DELETE) {
            if (request.getFederationRuleId().equals("exampleFedId1")) {
                FederationRule federationRule = new FederationRule("exampleFedId1",
                        new HashSet<>());
                response.put("exampleFedId1", federationRule);
                return response;
            } else
                return null;
        }

        return null;
    }
}
