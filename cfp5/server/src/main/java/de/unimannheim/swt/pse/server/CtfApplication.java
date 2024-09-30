package de.unimannheim.swt.pse.server;

import de.unimannheim.swt.pse.server.game.DummyGame;
import de.unimannheim.swt.pse.server.game.Game;
import de.unimannheim.swt.pse.server.game.GameEngineGame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This is the main class (entry point) of your webservice.
 */
@SpringBootApplication
public class CtfApplication {
	static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(CtfApplication.class, args);
	}

	public static void kill(){
		context.close();
	}

  /**
   * FIXME You need to return your game engine implementation here. Return an instance here.
   *
   * @return your {@link Game} engine.
   */
  public static Game createGameEngine() {
    // FIXME change object
    return new GameEngineGame();
  }

}
