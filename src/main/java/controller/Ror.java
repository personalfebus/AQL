package controller;

import java.io.Serializable;
import java.util.List;

public class Ror implements Serializable {
    private final int id;
    private String name;
    private List<Integer> neighbours;

    public Ror(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getNeighbours() {
        return neighbours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNeighbours(List<Integer> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "controller.Ror{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", neighbours=" + neighbours +
                '}';
    }
}
