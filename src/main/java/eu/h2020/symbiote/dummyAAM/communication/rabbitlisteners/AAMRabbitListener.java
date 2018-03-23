package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.security.commons.Certificate;
import eu.h2020.symbiote.security.commons.enums.ManagementStatus;
import eu.h2020.symbiote.security.commons.enums.OperationType;
import eu.h2020.symbiote.security.commons.enums.UserRole;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
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

import java.security.Security;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
            value = @Queue(value = "aamSSPManagementRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.manage.smartspace.request}")
    )
    public SmartSpaceManagementResponse sspManagementRequest(SmartSpaceManagementRequest request) {

        log.info("request: "+ ReflectionToStringBuilder.toString(request));

        if (request.getInstanceId().isEmpty()) {
            try {
                log.info(ReflectionToStringBuilder.toString(request));
                request = new SmartSpaceManagementRequest(
                        request.getAamOwnerCredentials(),
                        request.getServiceOwnerCredentials(),
                        request.getExternalAddress(),
                        request.getSiteLocalAddress(),
                        request.getInstanceFriendlyName(),
                        request.getOperationType(),
                        "EmptyId",
                        request.isExposingSiteLocalAddress()
                );
                log.info(ReflectionToStringBuilder.toString(request));

            } catch (InvalidArgumentsException e) {
                log.info("Invalid Arguments", e);
            }
        }


        if (request.getServiceOwnerCredentials() == null ||
                (request.getServiceOwnerCredentials().getUsername() == null ||
                        request.getServiceOwnerCredentials().getPassword() == null))
            return new SmartSpaceManagementResponse(null, ManagementStatus.ERROR);
        else if (request.getOperationType() == OperationType.CREATE) {
            log.info("OperationType.CREATE");

            if (!request.getInstanceId().equals("exists") &&
                    !request.getInstanceId().equals("error")) {
                return new SmartSpaceManagementResponse(request.getInstanceId(), ManagementStatus.OK);
            } else if (request.getInstanceId().equals("exists")) {
                return new SmartSpaceManagementResponse(null, ManagementStatus.PLATFORM_EXISTS);
            } else if (request.getInstanceId().equals("error")) {
                return new SmartSpaceManagementResponse(null, ManagementStatus.ERROR);
            }
        } else if (request.getOperationType() == OperationType.UPDATE) {
            log.info("OperationType.UPDATE");
            if (!request.getInstanceId().equals("reg401") &&
                    !request.getInstanceId().equals("validPO2Platform1")) {
                log.info("UPDATE was accepted");
                return new SmartSpaceManagementResponse(request.getInstanceId(), ManagementStatus.OK);

            } else {
                log.info("UPDATE was rejected");
                return new SmartSpaceManagementResponse(null, ManagementStatus.ERROR);
            }
        } else if (request.getOperationType() == OperationType.DELETE) {
            log.info("OperationType.DELETE");
            if (!request.getInstanceId().equals("reg401") &&
                    !request.getInstanceId().equals("validPO2SSP1")) {
                log.info("DELETE was accepted");
                return new SmartSpaceManagementResponse(request.getInstanceId(), ManagementStatus.OK);

            } else {
                log.info("DELETE was rejected");
                return new SmartSpaceManagementResponse(null, ManagementStatus.ERROR);
            }
        }

        return new SmartSpaceManagementResponse(null, null);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamOwnedServiceDetailsRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.queue.ownedservices.request}")
    )
    public Set<OwnedService> ownedServiceDetailsRequest(UserManagementRequest request) {

        log.info("ownedServiceDetailsRequest, request: " + ReflectionToStringBuilder.toString(request));

        if(request != null) {
            String username = request.getUserCredentials().getUsername();
            Set<OwnedService> set = new HashSet<>();

            if (username.equals("valid")) {
                return set;
            }

            if (username.contains("validPO")) {

                OwnedService details = new OwnedService(username + "Platform1",
                        username + "PlatformFriendlyName",OwnedService.ServiceType.PLATFORM,
                        "http://" + username + "Platform1.com",
                        null, false, null,
                         new Certificate(), new HashMap<>());

                set.add(details);
            }

            if (username.equals("validPO2")) {
                set.add(new OwnedService(username + "Platform2",
                        username + "Platform2FriendlyName", OwnedService.ServiceType.PLATFORM,
                        "http://" + username + "Platform2.com:8102",
                        null, false, null,
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService(username + "Platform3",
                        username + "Platform3FriendlyName", OwnedService.ServiceType.PLATFORM,
                        "http://" + username + "Platform3.com:8103/",
                        null, false, null,
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService(username + "Platform4",
                        username + "Platform4FriendlyName", OwnedService.ServiceType.PLATFORM,
                        "http://" + username + "Platform4.com:8104/test",
                        null, false, null,
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService(username + "Platform-5",
                        username + "Platform-5FriendlyName", OwnedService.ServiceType.PLATFORM,
                        "http://" + username + "Platform-5.com",
                        null, false, null,
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService(username + "SSP1",
                        username + "SSP1FriendlyName", OwnedService.ServiceType.SMART_SPACE,
                        null, "http://" + username + "externalSSP1.com",
                        false, "http://" + username + "localSSP1.com",
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService(username + "SSP2",
                        username + "SSP2FriendlyName", OwnedService.ServiceType.SMART_SPACE,
                        null, "http://" + username + "externalSSP2.com",
                        true, "http://" + username + "localSSP2.com",
                        new Certificate(), new HashMap<>()));
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
        log.info("UserDetails: " + ReflectionToStringBuilder.toString(userManagementRequest.getUserDetails()));

        if (userManagementRequest.getOperationType() == OperationType.CREATE) {
            if (userManagementRequest.getUserCredentials().getUsername().equals("valid"))
                return ManagementStatus.OK;
            else if (userManagementRequest.getUserCredentials().getUsername().equals("exists"))
                return ManagementStatus.USERNAME_EXISTS;
            else
                return ManagementStatus.ERROR;
        } else if (userManagementRequest.getOperationType() == OperationType.UPDATE) {
            if (userManagementRequest.getUserDetails().getRecoveryMail().equals("c@c.com"))
                return null;
            if (userManagementRequest.getUserDetails().getCredentials().getPassword().equals("cccc"))
                return null;
            else {
                return ManagementStatus.OK;
            }
        }

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
                    username + "email", UserRole.SERVICE_OWNER, new HashMap<>(), new HashMap<>()));
        if (username.equals("validPO2"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPO2", ""),
                    username + "email", UserRole.SERVICE_OWNER, new HashMap<>(), new HashMap<>()));
        else if (username.equals("wrongUsername"))
            return new UserDetailsResponse(HttpStatus.BAD_REQUEST, new UserDetails());
        else if (username.equals("wrongUserPassword"))
            return new UserDetailsResponse(HttpStatus.UNAUTHORIZED, new UserDetails());
        else if (username.equals("wrongAdminPassword"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails());

        return null;
    }
}
