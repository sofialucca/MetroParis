package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata, Fermata> predecessore;
	
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
		
		this.predecessore = new HashMap<>();
		this.predecessore.put(partenza, null);
		
		//creo listener per ottenere info di fermate precedenti
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){ //essendo interfaccia va implementato tale che viene definito un oggetto con classe senza nome
				//-->creazione di classe inline

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			}
			//e rappresenta evento da cui si estraggono informazioni necessarie
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				//conviene usare questo metodo perchè memorizza sia vertici che arco tale da non avere operazioni complicate per trovare predecessore
				DefaultEdge arco = e.getEdge();
				Fermata a = grafo.getEdgeSource(arco);
				Fermata b = grafo.getEdgeTarget(arco);
				//essendo grafo non orientato target e source sono associati alla creazione quindi devo controllare se si è già attraversato uno due perchè il verso di attraversamento è indifferente
				//ho scoperto 'a' arrivando da 'b' se b lo si conosce già
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					predecessore.put(a, b);
//					System.out.println(a+" scoperto da "+b);
				}else if(predecessore.containsKey(a) && !predecessore.containsKey(b)){
					//se conoscevo 'a' allora si ottiene 'b'
					predecessore.put(b, a);
//					System.out.println(b+" scoperto da "+a);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
/*				Fermata nuova = e.getVertex();
//				Fermata precedente ; //vertice adiacente a 'nuova' e che sia già presente nella key map
					//ciclo nella mappa
				predecessore.put(nuova,precedente); //closure permette che la classe definita all'interno di un'altra classe può accedere ai parametri della classe in cui è contenuta
*/				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			}
			
		});
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
	
	//per ottenere una lista ordinata del cammino tra due vertici
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {
		fermateRaggiungibili(partenza); // usato per creare mappa predecessori
			//--> output è una lista di tutte le fermate quindi non mi serve
		
		List<Fermata> result = new LinkedList<>(); // conviene rispetto a ArrayList perchè ha costo minore l'aggiunta in testa
		result.add(arrivo);
		Fermata f = arrivo;
		while(predecessore.get(f)!=null) {
			f = predecessore.get(f);			
			result.add(0,f);
		}
		
		return result;
	}
}
