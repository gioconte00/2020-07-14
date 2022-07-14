package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;


public class Simulator {
	
	//dati input
	private int soglia;
	private int N;
	private int nPartite;
	
	//dati output
	private int numPartiteSottoSoglia;  //quante partite un num di reporte inferiore alla soglia
	private double avgReporterPartita;  //quanti reporter hanno assistito in media a ogi partita
										
	
	//stato mondo 
	Graph<Team, DefaultWeightedEdge> grafo;
	Map<Team, Integer> statoAttuale;
	
	//coda eventi
	PriorityQueue<Event> queue;
	
	//costruttore
	public Simulator(Graph<Team, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
	}

	//inizializzo
	public void init(int N, int soglia, List<Match> partite) {
		
		this.N = 0;
		this.soglia = 0;
		this.avgReporterPartita = 0.0;
		this.numPartiteSottoSoglia = 0;
		
		this.statoAttuale = new HashMap<Team, Integer>();
		
		for(Team t : this.grafo.vertexSet()) {
			this.statoAttuale.put(t, this.N);
		}
		for(Match m : partite)
		this.queue.add(new Event
					(new Team(m.getTeamHomeID(), m.getTeamHomeNAME()),
							new Team(m.getTeamAwayID(), m.getTeamAwayNAME()),
								m.getReaultOfTeamHome(), m.getDate()));
		
	}
	
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		
		Team vincente = null;
		Team perdente = null;
		
		if(e.getVincente()==1) {
			vincente = e.getTeam1();
			perdente = e.getTeam2();
		} else if (e.getVincente()==-1) {
			vincente = e.getTeam2();
			perdente = e.getTeam1();
		}
		
		if(vincente!=null && perdente!=null) {
			//se il num di reportee totali alla partita < soglia
			if(statoAttuale.get(vincente)+statoAttuale.get(perdente)<this.soglia) {
				this.numPartiteSottoSoglia++;
			}
		
			
			//SQUADRA VINCENTE
			
			//aggiungo i reporter presenti della squadra vicnente
			this.avgReporterPartita += statoAttuale.get(vincente);
			
			if(Math.random()<0.5) {
				//prendo una squadra migliore della vincente
				Team t = getSquadraMigliore(vincente);
				//reporter che ha quella squadra attualmente
				int reporter = statoAttuale.get(vincente); 
				
				//se esiste una squadra migliore e ci sono dei reporter nelle vincente attuale
				//allora promuovo il reporter
				//quindi diminuisco quelli della squadra vincente e aumento quelli della squadra migliore
				if(t!=null && reporter>0) {
					statoAttuale.replace(vincente, reporter--);
					reporter = statoAttuale.get(t);
					statoAttuale.replace(t, reporter++);
				}
			}
			
			//SQUADRA PERDENTE
			
			//aggiungo i reporter presenti della squadra perdente
			this.avgReporterPartita += statoAttuale.get(perdente);
			
			if(Math.random()<0.2) {
				Team t = getSquadraPeggiore(perdente);
				//uno o più reporter vengono bocciati
				int reporter = (int)(Math.random()*statoAttuale.get(perdente)+1);
				if(t!=null && reporter>0) {
					//sottraggo il num di reporter bocciati
					statoAttuale.replace(perdente, statoAttuale.get(perdente)-reporter);
					statoAttuale.replace(t, statoAttuale.get(t)+reporter);
				}
			
			}
		}
	}

			
	
	private Team getSquadraPeggiore(Team team) {
		
		List<Team> peggiori = new ArrayList<>();
		
		for(Team t : this.grafo.vertexSet()) {
			if(!t.equals(team) && this.grafo.getEdge(team, t)!=null) 
				peggiori.add(t);
			//visto che il grafo è orientato posso vedere se esiste un arco
			//che va da team a t significa che sarà peggiore di team
		}
		
		//se esiste un team peggiore --> prendo casualmente
		if(peggiori.size()>0) {
			int random = (int) (Math.random()*peggiori.size()); //indice casuale
			return peggiori.get(random);
		}
		return null;
	}

	
	private Team getSquadraMigliore(Team team) {
			
			List<Team> migliori = new ArrayList<>();
			
			for(Team t : this.grafo.vertexSet()) {
				if(!t.equals(team) && this.grafo.getEdge(t, team)!=null) 
					migliori.add(t);
				
			}
			
			//se esiste un team migliore --> prendo casualmente
			if(migliori.size()>0) {
				int random = (int) (Math.random()*migliori.size()); //indice casuale
				return migliori.get(random);
			}
			return null;
		}

	public int getNumPartiteSottoSoglia() {
		return numPartiteSottoSoglia;
	}

	public double getAvgReporterPartita() {
		return avgReporterPartita;
	}
	
	
}
