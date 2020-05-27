package com.averno.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.averno.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		//Indicamos el título de la ventana del juego
		config.title = "El tesoro del Averno";
		//Indicamos la anchura de la ventana del juego
		config.width = 800;
		//Indicamos la altura de la ventana del juego
		config.height = 480;
		//Lanzamos el juego en su versión de escritorio
		new LwjglApplication(new MyGdxGame(), config);
	}
}
