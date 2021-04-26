package it.polito.tdp.metroparis.model;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ProvaGrafo {

	public static void main(String[] args) {
		// creo grafo semplice con archi bidirezionali
		Graph<String, DefaultEdge> grafo = new SimpleGraph<>(DefaultEdge.class);
		
		grafo.addVertex("UNO");
		grafo.addVertex("DUE");
		grafo.addVertex("TRE");
		
		grafo.addEdge("UNO", "TRE");
		grafo.addEdge("TRE", "DUE");
		//grafo.addEdge("UNO", "UNO"); //per SimpleGraph da errore perchè contro la sua definizione
			//--> per altri tipi può essere accettabile
		//grafo.addEdge("UNO", "SETTE"); // da errore perchè cerca con equals questo possibile oggetti nel grafo
		
		System.out.println(grafo);
	}

}
