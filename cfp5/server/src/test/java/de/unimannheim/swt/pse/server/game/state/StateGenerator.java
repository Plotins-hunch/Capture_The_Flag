package de.unimannheim.swt.pse.server.game.state;

public class StateGenerator {

  public Piece[] generatePieces(int pieceCount, int teamCount) {
    Piece[] pieces = new Piece[pieceCount];
    for (int i = 1; i <= teamCount; i++) {
      for (int j = 1; j <= pieceCount; j++) {
        pieces[j - 1] = new Piece();
        pieces[j - 1].setTeamId(i + "");
        pieces[j - 1].setId(j + "");
      }
    }
    return pieces;
  }

  public Team[] generateTeams(int teamCount) {
    Team[] teams = new Team[teamCount];
    for (int i = 1; i <= teamCount; i++) {
      teams[i] = new Team();
      teams[i].setId(i + "");
    }
    return teams;
  }


}
