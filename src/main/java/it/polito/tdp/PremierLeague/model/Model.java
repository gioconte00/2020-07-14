package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Map<Integer, Team> idMap;
	private PremierLeagueDAO dao;
	private Graph<Team, DefaultWeightedEdge> grafo;
	
	private Map<Team, Integer> classifica; // classifica team-punti
	
	public Model() {
		dao = new PremierLeagueDAO();
	}
	
	public String creaGrafo() {
		
		idMap = new HashMap<>();
		dao.listAllTeams(idMap);
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, idMap.values());
		
		classifica = new HashMap<>(dao.getClassifica(idMap));
		for (Team t1: grafo.vertexSet()) {
			for (Team t2: grafo.vertexSet()) {
				if (!t1.equals(t2) && !grafo.containsEdge(t1, t2) && classifica.get(t1)>classifica.get(t2)) {
					Graphs.addEdge(grafo, t1, t2, classifica.get(t1)-classifica.get(t2));
				}
			}
		}
		
		String result = "Grafo creato: "+grafo.vertexSet().size()+" vertici, "+grafo.edgeSet().size()+" archi\n";
		return result;
		
	}
	
	public Collection<Team> getAllTeams() {
		return idMap.values();
	}
	
	public String getClassificaSquadra(Team team) {
		
		List<TeamsDiffPunti> peggiori = new ArrayList<>();
		List<TeamsDiffPunti> migliori = new ArrayList<>();
		String result = "";
		
		for (Team t: classifica.keySet()) {
			if (!t.equals(team) && classifica.get(t)>classifica.get(team))
				migliori.add(new TeamsDiffPunti(t, classifica.get(t)-classifica.get(team)));
			if (!t.equals(team) && classifica.get(t)<classifica.get(team))
				peggiori.add(new TeamsDiffPunti(t, classifica.get(team)-classifica.get(t)));
		}
		
		Collections.sort(migliori);
		Collections.sort(peggiori);
		
		if (migliori.size()>0) {
			result += "SQUADRE MIGLIORI:\n";
			for (TeamsDiffPunti t: migliori)
				result += t.getT()+" ("+t.getDiffPunti()+")\n";
		}
		
		if (peggiori.size()>0) {
			result += "\nSQUADRE PEGGIORI:\n";
			for (TeamsDiffPunti t: peggiori)
				result += t.getT()+" ("+t.getDiffPunti()+")\n";
		}
		
		return result;
	}
	
	public String doSimulazione(int N, int soglia) {
		
		List<Match> partite = new ArrayList<>(this.dao.listAllMatches());
		
		Simulator s = new Simulator(grafo);
		s.init(N, soglia, partite);
		s.run();
		
		return "Reporter medi a partita: "+s.getAvgReporterPartita()+"\n"+
			"Numero partite con un numero di reporter critico: "+s.getNumPartiteSottoSoglia();
	}
}