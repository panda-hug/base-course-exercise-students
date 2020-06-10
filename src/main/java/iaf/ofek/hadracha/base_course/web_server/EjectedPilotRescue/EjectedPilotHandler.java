package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.Data.InMemoryMapDataBase;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ejectedPilotRescue")
public class EjectedPilotHandler {

    @Autowired
    InMemoryMapDataBase dataBase;

    @GetMapping("/infos")
    List<EjectedPilotInfo> getEjectedPilotInfos() {
        @NotNull List<EjectedPilotInfo> pilotInfo = dataBase.getAllOfType(EjectedPilotInfo.class);
        // More logic?
        return pilotInfo;
    }
}
