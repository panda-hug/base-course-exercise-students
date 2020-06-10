package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.InMemoryMapDataBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ejectedPilotRescue")
public class EjectedPilotHandler {

    @Autowired
    InMemoryMapDataBase dataBase;

    @Autowired
    AirplanesAllocationManager airplanesAllocationManager;

    @GetMapping("/infos")
    List<EjectedPilotInfo> getEjectedPilotInfos() {
        List<EjectedPilotInfo> ejectedPilotInfos = dataBase.getAllOfType(EjectedPilotInfo.class);
        // More logic?
        return ejectedPilotInfos;
    }

    @GetMapping("/takeResponsibility")
    // TODO: Correct return type?
    void takeResponsibility(@CookieValue(name = "client-id") String clientId, @RequestParam int ejectionId) {
        EjectedPilotInfo ejectedInfo = dataBase.getByID(ejectionId, EjectedPilotInfo.class);
        if (ejectedInfo != null && ejectedInfo.rescuedBy == null) {
            airplanesAllocationManager.allocateAirplanesForEjection(ejectedInfo, clientId);
        }
    }


}
