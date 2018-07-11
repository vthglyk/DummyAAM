package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.security.commons.Certificate;
import eu.h2020.symbiote.security.commons.enums.AccountStatus;
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
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AAMRabbitListener {
    private static Log log = LogFactory.getLog(AAMRabbitListener.class);
    private static final String exampleCertificate = "-----BEGIN CERTIFICATE-----" +
            "\\nMIIBxzCCAW6gAwIBAgIBATAKBggqhkjOPQQDAjCBrDErMCkGCSqGSIb3DQEJARYc" +
            "\\nbWlrb2xhai5kb2Jza2lAbWFuLnBvem5hbi5wbDEVMBMGA1UEBxMMV2llbGtvcG9s" +
            "\\nc2thMQ8wDQYDVQQIEwZQb3puYW4xCzAJBgNVBAYTAlBMMR0wGwYDVQQLExRTeW1i" +
            "\\naW90ZSBEZXZlbG9wbWVudDENMAsGA1UEChMEUFNOQzEaMBgGA1UEAwwRU3ltYklv" +
            "\\nVGVfQ29yZV9BQU0wHhcNMTcxMjExMTMwMjIxWhcNMTgxMjExMTMwMjIxWjAZMRcw\\" +
            "nFQYDVQQDEw5BSVQtb3BlblV3ZWRhdDBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IA" +
            "\\nBHOlakZd3SnpfAgCVCUDXXKINnaS4gNYXJMYT3rO6GS2pRSwkSzUaIINJidMXqHi" +
            "\\npUsg9tUe9hfhTWggtwUuA4ejEzARMA8GA1UdEwQIMAYBAf8CAQAwCgYIKoZIzj0E" +
            "\\nAwIDRwAwRAIgeXzflPYqF8d65/BGGK5kdA0kOZFgtZS0Tq/dHAxQpDYCIBJWsarZ" +
            "\\nRWHgqSlzLLj6wyGFNYMTWXFIHXo2cnFRAfBv\\n-----END CERTIFICATE-----\\n";

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
    public Object sspManagementRequest(SmartSpaceManagementRequest request) {

        log.info("request: "+ ReflectionToStringBuilder.toString(request));

        if (request.getInstanceId().equals("error"))
            return new ErrorResponseContainer("There is an error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        if (!request.getInstanceId().isEmpty()) {
            Pattern p = Pattern.compile("^SSP_([\\w-])+$");   // the pattern to search for
            Matcher m = p.matcher(request.getInstanceId());

            if (!m.find())
                return new ErrorResponseContainer("Smart space identifier must start with 'SSP_' prefix.",
                        HttpStatus.BAD_REQUEST.value());
        }

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
                    !request.getInstanceId().equals("validPO2SSP1")) {
                log.info("UPDATE was accepted");
                return new SmartSpaceManagementResponse(request.getInstanceId(), ManagementStatus.OK);

            } else {
                log.info("UPDATE was rejected");
                return new SmartSpaceManagementResponse(null, ManagementStatus.ERROR);
            }
        } else if (request.getOperationType() == OperationType.DELETE) {
            log.info("OperationType.DELETE");
            if (!request.getInstanceId().equals("reg401") &&
                    !request.getInstanceId().equals("SSP_validPO2SSP1")) {
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
            key = "${rabbit.routingKey.ownedservices.request}")
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

            if (username.equals("icom")) {
                set.add(new OwnedService("icom-platform",
                        "icom-platform", OwnedService.ServiceType.PLATFORM,
                        "https://intracom.symbiote-h2020.eu/",
                        null, false, null,
                        new Certificate(), new HashMap<>()));
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

                set.add(new OwnedService("SSP_" + username + "SSP1",
                        username + "SSP1FriendlyName", OwnedService.ServiceType.SMART_SPACE,
                        null, "https://" + username.toLowerCase() + "externalssp1.com",
                        true, "https://" + username.toLowerCase() + "localssp1.com",
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService("SSP_" + username + "SSP2",
                        username + "SSP2FriendlyName", OwnedService.ServiceType.SMART_SPACE,
                        null, null,
                        false, null,
                        new Certificate(), new HashMap<>()));

                set.add(new OwnedService("SSP_" + username + "SSP3",
                        username + "SSP3FriendlyName", OwnedService.ServiceType.SMART_SPACE,
                        null, "https://" + username.toLowerCase() + "externalssp3.com",
                        true, "https://" + username.toLowerCase() + "localssp3.com",
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
        OperationType type = userManagementRequest.getOperationType();
        String username = userManagementRequest.getUserCredentials().getUsername();
        String password = userManagementRequest.getUserCredentials().getPassword();
        String email = userManagementRequest.getUserDetails().getRecoveryMail();
        Map<String, Certificate> clients = userManagementRequest.getUserDetails().getClients();

        if (type == OperationType.CREATE) {
            if (username.equals("valid"))
                return ManagementStatus.OK;
            if (username.equals("exists"))
                return ManagementStatus.USERNAME_EXISTS;
            else
                return ManagementStatus.ERROR;
        } else if (type == OperationType.UPDATE) {
            log.info("Got an UPDATE operation");

            if (email.equals("c@c.com")) {
                log.info("Wrong email");
                return null;
            } else if (password.equals("cccc")) {
                log.info("Wrong password");
                return null;
            } else {
                log.info("Successful update");
                return ManagementStatus.OK;
            }
        } else if (type == OperationType.DELETE) {
            log.info("Got a DELETE operation");

            if (username.equals("valid"))
                return ManagementStatus.OK;
            else
                return ManagementStatus.ERROR;
        } else if (type == OperationType.FORCE_UPDATE)
            return ManagementStatus.OK;

        return ManagementStatus.ERROR;

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "aamRevocationRequest", durable = "${rabbit.exchange.aam.durable}",
                    autoDelete = "${rabbit.exchange.aam.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.aam.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.aam.durable}", autoDelete  = "${rabbit.exchange.aam.autodelete}",
                    internal = "${rabbit.exchange.aam.internal}", type = "${rabbit.exchange.aam.type}"),
            key = "${rabbit.routingKey.manage.revocation.request}")
    )
    public RevocationResponse revocation(RevocationRequest request) {

        log.info("revocation: "+ ReflectionToStringBuilder.toString(request));
        RevocationRequest.CredentialType type = request.getCredentialType();
        String username = request.getCredentials().getUsername();
        String password = request.getCredentials().getPassword();
        String certificateCommonName = request.getCertificateCommonName();
        log.info("username = " + username);
        log.info("password = " + password);
        log.info("certificateCommonName = " + certificateCommonName);

        if (type == RevocationRequest.CredentialType.USER &&
                username.equals("icom") &&
                password.equals("icom")) {
            switch (certificateCommonName) {
                case "icom@client4_icom":
                    return new RevocationResponse(true, HttpStatus.OK);
                case "icom@client3_icom":
                    return new RevocationResponse(false, HttpStatus.OK);
                case "icom@client2_icom":
                    return new RevocationResponse(true, HttpStatus.BAD_REQUEST);
                case "icom@client1_icom":
                    return new RevocationResponse(false, HttpStatus.BAD_REQUEST);
            }
        }
        return new RevocationResponse(false, HttpStatus.BAD_REQUEST);
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

        Map<String, Certificate> clients = new HashMap<>();

        try {
            clients.put("client1_" + username, new Certificate("certificate1_" + username + exampleCertificate));
            clients.put("client2_" + username, new Certificate("certificate2_" + username + exampleCertificate));
            clients.put("client3_" + username, new Certificate("certificate3_" + username + exampleCertificate));
            clients.put("client4_" + username, new Certificate("certificate4_" + username + exampleCertificate));
        } catch (CertificateException e) {
            log.warn("Exception during Certification creation", e);
        }

        if (username.equals("valid") || username.equals("valid2"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validUSER", ""),
                    username + "email", UserRole.USER, AccountStatus.ACTIVE, new HashMap<>(), clients,
                    true, true));

        if (username.equals("icom"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("icom", "icom"),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.ACTIVE, new HashMap<>(),
                    clients, true, false));

        if (username.equals("validUser"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validUSER", ""),
                    username + "email", UserRole.USER, AccountStatus.ACTIVE, new HashMap<>(),
                    clients, true, true));

        if (username.equals("validPO"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPO", ""),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.ACTIVE, new HashMap<>(),
                    clients, true, true));

        if (username.equals("validPO2"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPO2", ""),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.ACTIVE, new HashMap<>(),
                    clients, true, true));

        if (username.equals("wrongUsername"))
            return new UserDetailsResponse(HttpStatus.BAD_REQUEST, new UserDetails());

        if (username.equals("wrongUserPassword"))
            return new UserDetailsResponse(HttpStatus.UNAUTHORIZED, new UserDetails());

        if (username.equals("wrongAdminPassword"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, null);

        if (username.equals("inactiveUser"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails(new Credentials("inactiveUser", ""),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.NEW, new HashMap<>(),
                    clients, true, true));

        if (username.equals("termsNotAccepted"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails(new Credentials("termsNotAccepted", ""),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.CONSENT_BLOCKED, new HashMap<>(),
                    clients, false, true));

        if (username.equals("blocked"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails(new Credentials("blocked", ""),
                    username + "email", UserRole.SERVICE_OWNER, AccountStatus.ACTIVITY_BLOCKED, new HashMap<>(),
                    clients, false, true));

        return null;
    }
}
