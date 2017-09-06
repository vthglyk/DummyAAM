package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.security.commons.Certificate;
import eu.h2020.symbiote.security.commons.SecurityConstants;
import eu.h2020.symbiote.security.commons.Token;
import eu.h2020.symbiote.security.commons.enums.ManagementStatus;
import eu.h2020.symbiote.security.commons.enums.OperationType;
import eu.h2020.symbiote.security.commons.enums.UserRole;
import eu.h2020.symbiote.security.communication.payloads.*;
import eu.h2020.symbiote.security.helpers.ECDSAHelper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    private SecureRandom random = new SecureRandom();

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
        PlatformManagementResponse response = new PlatformManagementResponse();


        if (platformManagementRequest.getPlatformOwnerCredentials() == null ||
                (platformManagementRequest.getPlatformOwnerCredentials().getUsername() == null ||
                        platformManagementRequest.getPlatformOwnerCredentials().getPassword() == null))
            response.setRegistrationStatus(ManagementStatus.ERROR);
        else if (platformManagementRequest.getOperationType() == OperationType.CREATE) {
            log.info("OperationType.CREATE");
            if (!platformManagementRequest.getPlatformInstanceId().equals("exists") &&
                    !platformManagementRequest.getPlatformInstanceId().equals("error")) {
                response.setPlatformId(platformManagementRequest.getPlatformInstanceId());
                response.setRegistrationStatus(ManagementStatus.OK);
            } else if (platformManagementRequest.getPlatformInstanceId().equals("exists")) {
                response.setRegistrationStatus(ManagementStatus.PLATFORM_EXISTS);
            } else if (platformManagementRequest.getPlatformInstanceId().equals("error")) {
                response.setRegistrationStatus(ManagementStatus.ERROR);
            }
        } else if (platformManagementRequest.getOperationType() == OperationType.DELETE) {
            log.info("OperationType.DELETE");
            if (!platformManagementRequest.getPlatformInstanceId().equals("reg401") &&
                    !platformManagementRequest.getPlatformInstanceId().equals("validPlatformOwner2Platform1")) {
                response.setPlatformId(platformManagementRequest.getPlatformInstanceId());
                response.setRegistrationStatus(ManagementStatus.OK);
            } else {
                response.setRegistrationStatus(ManagementStatus.ERROR);
            }
        }


        return response;
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

            if (username.contains("validPlatformOwner")) {

                OwnedPlatformDetails details = new OwnedPlatformDetails(username + "Platform1",
                        "http://" + username + "Platform1.com",
                        username + "PlatformFriendlyName", new Certificate(), new HashMap<>());

                set.add(details);
            }

            if (username.equals("validPlatformOwner2")) {
                set.add(new OwnedPlatformDetails(username + "Platform2",
                        "http://" + username + "Platform2.com",
                        username + "Platform2FriendlyName", new Certificate(), new HashMap<>()));
                set.add(new OwnedPlatformDetails(username + "Platform3",
                        "http://" + username + "Platform3.com",
                        username + "Platform3FriendlyName", new Certificate(), new HashMap<>()));
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

        if (userManagementRequest.getUserCredentials().getUsername().equals("valid"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails());
        if (userManagementRequest.getUserCredentials().getUsername().equals("validPlatformOwner"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPlatformOwner", ""),
                    "", "", UserRole.PLATFORM_OWNER, new HashMap<>(), new HashMap<>()));
        if (userManagementRequest.getUserCredentials().getUsername().equals("validPlatformOwner2"))
            return new UserDetailsResponse(HttpStatus.OK, new UserDetails(new Credentials("validPlatformOwner2", ""),
                    "", "", UserRole.PLATFORM_OWNER, new HashMap<>(), new HashMap<>()));
        else if (userManagementRequest.getUserCredentials().getUsername().equals("wrongUsername"))
            return new UserDetailsResponse(HttpStatus.BAD_REQUEST, new UserDetails());
        else if (userManagementRequest.getUserCredentials().getUsername().equals("wrongUserPassword"))
            return new UserDetailsResponse(HttpStatus.UNAUTHORIZED, new UserDetails());
        else if (userManagementRequest.getUserCredentials().getUsername().equals("wrongAdminPassword"))
            return new UserDetailsResponse(HttpStatus.FORBIDDEN, new UserDetails());

        return null;
    }

    private String buildAuthorizationToken(String subject, Map<String, String> attributes, byte[] subjectPublicKey,
                                           Token.Type tokenType, Long tokenValidity, String issuer,
                                           PublicKey issuerPublicKey, PrivateKey issuerPrivateKey) {
        ECDSAHelper.enableECDSAProvider();

        String jti = String.valueOf(random.nextInt());
        Map<String, Object> claimsMap = new HashMap<>();

        // Insert AAM Public Key
        claimsMap.put("ipk", Base64.getEncoder().encodeToString(issuerPublicKey.getEncoded()));

        //Insert issuee Public Key
        claimsMap.put("spk", Base64.getEncoder().encodeToString(subjectPublicKey));

        //Add symbIoTe related attributes to token
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                claimsMap.put(SecurityConstants.SYMBIOTE_ATTRIBUTES_PREFIX + entry.getKey(), entry.getValue());
            }
        }

        //Insert token type
        claimsMap.put(SecurityConstants.CLAIM_NAME_TOKEN_TYPE, tokenType);

        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setClaims(claimsMap);
        jwtBuilder.setId(jti);
        jwtBuilder.setIssuer(issuer);
        jwtBuilder.setSubject(subject);
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + tokenValidity));
        jwtBuilder.signWith(SignatureAlgorithm.ES256, issuerPrivateKey);

        return jwtBuilder.compact();
    }

    static public class DateUtil
    {
        static Date addDays(Date date, int days)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days); //minus number would decrement the days
            return cal.getTime();
        }
    }
}
