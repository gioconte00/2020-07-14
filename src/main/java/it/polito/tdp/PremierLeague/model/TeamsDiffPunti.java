package it.polito.tdp.PremierLeague.model;

public class TeamsDiffPunti implements Comparable<TeamsDiffPunti>{
	
	private Team t;
	private Integer diffPunti;
	
	public TeamsDiffPunti(Team t, int diffPunti) {
		super();
		this.t = t;
		this.diffPunti = diffPunti;
	}

	public Team getT() {
		return t;
	}

	public int getDiffPunti() {
		return diffPunti;
	}

	@Override
	public int compareTo(TeamsDiffPunti o) {
		return this.diffPunti.compareTo(o.getDiffPunti());
	}
	
	

}
	
	
