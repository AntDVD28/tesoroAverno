package com.averno.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Clase principal del juego
 */
public class MyGdxGame extends ApplicationAdapter {

	//Estados del juego
	public enum GameState {
		MENU, PLAY, GAME_OVER, WIN
	}
	//Variable en la que guardamos el estado del juego
	public static GameState gameState;
	//Necesitamos un skin y un escenario para presentar elementos como botones en las pantallas del menú
	private Skin skin;
	private Stage stage;
	//Objeto que recoge el mapa de baldosas
	private TiledMap mapa;
	//Objeto con el que se pinta el mapa de baldosas
	private TiledMapRenderer mapaRenderer;
	//Cámara que nos da la vista del juego
	private OrthographicCamera camara;
	// Atributo en el que se cargarán las imagenes del personaje y NPCs
	private Texture img, imgNPC1, imgNPC2, imgNPC3, imgNPC4, imgNPC5, imgNPC6;
	//Atributo que permite dibujar imágenes 2D
	private SpriteBatch sb;

	//Objeto de la clase MyInputProcessor la cual se encarga de la gestión del movimiento del pesonaje
	MyInputProcessor mip;
	//Objetos de la clase NPC
	NPC npc1, npc2, npc3, npc4, npc5, npc6, npc7, npc8;

	//Variables auxiliares que utilizaremos para cargar la información del movimiento del jugador
	TextureRegion cuadroActual;
	Animation jugador;
	float jugadorX, jugadorY;

	//Variables auxiliares que utilizaremos para cargar la información de los npcs
	TextureRegion cuadroActualNPC, cuadroActualNPC2, cuadroActualNPC3, cuadroActualNPC4, cuadroActualNPC5, cuadroActualNPC6, cuadroActualNPC7, cuadroActualNPC8;
	Animation noJugador;
	float noJugadorX, noJugadorX2, noJugadorX3, noJugadorX4, noJugadorX5, noJugadorX6, noJugadorX7, noJugadorX8;
	float noJugadorY, noJugadorY2, noJugadorY3, noJugadorY4, noJugadorY5, noJugadorY6, noJugadorY7, noJugadorY8;

	// Tamaño del mapa de baldosas.
	static protected int mapaAncho, mapaAlto;
	//Atributos que indican la anchura y la altura de un tile del mapa de baldosas
	static protected int anchoCelda,altoCelda;

	//Capas que contienes los obstaculos y los premios
	private TiledMapTileLayer capaObstaculos;
	static protected TiledMapTileLayer capaPremios;

	//Matrices dónde guardaremos los obstaculos y premios
	static protected boolean[][] obstaculo;
	static protected boolean[][] premio;

	//Música del juego
	private Music musica;

	//Sonido colisión NPC
	static protected Sound sonidoObstaculo;
	static protected Sound sonidoPasos;
	private Sound sonidoColisionEnemigo;
	static protected Sound sonidoPremio;
	
	@Override
	public void create () {

		//Creamos una cámara y la vinculamos con el lienzo del juego
		//En este caso le damos unos valores de tamaño que haga que el juego se muestre de forma idéntica en todas las plataformas
		camara = new OrthographicCamera(800, 480);
		//Posicionamos la vista de la cámara para que su vértice inferior izquierdo sea (0,0)
		camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);

		camara.update();

		// Cargamos la imagen del caballero en el objeto img de la clase Texture.
		img = new Texture(Gdx.files.internal("caballero.png"));

		// Cargamos la imagen de los frames de los NPCs en el objeto img de la clase Texture.
		imgNPC1 = new Texture(Gdx.files.internal("esqueleto.png"));
		imgNPC2 = new Texture(Gdx.files.internal("gargola.png"));
		imgNPC3 = new Texture(Gdx.files.internal("muerte.png"));
		imgNPC4 = new Texture(Gdx.files.internal("fantasma.png"));
		imgNPC5 = new Texture(Gdx.files.internal("mago.png"));
		imgNPC6 = new Texture(Gdx.files.internal("ogro.png"));

		//Creamos el objeto SpriteBatch que nos permitirá representar adecuadamente los sprite en el método render()
		sb= new SpriteBatch();

		//Cargamos el mapa de baldosas desde la carpeta assets
		mapa = new TmxMapLoader().load("mapa.tmx");
		mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

		//Determinamos el alto y ancho del mapa de baldosas. Para ello necesitamos extraer la capa
		//base del mapa y, a partir de ella, determinamos el número de celdas a lo ancho y alto,
		//así como el tamaño de la celda, que multiplicando por el número de celdas a lo alto y
		//ancho, da como resultado el alto y ancho en pixeles del mapa.
		TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
		anchoCelda = (int) capa.getTileWidth();
		altoCelda = (int) capa.getTileHeight();
		mapaAncho = capa.getWidth() * anchoCelda;
		mapaAlto = capa.getHeight() * altoCelda;

		//*********************Cargar obstáculos****************/
		//Cargamos la capa de los obstáculos, que es la tercera capa en el TiledMap.
		capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(2);
		//Cargamos la matriz de los obstáculos del mapa de baldosas.
		int anchoCapa = capaObstaculos.getWidth(), altoCapa = capaObstaculos.getHeight();
		obstaculo = new boolean[anchoCapa][altoCapa];
		for (int x = 0; x < anchoCapa; x++) {
			for (int y = 0; y < altoCapa; y++) {
				obstaculo[x][y] = (capaObstaculos.getCell(x, y) != null);
			}
		}
		//********************Cargar premios********************************/
		//Cargamos la capa de los premios, que es la cuarta capa en el TiledMap.
		capaPremios = (TiledMapTileLayer) mapa.getLayers().get(3);
		//Cargamos la matriz de los premios del mapa de baldosas
		int anchoCapaP = capaPremios.getWidth(), altoCapaP = capaPremios.getHeight();
		premio = new boolean[anchoCapaP][altoCapaP];
		for (int x = 0; x < anchoCapaP; x++) {
			for (int y = 0; y < altoCapaP; y++) {
				premio[x][y] = (capaPremios.getCell(x, y) != null);
				/*if(premio[x][y]){
					System.out.println("x:"+x+" , "+"y:"+y);
				}*/

			}
		}

		//Creamos una instancia de la clase MyInputProcessor
		mip = new MyInputProcessor(camara, mapa, img);

		//Instancias de los NPC
		npc1 = new NPC(imgNPC1, 255, 390, 255, 230); //esqueleto entrada
		npc2 = new NPC(imgNPC2, 350, 350, 650, 350); //gargola arriba
		npc3 = new NPC(imgNPC3, 450, 150, 450, 370); //muerte
		npc4 = new NPC(imgNPC4, 160, 60, 160, 220); //fantasma
		npc5 = new NPC(imgNPC2, 300, 60, 550, 60); //gargola abajo
		npc6 = new NPC(imgNPC2, 250, 250, 500, 0); //gargola en diagonal
		npc7 = new NPC(imgNPC5, 610, 40, 610, 180); //mago
		npc8 = new NPC(imgNPC6, 540, 300, 740, 300); //ogro

		//Inicializamos la música de fondo del juego y la reproducimos.
		musica = Gdx.audio.newMusic(Gdx.files.internal("dungeon.mp3"));
		//Damos al volumen un valor del 25%
		musica.setVolume(.25f);
		musica.play();
		musica.setLooping(true);

		//Inicializamos los atributos de los efectos de sonido.
		sonidoColisionEnemigo = Gdx.audio.newSound(Gdx.files.internal("game-over.wav"));
		sonidoPasos = Gdx.audio.newSound(Gdx.files.internal("movimiento.ogg"));
		sonidoObstaculo = Gdx.audio.newSound(Gdx.files.internal("wall.wav"));
		sonidoPremio = Gdx.audio.newSound(Gdx.files.internal("success.wav"));

		//Comenzamos el juego en el menú
		gameState = GameState.MENU;
	}

	/**
	 * Método para resetear todas las variables del juego. El método create sólo se ejecutará una vez al inicio.
	 */
	public void reset_game(){

		//Creamos una cámara y la vinculamos con el lienzo del juego
		//En este caso le damos unos valores de tamaño que haga que el juego se muestre de forma idéntica en todas las plataformas
		camara = new OrthographicCamera(800, 480);
		//Posicionamos la vista de la cámara para que su vértice inferior izquierdo sea (0,0)
		camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);

		camara.update();

		// Cargamos la imagen del caballero en el objeto img de la clase Texture.
		img = new Texture(Gdx.files.internal("caballero.png"));

		// Cargamos la imagen de los frames de los NPCs en el objeto img de la clase Texture.
		imgNPC1 = new Texture(Gdx.files.internal("esqueleto.png"));
		imgNPC2 = new Texture(Gdx.files.internal("gargola.png"));
		imgNPC3 = new Texture(Gdx.files.internal("muerte.png"));
		imgNPC4 = new Texture(Gdx.files.internal("fantasma.png"));
		imgNPC5 = new Texture(Gdx.files.internal("mago.png"));
		imgNPC6 = new Texture(Gdx.files.internal("ogro.png"));

		//Creamos el objeto SpriteBatch que nos permitirá representar adecuadamente los sprite en el método render()
		sb= new SpriteBatch();

		//Cargamos el mapa de baldosas desde la carpeta assets
		mapa = new TmxMapLoader().load("mapa.tmx");
		mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

		//Determinamos el alto y ancho del mapa de baldosas. Para ello necesitamos extraer la capa
		//base del mapa y, a partir de ella, determinamos el número de celdas a lo ancho y alto,
		//así como el tamaño de la celda, que multiplicando por el número de celdas a lo alto y
		//ancho, da como resultado el alto y ancho en pixeles del mapa.
		TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
		anchoCelda = (int) capa.getTileWidth();
		altoCelda = (int) capa.getTileHeight();
		mapaAncho = capa.getWidth() * anchoCelda;
		mapaAlto = capa.getHeight() * altoCelda;

		//*********************Cargar obstáculos****************/
		//Cargamos la capa de los obstáculos, que es la tercera capa en el TiledMap.
		capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(2);
		//Cargamos la matriz de los obstáculos del mapa de baldosas.
		int anchoCapa = capaObstaculos.getWidth(), altoCapa = capaObstaculos.getHeight();
		obstaculo = new boolean[anchoCapa][altoCapa];
		for (int x = 0; x < anchoCapa; x++) {
			for (int y = 0; y < altoCapa; y++) {
				obstaculo[x][y] = (capaObstaculos.getCell(x, y) != null);
			}
		}
		//********************Cargar premios********************************/
		//Cargamos la capa de los premios, que es la cuarta capa en el TiledMap.
		capaPremios = (TiledMapTileLayer) mapa.getLayers().get(3);
		//Cargamos la matriz de los premios del mapa de baldosas
		int anchoCapaP = capaPremios.getWidth(), altoCapaP = capaPremios.getHeight();
		premio = new boolean[anchoCapaP][altoCapaP];
		for (int x = 0; x < anchoCapaP; x++) {
			for (int y = 0; y < altoCapaP; y++) {
				premio[x][y] = (capaPremios.getCell(x, y) != null);
				/*if(premio[x][y]){
					System.out.println("x:"+x+" , "+"y:"+y);
				}*/

			}
		}

		//Creamos una instancia de la clase MyInputProcessor
		mip = new MyInputProcessor(camara, mapa, img);

		//Instancias de los NPC
		npc1 = new NPC(imgNPC1, 255, 390, 255, 230); //esqueleto entrada
		npc2 = new NPC(imgNPC2, 350, 350, 650, 350); //gargola arriba
		npc3 = new NPC(imgNPC3, 450, 150, 450, 370); //muerte
		npc4 = new NPC(imgNPC4, 160, 60, 160, 220); //fantasma
		npc5 = new NPC(imgNPC2, 300, 60, 550, 60); //gargola abajo
		npc6 = new NPC(imgNPC2, 250, 250, 500, 0); //gargola en diagonal
		npc7 = new NPC(imgNPC5, 610, 40, 610, 180); //mago
		npc8 = new NPC(imgNPC6, 540, 300, 740, 300); //ogro

		//Inicializamos la música de fondo del juego y la reproducimos.
		musica = Gdx.audio.newMusic(Gdx.files.internal("dungeon.mp3"));
		//Damos al volumen un valor del 25%
		musica.setVolume(.25f);
		musica.play();
		musica.setLooping(true);

		//Inicializamos los atributos de los efectos de sonido.
		sonidoColisionEnemigo = Gdx.audio.newSound(Gdx.files.internal("game-over.wav"));
		sonidoPasos = Gdx.audio.newSound(Gdx.files.internal("movimiento.ogg"));
		sonidoObstaculo = Gdx.audio.newSound(Gdx.files.internal("wall.wav"));
		sonidoPremio = Gdx.audio.newSound(Gdx.files.internal("success.wav"));

		//Comenzamos el juego en el menú
		gameState = GameState.MENU;
	}


	@Override
	public void render () {

		//Ponemos el color de fondo a negro
		Gdx.gl.glClearColor(0, 0, 0, 1);
		//Borramos el buffer de la tarjeta de video
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		switch(gameState) {
			case MENU:
				//Creamos un escenario
				stage = new Stage();
				//Instanciamos una skin con el archivo json, a través del cual el sistema cargará todos los archivos necesarios
                skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

				//Instancio imagen, botón y texto
				Texture titulo = new Texture("titulo_juego.png");
                Image titulo_i = new Image(titulo);
				TextButton play = new TextButton("JUGAR", skin);
				Label autor = new Label("Juego realizado como practica por David Jimenez Riscardo para la asignatura de PMDM. 16/05/2020", skin);

				//Evento al hacer clic en el botón 'Jugar'
				play.addListener(new InputListener() {
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						gameState = GameState.PLAY;
						return false;
					}
				});

				//Establezco propiedades a la imagen, botón y texto
				titulo_i.setPosition(Gdx.graphics.getWidth()/2 - titulo.getWidth()/2, ((Gdx.graphics.getHeight() / 4)*3)-titulo.getHeight() );
				play.setSize(200, 100);
				play.setPosition(Gdx.graphics.getWidth()/2 - play.getWidth()/2, ((Gdx.graphics.getHeight()/4)*2)-play.getHeight() );
				play.setColor(0.573f, 0.122f, 0.122f, 1);
				//autor.setAlignment(Align.center); No sé por qué no se centra así, debería. Por lo que utilicé setPosition
				autor.setPosition(Gdx.graphics.getWidth()/2 - autor.getWidth()/2, ((Gdx.graphics.getHeight()/4)*1)-autor.getHeight());

				//Agregamos actores al escenario
				stage.addActor(autor);
				stage.addActor(play);
				stage.addActor(titulo_i);

				//Pintamos el menú
				stage.act();
				stage.draw();
				//La entrada debe de ser redirigida al stage
				Gdx.input.setInputProcessor(stage);
				break;

			case GAME_OVER:
				//Creamos un escenario
				stage = new Stage();
				//Instanciamos una skin con el archivo json, a través del cual el sistema cargará todos los archivos necesarios
				skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

				//Instancio imagen y botón
				Texture gameover = new Texture("game_over.png");
				Image gameover_i = new Image(gameover);
				TextButton menu = new TextButton("VOLVER AL MENU", skin);

				//Evento al hacer clic en el botón 'Volver al menú'
				menu.addListener(new InputListener() {
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						gameState = GameState.MENU;
						reset_game();
						return false;
					}
				});

				//Establezco propiedades a la imagen y botón
				gameover_i.setPosition(Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, ((Gdx.graphics.getHeight() / 4)*3)-gameover.getHeight() );
				menu.setSize(200, 100);
				menu.setPosition(Gdx.graphics.getWidth()/2 - menu.getWidth()/2, ((Gdx.graphics.getHeight()/4)*2)-menu.getHeight() );
				menu.setColor(0.573f, 0.122f, 0.122f, 1);

				//Agregamos actores al escenario
				stage.addActor(menu);
				stage.addActor(gameover_i);

				//Pintamos el menú
				stage.act();
				stage.draw();
				//La entrada debe de ser redirigida al stage
				Gdx.input.setInputProcessor(stage);
				break;

			case WIN:
				musica.stop();
				//Creamos un escenario
				stage = new Stage();
				//Instanciamos una skin con el archivo json, a través del cual el sistema cargará todos los archivos necesarios
				skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

				//Instancio imagen y botón
				Texture victory = new Texture("victory.png");
				Image victory_i = new Image(victory);
				TextButton menu2 = new TextButton("VOLVER AL MENU", skin);

				//Evento al hacer clic en el botón 'Volver al menú'
				menu2.addListener(new InputListener() {
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						gameState = GameState.MENU;
						reset_game();
						return false;
					}
				});

				//Establezco propiedades a la imagen y botón
				victory_i.setPosition(Gdx.graphics.getWidth()/2 - victory.getWidth()/2, ((Gdx.graphics.getHeight() / 4)*3)-victory.getHeight() );
				menu2.setSize(200, 100);
				menu2.setPosition( Gdx.graphics.getWidth()/2 - menu2.getWidth()/2, ((Gdx.graphics.getHeight()/4)*2)-menu2.getHeight() );
				menu2.setColor(0.573f, 0.122f, 0.122f, 1);

				//Agregamos actores al escenario
				stage.addActor(menu2);
				stage.addActor(victory_i);

				//Pintamos el menú
				stage.act();
				stage.draw();
				//La entrada debe de ser redirigida al stage
				Gdx.input.setInputProcessor(stage);
				break;

			case PLAY:
				//La entrada debe de ser redirigida al objeto de mi personaje
				Gdx.input.setInputProcessor(mip);
				//Trasladamos la cámara para que se centre en el personaje.
				camara.position.set(jugadorX,jugadorY,0f);
				//Comprobamos que la cámara no se salga de los límites del mapa de baldosas,
				//Verificamos, con el método clamp(), que el valor de la posición x de la cámara
				//esté entre la mitad de la anchura de la vista de la cámara y entre la diferencia entre
				//la anchura del mapa restando la mitad de la anchura de la vista de la cámara,
				camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth / 2f,
						mapaAncho - camara.viewportWidth / 2f);
				//Verificamos, con el método clamp(), que el valor de la posición y de la cámara
				//esté entre la mitad de la altura de la vista de la cámara y entre la diferencia entre
				//la altura del mapa restando la mitad de la altura de la vista de la cámara,
				camara.position.y = MathUtils.clamp(camara.position.y, camara.viewportHeight / 2f,
						mapaAlto - camara.viewportHeight / 2f);

				//Actualizamos la cámara del juego
				camara.update();
				//Vinculamos el objeto de dibuja el TiledMap con la cámara del juego
				mapaRenderer.setView(camara);
				//Dibujamos las cuatro primeras capas del TiledMap (suelo, decoración, obstáculos y premios)
				int[] capas = {0,1,2,3};
				mapaRenderer.render(capas);

				// extraemos el tiempo de la última actualización del sprite del personaje y la acumulamos a stateTime.
				mip.stateTime +=  Gdx.graphics.getDeltaTime();
				//Extraermos el frame que debe ir asociado a al momento actual.
				jugador = mip.getJugador();
				cuadroActual = (TextureRegion) jugador.getKeyFrame(mip.stateTime); // 1

				//De igual forma que hemos hecho con el caballero hacemos con todos los NPC
				npc1.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc1.getNoJugador();
				cuadroActualNPC = (TextureRegion) noJugador.getKeyFrame(npc1.stateTimeNPC);

				npc2.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc2.getNoJugador();
				cuadroActualNPC2 = (TextureRegion) noJugador.getKeyFrame(npc2.stateTimeNPC);

				npc3.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc3.getNoJugador();
				cuadroActualNPC3 = (TextureRegion) noJugador.getKeyFrame(npc3.stateTimeNPC);

				npc4.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc4.getNoJugador();
				cuadroActualNPC4 = (TextureRegion) noJugador.getKeyFrame(npc4.stateTimeNPC);

				npc5.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc5.getNoJugador();
				cuadroActualNPC5 = (TextureRegion) noJugador.getKeyFrame(npc5.stateTimeNPC);

				npc6.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc6.getNoJugador();
				cuadroActualNPC6 = (TextureRegion) noJugador.getKeyFrame(npc6.stateTimeNPC);

				npc7.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc7.getNoJugador();
				cuadroActualNPC7 = (TextureRegion) noJugador.getKeyFrame(npc7.stateTimeNPC);

				npc8.stateTimeNPC +=  Gdx.graphics.getDeltaTime();
				noJugador = npc8.getNoJugador();
				cuadroActualNPC8 = (TextureRegion) noJugador.getKeyFrame(npc8.stateTimeNPC);

				// le indicamos al SpriteBatch que se muestre en el sistema de coordenadas
				// específicas de la cámara.
				sb.setProjectionMatrix(camara.combined);

				//Inicializamos el objeto SpriteBatch
				sb.begin();

				//Pintamos el personaje y NPCs Sprite a través del objeto SpriteBatch
				jugadorX = mip.getJugadorX();
				jugadorY = mip.getJugadorY();
				sb.draw(cuadroActual,jugadorX,jugadorY); // 2

				noJugadorX = npc1.getNoJugadorX();
				noJugadorY = npc1.getNoJugadorY();
				npc1.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC,noJugadorX,noJugadorY);

				noJugadorX2 = npc2.getNoJugadorX();
				noJugadorY2 = npc2.getNoJugadorY();
				npc2.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC2,noJugadorX2,noJugadorY2);

				noJugadorX3 = npc3.getNoJugadorX();
				noJugadorY3 = npc3.getNoJugadorY();
				npc3.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC3,noJugadorX3,noJugadorY3);

				noJugadorX4 = npc4.getNoJugadorX();
				noJugadorY4 = npc4.getNoJugadorY();
				npc4.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC4,noJugadorX4,noJugadorY4);

				noJugadorX5 = npc5.getNoJugadorX();
				noJugadorY5 = npc5.getNoJugadorY();
				npc5.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC5,noJugadorX5,noJugadorY5);

				noJugadorX6 = npc6.getNoJugadorX();
				noJugadorY6 = npc6.getNoJugadorY();
				npc6.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC6,noJugadorX6,noJugadorY6);

				noJugadorX7= npc7.getNoJugadorX();
				noJugadorY7 = npc7.getNoJugadorY();
				npc7.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC7,noJugadorX7,noJugadorY7);

				noJugadorX8= npc8.getNoJugadorX();
				noJugadorY8 = npc8.getNoJugadorY();
				npc8.actualizaNPC(0.5f);
				sb.draw(cuadroActualNPC8,noJugadorX8,noJugadorY8);

				//Finalizamos el objeto SpriteBatch
				sb.end();
				//Pintamos la quinta capa del mapa de baldosas (profundidad)
				capas = new int[1];
				capas[0] = 4;
				mapaRenderer.render(capas);

				detectaColisionesNPC();
				break;
		}

	}

	/**
	 * Método para detectar las colisiones de nuestro personaje con los NPCs
	 */
	private void detectaColisionesNPC() {
		//Vamos a comprobar que el rectángulo que rodea al jugador, no se solape
		//con el rectángulo de alguno de los NPC. Primero calculamos el rectángulo
		//en torno al jugador.
		Rectangle rJugador = new Rectangle(jugadorX,jugadorY,mip.getAnchoJugador(),mip.getAltoJugador());
		Rectangle rNPC1, rNPC2, rNPC3, rNPC4, rNPC5, rNPC6, rNPC7, rNPC8;

		//Cálculo de los rectángulos de los NPC
		rNPC1 = new Rectangle(noJugadorX, noJugadorY, npc1.getAnchoNoJugador(), npc1.getAltoNoJugador());
		rNPC2 = new Rectangle(noJugadorX2, noJugadorY2, npc2.getAnchoNoJugador(), npc2.getAltoNoJugador());
		rNPC3 = new Rectangle(noJugadorX3, noJugadorY3, npc3.getAnchoNoJugador(), npc3.getAltoNoJugador());
		rNPC4 = new Rectangle(noJugadorX4, noJugadorY4, npc4.getAnchoNoJugador(), npc4.getAltoNoJugador());
		rNPC5 = new Rectangle(noJugadorX5, noJugadorY5, npc5.getAnchoNoJugador(), npc5.getAltoNoJugador());
		rNPC6 = new Rectangle(noJugadorX6, noJugadorY6, npc6.getAnchoNoJugador(), npc6.getAltoNoJugador());
		rNPC7 = new Rectangle(noJugadorX7, noJugadorY7, npc7.getAnchoNoJugador(), npc7.getAltoNoJugador());
		rNPC8 = new Rectangle(noJugadorX8, noJugadorY8, npc8.getAnchoNoJugador(), npc8.getAltoNoJugador());

		//Se comprueba si se solapan.
		if (rJugador.overlaps(rNPC1)  || rJugador.overlaps(rNPC2) || rJugador.overlaps(rNPC3) || rJugador.overlaps(rNPC4)  || rJugador.overlaps(rNPC5) ||
				rJugador.overlaps(rNPC6) || rJugador.overlaps(rNPC7) || rJugador.overlaps(rNPC8) ){

			//System.out.println("Hay colisión!!!");
			//Debemos de pasar al estado Game Over cuando se detecte una colisión, previamente paro la música del juego para que pueda apreciarse el sonido de la colisión con el NPC
			musica.stop();
			sonidoColisionEnemigo.play(0.25f);
			gameState = GameState.GAME_OVER;
		}

	}
	
	@Override
	public void dispose () {
		mapa.dispose();
		img.dispose();
		imgNPC1.dispose();
		imgNPC2.dispose();
		imgNPC3.dispose();
		imgNPC4.dispose();
		imgNPC5.dispose();
		imgNPC6.dispose();
		sb.dispose();
		musica.dispose();
		sonidoObstaculo.dispose();
		sonidoPasos.dispose();
		sonidoColisionEnemigo.dispose();
	}
}
