package it.polito.tdp.PremierLeague.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {

	private Team team1;
	private Team team2;
	private int vincente;  //punti del team1
	private LocalDateTime time;
	
	
	public Event(Team team1, Team team2, int vincente, LocalDateTime time) {
		super();
		this.team1 = team1;
		this.team2 = team2;
		this.vincente = vincente;
		this.time = time;
	}


	public Team getTeam1() {
		return team1;
	}


	public Team getTeam2() {
		return team2;
	}


	public int getVincente() {
		return vincente;
	}


	public LocalDateTime getTime() {
		return time;
	}
	
	
	
	
}
