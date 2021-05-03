package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
		//posso usare i metodi statici della classe Graphs
		/*for(Fermata f : fermata) {
			this.grafo.addVertex(f);
		}*/
		
		Graphs.addAllVertices(this.grafo, fermate);
		
		//aggiungiamo archi
		
		//tempi eterni perchè il grafo è grande quindi in questo caso sarebbe troppo lungo
/*		for(Fermata f1: this.grafo.vertexSet()) {
			
			for(Fermata f2:this.grafo.vertexSet()) {
				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
					this.grafo.addEdge(f1, f2);
				}
			}
		}
*/		
		List<Connessione> connessioni = dao.getAllConnessione(fermate);
		for(Connessione c: connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		}
		System.out.println("Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+this.grafo.edgeSet().size()+" archi");
		
/* 		Fermata f;
		Set<DefaultEdge> archi = this.grafo.edgesOf(f); // ottengo un set di archi entranti e uscenti da tale vertice
		
		//voglio trovare l'arco che la collega ad unaltra specifica fermata

		for(DefaultEdge e: archi) {
			versione non statica
			Fermata f1 = this.grafo.getEdgeSource(e); //trovo il vertice sorgente di tale edge quando ho arco orientato, ma se ho arco non orientato si basa su come è stato salvato
			//oppure
			Fermata f2 = this.grafo.getEdgeTarget(e); //trovo vertice target
			
			if(f1.equals(f)) {
				// f2 è quello che mi serve
			}else {
				// f1 è quello che mi serve
			}
			
			f1 = Graphs.getOppositeVertex(this.grafo, e, f); //si ottiene vertice opposto dato un arco e il vertice che si conosce
		}
*/		
		//metodo senza ciclo e che non richiede uso di archi
		//List<Fermata> fermateAdiacenti = Graphs.successorListOf(null, null); // ottengo lista di fermate adiacenti/successive rispetto a dato vertice
			// --> nel caso di archi orientati uso predecessorListOf per archi entranti ma a livello non ordinato questi metodi sono intercambiabili
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata partenza) {
		//in ampiezza
		BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
		
		//in profondità
		//DepthFirstIterator<Fermata,DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo,partenza);
		
		List<Fermata> result = new ArrayList<>();
		
		while(bfv.hasNext()) {
			Fermata f = bfv.next();
			result.add(f);
		}
		
		return result;
	}
	
	//se usato numerose volte conviene avere una mappa tale che si consulti dierattamente quella invece di creare un loop ogni volta
	public Fermata trovaFermata(String nome) {
		for(Fermata f: this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}
}
