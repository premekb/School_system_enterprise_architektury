package cz.cvut.kbss.ear.project.kosapi.links;

public class RoomLink extends AtomLink {

    @Override
    public String toString() {
        return this.url.split("/")[1];
    }
}
