package unal.optimization.Entity;

import com.sun.org.apache.xml.internal.utils.Hashtree2Node;

import java.util.*;

/**
 * Created by root on 11/20/16.
 */
public class Graph {
    private Map<String, ArrayList<Object> > nodos;
    private Map< String, Map<String,Integer> > rutas;

    public Graph(){

    }

    public Map<String, ArrayList<Object>> getNodos() {
        return nodos;
    }

    public void setNodos(Map<String, ArrayList<Object>> nodos) {
        this.nodos = nodos;
    }

    public Map<String, Map<String, Integer>> getRutas() {
        return rutas;
    }

    public void setRutas(Map<String, Map<String, Integer>> rutas) {
        this.rutas = rutas;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodos +
                ", paths=" + rutas +
                '}';

    }
}
