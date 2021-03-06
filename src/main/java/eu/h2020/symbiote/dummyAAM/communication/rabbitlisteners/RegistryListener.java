package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.core.cci.InformationModelRequest;
import eu.h2020.symbiote.core.cci.InformationModelResponse;
import eu.h2020.symbiote.core.cci.PlatformRegistryResponse;
import eu.h2020.symbiote.core.cci.SspRegistryResponse;
import eu.h2020.symbiote.core.internal.ClearDataRequest;
import eu.h2020.symbiote.core.internal.ClearDataResponse;
import eu.h2020.symbiote.core.internal.InformationModelListResponse;
import eu.h2020.symbiote.core.internal.RDFFormat;
import eu.h2020.symbiote.model.mim.InformationModel;
import eu.h2020.symbiote.model.mim.InterworkingService;
import eu.h2020.symbiote.model.mim.Platform;
import eu.h2020.symbiote.model.mim.SmartSpace;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@Component
public class RegistryListener {
    private static Log log = LogFactory.getLog(RegistryListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryPlatformRegistrationRequest", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.creationRequested}")
    )
    public PlatformRegistryResponse platformRegistrationRequest(Platform platform) {

        log.info("platformRegistrationRequest: "+ ReflectionToStringBuilder.toString(platform));

        PlatformRegistryResponse response = new PlatformRegistryResponse();

        if (platform.getName().equals("reg400") ||
                platform.getName().equals("reg401")) {
            response.setStatus(400);
            response.setMessage("Status 400");
        }
        else if (platform.getName().equals("reg500")) {
            response.setStatus(500);
            response.setMessage("Status 500");
        }
        else
            response.setStatus(200);

        return response;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryPlatformModificationRequest", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.modificationRequested}")
    )
    public PlatformRegistryResponse platformModificationRequest(Platform platform) {

        log.info("platformModificationRequest: "+ ReflectionToStringBuilder.toString(platform));

        PlatformRegistryResponse response = new PlatformRegistryResponse();

        if (platform.getName().equals("reg400") ||
                platform.getName().equals("reg401")) {
            response.setStatus(400);
            response.setMessage("Status 400");
        }
        else if (platform.getName().equals("reg500")) {
            response.setStatus(500);
            response.setMessage("Status 500");
        }
        else
            response.setStatus(200);

        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryGetPlatformDetailsRequest", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.platformDetailsRequested}")
    )
    public PlatformRegistryResponse getPlatformDetailsRequest(byte[] body) {

        String platformId;
        try {
            platformId = new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        log.info("getPlatformDetailsRequest: "+ platformId);


        PlatformRegistryResponse response = new PlatformRegistryResponse();

//        if (platform.getId().equals("validPO2Platform1")) {
//            response.setStatus(400);
//            return response;
//        }

        ArrayList<String> description = new ArrayList<>();
        ArrayList<InterworkingService> interworkingServices = new ArrayList<>();
        Platform platform = new Platform();
        InterworkingService service = new InterworkingService();

        if (platformId.equals("icom-platform")) {
            description.add(platformId + "Description");
            service.setInformationModelId("model2_id");
            service.setUrl("https://intracom.symbiote-h2020.eu/");
            interworkingServices.add(service);

            platform.setId(platformId);
            platform.setName(platformId);
            platform.setDescription(description);
            platform.setInterworkingServices(interworkingServices);
        } else {
            description.add(platformId + "Description");
            description.add(platformId + "Comment");
            service.setInformationModelId("model2_id");
            service.setUrl("https://" + platformId.toLowerCase() + ".com/");
            interworkingServices.add(service);

            platform.setId(platformId);
            platform.setName(platformId + "Name");
            platform.setDescription(description);
            platform.setInterworkingServices(interworkingServices);
        }

        if (platform.getId().equals("validPO2Platform1"))
            platform.setEnabler(true);
        else
            platform.setEnabler(false);


        response.setStatus(200);
        response.setBody(platform);

        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryPlatformRemovalRequest", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.removalRequested}")
    )
    public PlatformRegistryResponse platformRemovalRequest(Platform platform) {

        log.info("platformRemovalRequest: "+ ReflectionToStringBuilder.toString(platform));

        PlatformRegistryResponse response = new PlatformRegistryResponse();
        if (platform.getId().equals("validPO2Platform2")) {
            response.setStatus(400);
            response.setMessage("Take care of your resources first!");
        }
        else {
            response.setStatus(200);
        }
        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registrySspRegistrationRequest", durable = "${rabbit.exchange.ssp.durable}",
                    autoDelete = "${rabbit.exchange.ssp.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.ssp.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.ssp.autodelete}",
                    internal = "${rabbit.exchange.ssp.internal}", type = "${rabbit.exchange.ssp.type}"),
            key = "${rabbit.routingKey.ssp.creationRequested}")
    )
    public SspRegistryResponse sspRegistrationRequest(SmartSpace smartSpace) {

        log.info("sspRegistrationRequest: "+ ReflectionToStringBuilder.toString(smartSpace));

        SspRegistryResponse response = new SspRegistryResponse();

        if (smartSpace.getName().equals("reg400") ||
                smartSpace.getName().equals("reg401")) {
            response.setStatus(400);
            response.setMessage("Status 400");
        }
        else if (smartSpace.getName().equals("reg500")) {
            response.setStatus(500);
            response.setMessage("Status 500");
        }
        else
            response.setStatus(200);

        return response;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registrySspModificationRequest", durable = "${rabbit.exchange.ssp.durable}",
                    autoDelete = "${rabbit.exchange.ssp.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.ssp.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.ssp.durable}", autoDelete  = "${rabbit.exchange.ssp.autodelete}",
                    internal = "${rabbit.exchange.ssp.internal}", type = "${rabbit.exchange.ssp.type}"),
            key = "${rabbit.routingKey.ssp.modificationRequested}")
    )
    public SspRegistryResponse sspModificationRequest(SmartSpace smartSpace) {

        log.info("sspModificationRequest: "+ ReflectionToStringBuilder.toString(smartSpace));

        SspRegistryResponse response = new SspRegistryResponse();

        if (smartSpace.getName().equals("reg400") ||
                smartSpace.getName().equals("reg401")) {
            response.setStatus(400);
            response.setMessage("Status 400");
        }
        else if (smartSpace.getName().equals("reg500")) {
            response.setStatus(500);
            response.setMessage("Status 500");
        }
        else
            response.setStatus(200);

        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryGetSspDetailsRequest", durable = "${rabbit.exchange.ssp.durable}",
                    autoDelete = "${rabbit.exchange.ssp.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.ssp.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.ssp.durable}", autoDelete  = "${rabbit.exchange.ssp.autodelete}",
                    internal = "${rabbit.exchange.ssp.internal}", type = "${rabbit.exchange.ssp.type}"),
            key = "${rabbit.routingKey.ssp.sspDetailsRequested}")
    )
    public SspRegistryResponse getSspDetailsRequest(byte[] body) {

        String sspId;
        try {
            sspId = new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn(e);
            return null;
        }

        log.info("getSspDetailsRequest: "+ sspId);


        SspRegistryResponse response = new SspRegistryResponse();

        ArrayList<String> description = new ArrayList<>();
        ArrayList<InterworkingService> interworkingServices = new ArrayList<>();
        SmartSpace smartSpace = new SmartSpace();
        InterworkingService service = new InterworkingService();

        description.add(sspId + "Description");
        description.add(sspId + "Comment");

        if (!sspId.equals("SSP_validPO2SSP2"))
            service.setInformationModelId("model2_id");
        service.setUrl("https://" + sspId.toLowerCase() + ".com/");
        interworkingServices.add(service);

        smartSpace.setId(sspId);
        smartSpace.setName(sspId + "Name");
        smartSpace.setDescription(description);
        smartSpace.setInterworkingServices(interworkingServices);

        response.setStatus(200);
        response.setBody(smartSpace);

        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registrySspRemovalRequest", durable = "${rabbit.exchange.ssp.durable}",
                    autoDelete = "${rabbit.exchange.ssp.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.ssp.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.ssp.durable}", autoDelete  = "${rabbit.exchange.ssp.autodelete}",
                    internal = "${rabbit.exchange.ssp.internal}", type = "${rabbit.exchange.ssp.type}"),
            key = "${rabbit.routingKey.ssp.removalRequested}")
    )
    public SspRegistryResponse sspRemovalRequest(SmartSpace smartSpace) {

        log.info("sspRemovalRequest: "+ ReflectionToStringBuilder.toString(smartSpace));

        SspRegistryResponse response = new SspRegistryResponse();
        if (smartSpace.getId().equals("validPO2SSP2")) {
            response.setStatus(400);
            response.setMessage("Take care of your resources first!");
        }
        else {
            response.setStatus(200);
        }
        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryListInfoModelRequest", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.model.allInformationModelsRequested}")
    )
    public InformationModelListResponse listInformationModels(byte[] body) {

        String s;
        try {
            s = new String(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        log.info("listInformationModels: " + s);


        InformationModelListResponse response = new InformationModelListResponse();
        response.setMessage("OK");
        response.setStatus(200);
        InformationModel model1 = new InformationModel();
        model1.setId("model1_id");
        model1.setName("model1_name");
        model1.setOwner("model1_owner");
        model1.setUri("model1_uri");
        model1.setRdf("model1_rdf");
        model1.setRdfFormat(RDFFormat.JSONLD);

        InformationModel model2 = new InformationModel();
        model2.setId("model2_id");
        model2.setName("Model2_name");
        model2.setOwner("validPO2");
        model2.setUri("model2_uri");
        model2.setRdf("model2_rdf");
        model2.setRdfFormat(RDFFormat.N3);

        InformationModel model3 = new InformationModel();
        model3.setId("model3_id");
        model3.setName("a_name");
        model3.setOwner("validPO2");
        model3.setUri("model3_uri");
        model3.setRdf("model3_rdf");
        model3.setRdfFormat(RDFFormat.JSONLD);

        InformationModel model4 = new InformationModel();
        model4.setId("model4_id");
        model4.setName("A_name");
        model4.setOwner("model4_owner");
        model4.setUri("model4_uri");
        model4.setRdf("model4_rdf");
        model4.setRdfFormat(RDFFormat.JSONLD);

        InformationModel model5 = new InformationModel();
        model5.setId("model5-id");
        model5.setName("5-name");
        model5.setOwner("validPO2");
        model5.setUri("model5_uri");
        model5.setRdf("model5_rdf");
        model5.setRdfFormat(RDFFormat.N3);

        ArrayList<InformationModel> informationModels = new ArrayList<>();
        informationModels.add(model1);
        informationModels.add(model2);
        informationModels.add(model3);
        informationModels.add(model4);
        informationModels.add(model5);
        response.setBody(informationModels);
        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryCreateInformationModel", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.model.creationRequested}")
    )
    public InformationModelResponse createInformationModel(InformationModelRequest request) {

        log.info("createInformationModel");
        log.info(ReflectionToStringBuilder.toString(request.getBody()));

        InformationModelResponse response = new InformationModelResponse();

        if (request.getBody().getName().equals("error")) {
            response.setMessage("You cannot create it");
            response.setStatus(400);
        } else {
            InformationModel infoModel = request.getBody();
            infoModel.setId(infoModel.getName() + "_id");
            response.setBody(infoModel);
            response.setStatus(200);
        }


        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryDeleteInformationModel", durable = "${rabbit.exchange.platform.durable}",
                    autoDelete = "${rabbit.exchange.platform.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.platform.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.platform.durable}", autoDelete  = "${rabbit.exchange.platform.autodelete}",
                    internal = "${rabbit.exchange.platform.internal}", type = "${rabbit.exchange.platform.type}"),
            key = "${rabbit.routingKey.platform.model.removalRequested}")
    )
    public InformationModelResponse deleteInformationModel(InformationModelRequest request) {

        log.info("deleteInformationModel for id: " + request.getBody().getId());
        InformationModelResponse response = new InformationModelResponse();

        if (request.getBody().getId().equals("model2_id")) {
            response.setMessage("You cannot delete it");
            response.setStatus(400);
        } else {
            response.setStatus(200);
        }


        return response;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "registryClearDataRequest", durable = "${rabbit.exchange.resource.durable}",
                    autoDelete = "${rabbit.exchange.resource.autodelete}", exclusive = "false"),
            exchange = @Exchange(value = "${rabbit.exchange.resource.name}", ignoreDeclarationExceptions = "true",
                    durable = "${rabbit.exchange.resource.durable}", autoDelete  = "${rabbit.exchange.resource.autodelete}",
                    internal = "${rabbit.exchange.resource.internal}", type = "${rabbit.exchange.resource.type}"),
            key = "${rabbit.routingKey.resource.clearDataRequested}")
    )
    public ClearDataResponse clearData(ClearDataRequest request) {

        log.info("ClearDataRequest for platform: " + ReflectionToStringBuilder.toString(request));

        if (request.getBody().equals("1"))
            return new ClearDataResponse(200, "Data Cleared", null);
        else
            return new ClearDataResponse(400, "Data WERE NOT Cleared", null);

    }

}
