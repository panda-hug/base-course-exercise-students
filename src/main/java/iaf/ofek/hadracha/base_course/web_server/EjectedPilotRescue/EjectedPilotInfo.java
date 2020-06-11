package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.AirSituation.Airplane;
import iaf.ofek.hadracha.base_course.web_server.Data.Coordinates;
import iaf.ofek.hadracha.base_course.web_server.Data.Entity;

import java.util.HashSet;
import java.util.Set;

public class EjectedPilotInfo implements Entity<EjectedPilotInfo> {
    private int id;
    private RescueGroup rescueGroup = new RescueGroup();

    public Coordinates coordinates;

    public String pilotName;

    /**
     * The rescue manager's client id, or null if non.
     */
    public String rescuedBy;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public EjectedPilotInfo clone() {
        try {
            return (EjectedPilotInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Not possible - Entity implements Cloneable");
        }
    }

    public void allocateAirplane(Airplane airplane, String controllerClientId){
        rescueGroup.allocateAirplane(airplane, controllerClientId);
    }

    private class RescueGroup {
        Set<Airplane> enRoute = new HashSet<>();
        Set<Airplane> arrived = new HashSet<>();

        public void allocateAirplane(Airplane airplane, String controlledClientId) {
            enRoute.add(airplane);
            airplane.flyTo(coordinates, controlledClientId);
            airplane.onArrivedAtDestination(this::airplaneArrived);
        }

        private void airplaneArrived(Airplane airplane) {
            enRoute.remove(airplane);
            arrived.add(airplane);

            if (enRoute.isEmpty())
                arrived.forEach(plane -> plane.unAllocate());

            arrived.clear();
        }
    }
}
