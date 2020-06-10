package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.AirSituation.AirSituationProvider;
import iaf.ofek.hadracha.base_course.web_server.AirSituation.Airplane;
import iaf.ofek.hadracha.base_course.web_server.AirSituation.AirplaneKind;
import iaf.ofek.hadracha.base_course.web_server.Data.Coordinates;
import iaf.ofek.hadracha.base_course.web_server.Data.InMemoryMapDataBase;
import iaf.ofek.hadracha.base_course.web_server.Utilities.GeographicCalculations;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ejectedPilotRescue")
public class EjectedPilotHandler {

    @Autowired
    InMemoryMapDataBase dataBase;
    @Autowired
    AirSituationProvider airSituation;
    @Autowired
    GeographicCalculations geoCalculator;

    @GetMapping("/infos")
    List<EjectedPilotInfo> getEjectedPilotInfos() {
        @NotNull List<EjectedPilotInfo> pilotInfo = dataBase.getAllOfType(EjectedPilotInfo.class);
        // More logic?
        return pilotInfo;
    }

    @GetMapping("/takeResponsibility")
    // TODO: Correct return type?
    Object takeResponsibility(@CookieValue(name = "client-id") String clientId, @RequestParam int ejectionId) {
        // all the logic
        return null;
    }

    private List<Airplane> getClosestAvailableAirplanes(Coordinates crashLocation) {
        List<Airplane> allPlanes = airSituation.getAllAirplanes();

        // Split list by class of airplane (Fighter/Drone/etc)
        Map<AirplaneKind, List<Airplane>> planesByClass = allPlanes.stream().collect(Collectors.groupingBy(
                plane -> plane.getAirplaneKind().getBaseKind())
        );

        // Get the closest available plane of each type
        return planesByClass.values().stream().map(classOfAirplanes -> classOfAirplanes.stream()
                .sorted((plane1, plane2) -> Double.compare(
                            geoCalculator.distanceBetween(plane1.coordinates, crashLocation),
                            geoCalculator.distanceBetween(plane2.coordinates, crashLocation)
                        )
                ).filter(plane -> !plane.isAllocated()).findFirst().get()).collect(Collectors.toList());
    }
}
