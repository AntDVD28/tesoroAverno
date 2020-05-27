package com.averno.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;

import static com.averno.game.MyGdxGame.sonidoObstaculo;
import static com.averno.game.MyGdxGame.sonidoPasos;
import static com.averno.game.MyGdxGame.sonidoPremio;

/**
 * Clase que utilizaremos para instanciar nuestro personaje y gestionar sus movimientos, además de detectar colisiones con obstáculos y premios.
 */
public class MyInputProcessor implements com.badlogic.gdx.InputProcessor{

    private OrthographicCamera camara;
    private TiledMap mapa;
    private Texture img;
    //Constantes que indican el número de filas y columnas de la hoja de sprites.
    private static final int FRAME_COLS = 3;
    private static final int FRAME_ROWS = 4;
    //Animación que se muestra en el método render()
    private Animation jugador;
    //Animaciones para cada una de las direcciones de movimiento del personaje del jugador.
    private Animation jugadorArriba;
    private Animation jugadorDerecha;
    private Animation jugadorAbajo;
    private Animation jugadorIzquierda;
    //Posición en el eje de coordenadas actual del jugador.
    private float jugadorX, jugadorY;
    // Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación,
    // servirá para determinar cual es el frame que se debe representar.
    protected static float stateTime;
    //Atributos que indican la anchura y altura del sprite animado del jugador.
    private int anchoJugador, altoJugador;

    /**
     * Método Constructor
     * @OrthographicCamera camara Camara
     * @TiledMap mapa Mapa de baldosas
     * @Texture img Imagen
     */
    public MyInputProcessor(OrthographicCamera camara, TiledMap mapa, Texture img){

        this.camara = camara;
        this.mapa = mapa;
        this.img = img;
        //Sacamos los frames de img en un array de TextureRegion.
        TextureRegion[][] tmp = TextureRegion.split(this.img, this.img.getWidth() / FRAME_COLS, this.img.getHeight() / FRAME_ROWS);
        // Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
        // será de 150 milisegundos, y que les pasamos las distintas filas de la matriz tmp a las mismas
        jugadorAbajo = new Animation(0.150f, tmp[0]);
        jugadorIzquierda = new Animation(0.150f, tmp[1]);
        jugadorDerecha = new Animation(0.150f, tmp[2]);
        jugadorArriba = new Animation(0.150f, tmp[3]);
        //En principio se utiliza la animación del jugador arriba como animación por defecto.
        jugador = jugadorDerecha;
        // Posición inicial del jugador.
        jugadorX = 80;
        jugadorY = 330;
        //Ponemos a cero el atributo stateTime, que marca el tiempo e ejecución de la animación.
        stateTime = 0f;
        //Cargamos en los atributos del ancho y alto del sprite sus valores
        anchoJugador = tmp[0][0].getRegionWidth();
        altoJugador = tmp[0][0].getRegionHeight();
    }


    /**
     * Métodos getters para devolver diferentes propiedades de nuestro jugador, que necesitaremos en la clase principal del juego
     */
    public Animation getJugador() {

        return this.jugador;
    }

    public float getJugadorX() {

        return this.jugadorX;
    }

    public float getJugadorY() {

        return this.jugadorY;
    }

    public int getAnchoJugador() {

        return this.anchoJugador;
    }

    public int getAltoJugador() {

        return this.altoJugador;
    }



    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        //Si pulsamos uno de los cursores, se desplaza el sprite
        //de forma adecuada un pixel, y se pone a cero el
        //atributo que marca el tiempo de ejecución de la animación,
        //provocando que la misma se reinicie.

        //Guardamos la posición anterior del jugador por si al desplazarlo se topa
        //con un obstáculo y podamos volverlo a la posición anterior.
        float jugadorAnteriorX = jugadorX;
        float jugadorAnteriorY = jugadorY;

        stateTime = 0;

        if (keycode == Input.Keys.LEFT) {
            jugadorX += -5;
            jugador = jugadorIzquierda;

        }
        if (keycode == Input.Keys.RIGHT) {
            jugadorX += 5;
            jugador = jugadorDerecha;
        }
        if (keycode == Input.Keys.UP) {
            jugadorY += 5;
            jugador = jugadorArriba;
        }
        if (keycode == Input.Keys.DOWN) {
            jugadorY += -5;
            jugador = jugadorAbajo;
        }

        //Si pulsamos la tecla del número 1, se alterna la visibilidad de la primera capa
        //del mapa de baldosas.
        if (keycode == Input.Keys.NUM_1)
            mapa.getLayers().get(0).setVisible(!mapa.getLayers().get(0).isVisible());
        //Si pulsamos la tecla del número 2, se alterna la visibilidad de la segunda capa
        //del mapa de baldosas.
        if (keycode == Input.Keys.NUM_2)
            mapa.getLayers().get(1).setVisible(!mapa.getLayers().get(1).isVisible());

        //Detectamos las colisiones con los obstáculos del mapa y si el jugador se sale del mismo.

        if ((jugadorX < 0 || jugadorY < 0 ||
                jugadorX > (MyGdxGame.mapaAncho - anchoJugador) ||
                jugadorY > (MyGdxGame.mapaAlto - altoJugador)) ||
                ((MyGdxGame.obstaculo[(int) ((jugadorX + anchoJugador / 4) / MyGdxGame.anchoCelda)][((int) (jugadorY) / MyGdxGame.altoCelda)]) ||
                        (MyGdxGame.obstaculo[(int) ((jugadorX + 3 * anchoJugador / 4) / MyGdxGame.anchoCelda)][((int) (jugadorY) / MyGdxGame.altoCelda)]))) {
            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;
            sonidoObstaculo.play(0.5f);
        }else {

            sonidoPasos.play(0.25f);
        }

        //Detectamos colisiones con los premios del mapa
        detectaPremio();

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Vector en tres dimensiones que recoge las coordenadas donde se ha hecho click
        // o toque de la pantalla.
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        // Transformamos las coordenadas del vector a coordenadas de nuestra cámara.
        Vector3 posicion = camara.unproject(clickCoordinates);

        //Se pone a cero el atributo que marca el tiempo de ejecución de la animación,
        //provocando que la misma se reinicie.
        stateTime = 0;

        //Guardamos la posición anterior del jugador por si al desplazarlo se topa
        //con un obstáculo y podamos volverlo a la posición anterior.
        float jugadorAnteriorX = jugadorX;
        float jugadorAnteriorY = jugadorY;
        //Si se ha pulsado por encima de la animación, se sube esta 5 píxeles y se reproduce la
        //animación del jugador desplazándose hacia arriba.
        if ((jugadorY + altoJugador) < posicion.y) {
            jugadorY += 5;
            jugador = jugadorArriba;
            //Si se ha pulsado por debajo de la animación, se baja esta 5 píxeles y se reproduce
            //la animación del jugador desplazándose hacia abajo.
        } else if ((jugadorY) > posicion.y) {
            jugadorY -= 5;
            jugador = jugadorAbajo;
        }
        //Si se ha pulsado mas de la mitad del ancho del sprite a la derecha de la animación, se
        //mueve esta 5 píxeles a la derecha se reproduce la animación del jugador desplazándose
        // hacia la derecha.
        if ((jugadorX + anchoJugador/2) < posicion.x) {
            jugadorX += 5;
            jugador = jugadorDerecha;
            //Si se ha pulsado mas de la mitad del ancho del sprite a la izquierda de la animación,
            // se mueve esta 5 píxeles a la izquierda y se reproduce la animación del jugador
            // desplazándose hacia la izquierda.
        } else if ((jugadorX - anchoJugador/2) > posicion.x) {
            jugadorX -= 5;
            jugador = jugadorIzquierda;
        }

        //Detectamos las colisiones con los obstáculos del mapa y si el jugador se sale del mismo.
        if ((jugadorX < 0 || jugadorY < 0 ||
                jugadorX > (MyGdxGame.mapaAncho - anchoJugador) ||
                jugadorY > (MyGdxGame.mapaAlto - altoJugador)) ||
                ((MyGdxGame.obstaculo[(int) ((jugadorX + anchoJugador / 4) / MyGdxGame.anchoCelda)][((int) (jugadorY) / MyGdxGame.altoCelda)]) ||
                        (MyGdxGame.obstaculo[(int) ((jugadorX + 3 * anchoJugador / 4) / MyGdxGame.anchoCelda)][((int) (jugadorY) / MyGdxGame.altoCelda)]))) {
            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;
            sonidoObstaculo.play(0.5f);
        }else {
            sonidoPasos.play(0.25f);
        }

        //Detectamos colisiones con los premios del mapa
        detectaPremio();

        return true;
    }



    /**
     * Detectamos las colisiones con los premios, ocultando los mismos
     * También detectamos la llegada a la meta
     */
    private void detectaPremio(){

        int x1 = (int) ((jugadorX + anchoJugador / 4) / MyGdxGame.anchoCelda);
        int y1 = (int) ((jugadorY) / MyGdxGame.altoCelda);

        int x2 = (int) ((jugadorX + 3 * anchoJugador / 4) / MyGdxGame.anchoCelda);
        int y2 = (int) ((jugadorY) / MyGdxGame.altoCelda);

        if(MyGdxGame.premio[x1][y1]){

            TiledMapTileLayer.Cell  celda=MyGdxGame.capaPremios.getCell(x1,y1);
            if(celda.getTile() != null){
                celda.setTile(null);
                sonidoPremio.play(0.25f);
            }
        }
        if(MyGdxGame.premio[x2][y2]){

            TiledMapTileLayer.Cell  celda=MyGdxGame.capaPremios.getCell(x2,y2);
            if(celda.getTile() != null){
                celda.setTile(null);
                sonidoPremio.play(0.25f);
            }
        }
        //Llegada a la meta
        if(x1==20 && y1==13){
            //System.out.println("Has ganado!!");
            sonidoPremio.play(0.25f);
            MyGdxGame.gameState = MyGdxGame.GameState.WIN;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
