module ai {
  requires server;
  requires spring.web;
  requires java.desktop;
  requires jakarta.validation;
  exports de.unimannheim.swt.pse.ai.minimax;
  requires java.sql;
  exports de.unimannheim.swt.pse.ai.mcts;
  exports de.unimannheim.swt.pse.ai.advancedMCTS;
  //requires client;
  //requires javafx.base;
}