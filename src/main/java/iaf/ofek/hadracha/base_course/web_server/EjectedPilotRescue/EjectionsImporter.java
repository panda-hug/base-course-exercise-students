package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.CrudDataBase;
import iaf.ofek.hadracha.base_course.web_server.Data.Entity;
import iaf.ofek.hadracha.base_course.web_server.Utilities.ListOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EjectionsImporter {

    @Value("${ejections.server.url}")
    public String EJECTION_SERVER_URL;
    @Value("${ejections.namespace}")
    public String NAMESPACE;


    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final RestTemplate restTemplate;
    private final CrudDataBase dataBase;
    private final ListOperations listOperations;
    private static final Double SHIFT_NORTH = 1.7;

    public EjectionsImporter(RestTemplateBuilder restTemplateBuilder, CrudDataBase dataBase, ListOperations listOperations) {
        restTemplate = restTemplateBuilder.build();
        this.dataBase = dataBase;
        this.listOperations = listOperations;
        executor.scheduleAtFixedRate(this::updateEjections, 1, 1, TimeUnit.SECONDS);

    }

    private void updateEjections() {
            List<EjectedPilotInfo> ejectionsFromServer = getEjectionsFromServer();
            if (ejectionsFromServer != null) {
                for (EjectedPilotInfo ejectedPilotInfo : ejectionsFromServer) {
                    ejectedPilotInfo.getCoordinates().lat += SHIFT_NORTH;
                }
            }
            updateDB(ejectionsFromServer);
    }

    private List<EjectedPilotInfo> getEjectionsFromServer(){
        try {
            ResponseEntity<List<EjectedPilotInfo>> responseEntity = restTemplate.exchange(
                    EJECTION_SERVER_URL + "/ejections?name=" + NAMESPACE, HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<EjectedPilotInfo>>() {
                    });
            return responseEntity.getBody();
        }catch (RestClientException e) {
            System.err.println("Could not get ejections: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void updateDB(List<EjectedPilotInfo> updatedEjections) {
        List<EjectedPilotInfo> previousEjections = dataBase.getAllOfType(EjectedPilotInfo.class);

        List<EjectedPilotInfo> ejectionsToAdd = subtractEjectionsList(updatedEjections, previousEjections);
        List<EjectedPilotInfo> ejectionsToRemove = subtractEjectionsList(previousEjections, updatedEjections);

        ejectionsToAdd.forEach(dataBase::create);
        ejectionsToRemove.stream().map(EjectedPilotInfo::getId).forEach(id -> dataBase.delete(id, EjectedPilotInfo.class));
    }

    private List<EjectedPilotInfo> subtractEjectionsList(List<EjectedPilotInfo> baseList, List<EjectedPilotInfo> elementsToRemove) {
        return listOperations.subtract(baseList, elementsToRemove, new Entity.ByIdEqualizer<>());
    }

}
