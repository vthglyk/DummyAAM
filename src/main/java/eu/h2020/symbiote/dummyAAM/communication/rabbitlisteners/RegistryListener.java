package eu.h2020.symbiote.dummyAAM.communication.rabbitlisteners;

import eu.h2020.symbiote.core.cci.InformationModelRequest;
import eu.h2020.symbiote.core.cci.InformationModelResponse;
import eu.h2020.symbiote.core.cci.PlatformRegistryResponse;
import eu.h2020.symbiote.core.model.InterworkingService;
import eu.h2020.symbiote.core.model.Platform;
import eu.h2020.symbiote.core.model.RDFFormat;
import eu.h2020.symbiote.core.model.InformationModel;
import eu.h2020.symbiote.core.internal.InformationModelListResponse;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

        if (platform.getLabels().get(0).equals("reg400") ||
                platform.getLabels().get(0).equals("reg401")) {
            response.setStatus(400);
            response.setMessage("Status 400");
        }
        else if (platform.getLabels().get(0).equals("reg500")) {
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

//        if (platform.getId().equals("validPlatformOwner2Platform1")) {
//            response.setStatus(400);
//            return response;
//        }

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<InterworkingService> interworkingServices = new ArrayList<>();

        labels.add(platform.getId() + "Name");
        labels.add(platform.getId() + "Label");
        comments.add(platform.getId() + "Description");
        comments.add(platform.getId() + "Comment");
        InterworkingService service = new InterworkingService();
        service.setInformationModelId("model3_id");
        service.setUrl(platform.getId() + ".com");
        interworkingServices.add(service);

        platform.setLabels(labels);
        platform.setComments(comments);
        platform.setInterworkingServices(interworkingServices);
        platform.setEnabler(true);

        response.setStatus(200);
        response.setBody(platform);

        return response;

//        if (platform.getId().equals("noPlatform") ||
//                (platform.getId().equals("toCreatePlatform") && platform.getComments() == null))
//            response.setStatus(400);
//        else if (platform.getId().equals("toCreatePlatform") && platform.getComments() != null) {
//            InterworkingService interworkingService = new InterworkingService();
//            interworkingService.setUrl(platform.getInterworkingServices().get(0).getUrl());
//            interworkingService.setInformationModelId(platform.getInterworkingServices().get(0).getInformationModelId());
//
//            Platform storedPlatform = new Platform();
//            storedPlatform.setId(platform.getId());
//            storedPlatform.setLabels(platform.getLabels());
//            storedPlatform.setComments(platform.getComments());
//            storedPlatform.setInterworkingServices(Arrays.asList(interworkingService));
//
//            response.setMessage("Platform created");
//            response.setPlatform(storedPlatform);
//            response.setStatus(200);
//        }
//        else {
//            InterworkingService interworkingService = new InterworkingService();
//            interworkingService.setUrl("https://platform.com");
//            interworkingService.setInformationModelId("Information Model id");
//
//            Platform storedPlatform = new Platform();
//            storedPlatform.setId("testPlatformId");
//            storedPlatform.setLabels(Arrays.asList("testPlatformName"));
//            storedPlatform.setComments(Arrays.asList("testPlatformDescription"));
//            storedPlatform.setInterworkingServices(Arrays.asList(interworkingService));
//
//            response.setMessage("Platform exists");
//            response.setPlatform(storedPlatform);
//            response.setStatus(200);
//        }


//        return response;
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
        if (platform.getId().equals("validPlatformOwner2Platform2")) {
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
    public InformationModelListResponse listInformationModels(String s) {

        log.info("listInformationModels");


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
        model2.setOwner("validPlatformOwner2");
        model2.setUri("model2_uri");
        model2.setRdf("model2_rdf");
        model2.setRdfFormat(RDFFormat.N3);

        InformationModel model3 = new InformationModel();
        model3.setId("model3_id");
        model3.setName("a_name");
        model3.setOwner("validPlatformOwner2");
        model3.setUri("model3_uri");
        model3.setRdf("model3_rdf");
        model3.setRdfFormat(RDFFormat.JSONLD);

        InformationModel model4 = new InformationModel();
        model4.setId("model4_id");
        model4.setName("A_name");
        model4.setOwner("model3_owner");
        model4.setUri("model3_uri");
        model4.setRdf("model3_rdf");
        model4.setRdfFormat(RDFFormat.JSONLD);

        ArrayList<InformationModel> informationModels = new ArrayList<>();
        informationModels.add(model1);
        informationModels.add(model2);
        informationModels.add(model3);
        informationModels.add(model4);
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

        log.info("deleteInformationModel");
        InformationModelResponse response = new InformationModelResponse();

        if (request.getBody().getId().equals("model2_id")) {
            response.setMessage("You cannot delete it");
            response.setStatus(400);
        } else {
            response.setStatus(200);
        }


        return response;
    }

}
