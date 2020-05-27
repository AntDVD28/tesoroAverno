package com.averno.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Clase que utilizaremos para instanciar los NPCs y gestionar sus movimientos
 */
public class NPC {

    private Texture imgNPC;
    //Constantes que indican el número de filas y columnas de la hoja de sprites.
    private static final int FRAME_COLS = 3;
    private static final int FRAME_ROWS = 4;
    //Animación que se muestra en el método render()
    private Animation noJugador;
    //Animaciones posicionales relacionadas con los NPC del juego
    private Animation noJugadorArriba;
    private Animation noJugadorDerecha;
    private Animation noJugadorAbajo;
    private Animation noJugadorIzquierda;
    //Posición en el eje de coordenadas actual del NPC.
    private float noJugadorX, noJugadorY;
    // Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación
    //de los NPC , servirá para determinar cual es el frame que se debe representar.
    protected static float stateTimeNPC;
    //Atributos que indican la anchura y altura del sprite animado de los NPC.
    int anchoNoJugador, altoNoJugador;

    //Posición inicial X de cada uno de los NPC
    private float origenX;
    //Posición inicial Y de cada uno de los NPC
    private float origenY;

    //Posición final X de cada uno de los NPC
    private float destinoX, destinoX_copy;
    //Posición final Y de cada uno de los NPC
    private float destinoY, destinoY_copy;



    /**
     * Método Constructor. Recibimos como valores de entradas la posición origen y destino.
     * Hacemos una copia de los mismos para poder cambiar el sentido del movimiento del NPC
     * El origen pasaría a ser el destino y el destino pasaría a ser el origen. Esto lo relizamos en el método actualizaNPC
     * @Texture imgNPC Imagen del NPC
     * @float origenX Coordenada en X, punto de origen
     * @float origenY Coordenada en Y, punto de origen
     * @float destinoX Coordenada en X, punto de destino
     * @float destinoY Coordenada en Y, punto de destino
     */
    public NPC(Texture imgNPC, float origenX, float origenY, float destinoX, float destinoY){

        this.imgNPC = imgNPC;
        //en origenX y origenY guardaremos copia del punto de origen
        this.origenX = origenX;
        this.origenY = origenY;
        //noJugadorX, noJugadorY representan la posición ACTUAL del NPC, en un principio coincidiran con la posición de ORIGEN
        this.noJugadorX =origenX;
        this.noJugadorY = origenY;
        this.destinoX = destinoX;
        this.destinoY = destinoY;
        //Una vez el NPC llegue a su destino debemos de cambiar el sentido de su movimiento, el origen será el destino y el destino pasará a ser el origen
        //guardamos copia del punto de destino
        this.destinoX_copy = destinoX;
        this.destinoY_copy = destinoY;

        //Sacamos los frames de img en un array de TextureRegion.
        TextureRegion[][] tmp = TextureRegion.split(this.imgNPC, this.imgNPC.getWidth() / FRAME_COLS, this.imgNPC.getHeight() / FRAME_ROWS);
        // Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
        // será de 150 milisegundos.
        noJugadorAbajo = new Animation(0.150f, tmp[0]);
        noJugadorAbajo.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorIzquierda = new Animation(0.150f, tmp[1]);
        noJugadorIzquierda.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorDerecha = new Animation(0.150f, tmp[2]);
        noJugadorDerecha.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorArriba = new Animation(0.150f, tmp[3]);
        noJugadorArriba.setPlayMode(Animation.PlayMode.LOOP);
        //En principio se utiliza la animación del jugador arriba como animación por defecto.
        noJugador = noJugadorArriba;
        //Ponemos a cero el atributo stateTimeNPC, que marca el tiempo e ejecución de la animación
        // de los NPC.
        stateTimeNPC = 0f;
        //Cargamos en los atributos del ancho y alto del sprite del mostruo sus valores
        anchoNoJugador = tmp[0][0].getRegionWidth();
        altoNoJugador = tmp[0][0].getRegionHeight();

    }

    /**
     * Métodos getters para devolver diferentes propiedades de los NPCs, que necesitaremos en la clase principal del juego
     */
    public Animation getNoJugador(){
        return this.noJugador;
    }

    public float getNoJugadorX(){
        return this.noJugadorX;
    }

    public float getNoJugadorY(){
        return this.noJugadorY;
    }

    public int getAnchoNoJugador() {

        return this.anchoNoJugador;
    }

    public int getAltoNoJugador() {

        return this.altoNoJugador;
    }


    /**
     * Método que permite cambiar las coordenadas del NPC en la posición "i"
     * Este método será también válido para movimientos diagonales del NPC siempre que los puntos en X de origen y destino sean divisibles entre sí mismos,
     * así como los puntos en Y de origen y destino también lo sean
     * Ejemplo: si el NPC se mueve de la coordenada (250,250) a la (500,0) sería válido porque 500/250 y 0/250 son divisiones exactas
     * @float delta Variación "delta" en ambas coordenadas
     */
    protected void actualizaNPC(float delta) {
        //Movimiento hacia arriba
        if (destinoY>noJugadorY) {
            noJugadorY += delta;
            noJugador = noJugadorArriba;
        }
        //Movimiento hacia abajo
        if (destinoY<noJugadorY) {
            noJugadorY -= delta;
            noJugador = noJugadorAbajo;
        }
        //Si el NPC llega a su destino y viene desde abajo: cambio de sentido hacia abajo
        if(destinoY==noJugadorY && noJugador==noJugadorArriba){
            if(destinoY_copy > origenY){
                destinoY = origenY;
            }else {
                destinoY = destinoY_copy;
            }
            noJugadorY -= delta;
            noJugador = noJugadorAbajo;
        }
        //Si el NPC llega a su destino y viene desde arriba: cambio de sentido hacia arriba
        if(destinoY==noJugadorY && noJugador==noJugadorAbajo){
            if(destinoY_copy > origenY){
                destinoY = destinoY_copy;
            }else {
                destinoY = origenY;
            }
            noJugadorY += delta;
            noJugador = noJugadorArriba;
        }

        //Movimiento hacia la derecha
        if (destinoX>noJugadorX) {
            noJugadorX += delta;
            noJugador = noJugadorDerecha;
        }
        //Movimiento hacia la izquierda
        if (destinoX<noJugadorX) {
            noJugadorX -= delta;
            noJugador = noJugadorIzquierda;
        }
        //Si el NPC llega a su destino y viene de la derecha: cambio de sentido a la izquierda
        if(destinoX==noJugadorX && noJugador==noJugadorDerecha) {
            if(destinoX_copy > origenX){
                destinoX =  origenX;
            }else {
                destinoX = destinoX_copy;
            }
            noJugadorX -= delta;
            noJugador = noJugadorIzquierda;

        }
        //Si el NPC llega a su destino y viene de la izquierda: cambio de sentido a la derecha
        if(destinoX==noJugadorX && noJugador==noJugadorIzquierda){
            if(destinoX_copy > origenX){
                destinoX = destinoX_copy;
            }else {
                destinoX = origenX;
            }
            noJugadorX += delta;
            noJugador = noJugadorDerecha;
        }
    }


}
