package com.gabrielkaiki.lifeatsea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class LifeAtSea extends ApplicationAdapter {
    private SpriteBatch batch;

    //Exibiçao de textos
    BitmapFont textoPontuacao;
    BitmapFont textoMelhorPontuacao;

    //Objeto salvar pontuacao
    Preferences preferencias;

    //Texturas
    private Texture toqueParaReiniciar, nomeJogo, toqueParaIniciar, bolha, gameOver;

    //Array de texturas
    private Texture[] player;
    private Texture[] peixeDragao;
    private Texture[] objetosFundo;
    private Texture[] objetosFlutuantes;
    private Texture[] bolhas;
    private Texture[] explosao;
    private Texture[] polvo;
    private Texture[] baleia;
    private Texture[] explosao2;
    private Texture[] vida;
    private Texture[] texturaCoracoesVida;
    private Texture[] fundos;
    private Texture[] redFish;
    private Texture[] blueFish;
    private Texture[] orangeFish;
    private Texture[] greenFish;
    private Texture[] frog;
    private Texture[] peixeAzul;
    private Texture[] objetosTopo;
    private Texture[] efeitoBrilho;
    private Texture[] explosao3;

    //Matriz de texturas
    Texture[][] inimigos, inimigosPequenos;

    //Valores numéricos
    private float variacao = 0, indiceObjetosFundo = 0, indiceObjetosFlutuantes = 0, indiceVida = 0, posicaoPeixeX, posicaoPeixeY, posicaoObjFundoX, posicaoObjFundoY, posicaoObjFlutuanteX, posicaoBolhaY, posicaoBolha1Y, posicaoBolha2Y, posicaoBolha3Y, posicaoObjFlutuanteY, variacaoObjetos = 0, larguraDispositivo, alturaDispositivo, faixaAcimaTopoTela, indiceExplosao2 = 0, posicaoBolhaX, posicaoInimigoX, posicaoInimigoY, posicaoObjTopoX, posicaoObjTopoY, posicaoBolha1X, posicaoBolha2X, posicaoBolha3X, variacaoObjetosTopo = 0, velocidadeInimigo = 400, velocidadeInimigoPequeno = 400, variacaoTexturasInimigo = 0, velocidadeObjetoTopo = 300, variacaoInimigoPequenoAtual, variacaoTexturasInimigoPequeno, posicaoInimigoPequenoX, posicaoInimigoPequenoY, indiceBrilho = 0, indiceExplosao3, distaciaPercorrida = 0, pontuacaoMaxima = 0;

    //Final floats
    private final float VOLUME = 0.2f, VIRTUAL_WIDTH = 1920, VIRTUAL_HEIGHT = 1080;

    //Objetos para câmera
    private OrthographicCamera camera;
    private Viewport viewport;

    private Rectangle retanguloPeixe, retanguloObjetoFundo, retanguloObjetoFlutuante, retanguloObjetoTopo, retanguloInimigoAtual, retanguloInimigoPequenoAtual;
    private Random random;

    //Booleans
    private boolean objFlutuando = false, tocandoSomInimigoPequeno = false;

    //Inteiros
    private int gravidade = 2;
    private int velocidadeObjetosFlutuantesX = 300;
    private int velocidadeObjetosFlutuantesY = 300;
    private int velocidadeObjetosFundo = 300;
    private int variacaoFundos = 0;
    private int variacaoInimigoAtual = 0;
    private int indiceObjetosTopo;
    private int estadoJogo = 0;
    private int indiceMusicas = 0;
    private int incrementoDistancia = 2;
    private Sound somPeixe;
    private Sound somChoqueBorda;
    private Sound somGanhoVida;
    private Sound somPerdaVida;
    private Sound somInimigoAtual;
    private Sound somInimigoPequenoAtual;
    private Sound somBaleia;
    private Sound somPolvo;
    private Sound somArbusto;
    private Sound somExplosao;
    private Sound somDragao;
    private Sound somFrog;

    private Music musicaTema, musicaFase1, musicaFase2, musicaFase3, musicaFase4;
    private Music[] musicas;
    private boolean colidiuPeixeGrande = true;

    private AdController adController;

    public LifeAtSea(AdController adC) {
        adController = adC;
    }

    @Override
    public void create() {
        iniciarTexturas();
        iniciarObjetos();
    }

    @Override
    public void render() {
        // Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        verificarEstadoJogo();
        desenharTexturas();
    }

    private void iniciarObjetos() {
        batch = new SpriteBatch();
        random = new Random();

        //Textos
        iniciarTextos();

        //Formas
        iniciarFormas();

        //Sons
        iniciarSons();

        //Definir posicoes
        iniciarPosicoes();

        //Configura preferências dos objetos
        preferencias = Gdx.app.getPreferences("underworld");
        pontuacaoMaxima = preferencias.getFloat("distanciaMaxima", 0);

        //Configuração da câmera
        iniciarCamera();
    }

    private void iniciarTextos() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/sixty.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.YELLOW;
        parameter.borderWidth = 3;

        textoPontuacao = generator.generateFont(parameter);
        textoMelhorPontuacao = generator.generateFont(parameter);
        generator.dispose();
    }

    private void iniciarCamera() {
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    private void iniciarPosicoes() {
        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        faixaAcimaTopoTela = 100;
        posicaoObjFundoX = larguraDispositivo;
        posicaoObjFundoY = -20;
        posicaoPeixeX = 100;
        posicaoPeixeY = alturaDispositivo;
        posicaoBolhaY = -bolha.getHeight();
        posicaoBolha1Y = -bolha.getHeight();
        posicaoBolha2Y = -bolha.getHeight();
        posicaoBolha3Y = -bolha.getHeight();
        posicaoBolhaX = random.nextInt((int) larguraDispositivo);
        posicaoBolha1X = random.nextInt((int) larguraDispositivo);
        posicaoBolha2X = random.nextInt((int) larguraDispositivo);
        posicaoBolha3X = random.nextInt((int) larguraDispositivo);
        posicaoInimigoX = larguraDispositivo;
        posicaoInimigoY = random.nextInt((int) alturaDispositivo);
        posicaoObjTopoY = alturaDispositivo - objetosTopo[0].getHeight();
        posicaoObjTopoX = larguraDispositivo;
        posicaoInimigoPequenoX = larguraDispositivo;
        posicaoInimigoPequenoY = random.nextInt((int) alturaDispositivo);
    }

    private void iniciarFormas() {
        retanguloPeixe = new Rectangle();
        retanguloObjetoFundo = new Rectangle();
        retanguloObjetoTopo = new Rectangle();
        retanguloInimigoAtual = new Rectangle();
        retanguloInimigoPequenoAtual = new Rectangle();
        retanguloObjetoFlutuante = new Rectangle();
    }

    private void iniciarSons() {

        somPeixe = obterSound("sons/peixe.mp3");
        somChoqueBorda = obterSound("sons/choque_borda.mp3");
        somGanhoVida = obterSound("sons/ganho_vida.mp3");
        somPerdaVida = obterSound("sons/perda_vida.mp3");
        somArbusto = obterSound("sons/arbusto.mp3");
        somBaleia = obterSound("sons/baleia.mp3");
        somPolvo = obterSound("sons/polvo.mp3");
        somExplosao = obterSound("sons/explosao_bomba.mp3");
        somDragao = obterSound("sons/dragon.mp3");
        somFrog = obterSound("sons/frog.mp3");

        musicaTema = obterMusic("sons/music_tema.mp3");
        musicaFase1 = obterMusic("sons/musica_fundo_fase1.mp3");
        musicaFase2 = obterMusic("sons/musica_fundo_fase2.mp3");
        musicaFase3 = obterMusic("sons/musica_fundo_fase3.mp3");
        musicaFase4 = obterMusic("sons/musica_fundo_fase4.mp3");

        musicas = new Music[4];
        musicas[0] = musicaFase1;
        musicas[1] = musicaFase2;
        musicas[2] = musicaFase3;
        musicas[3] = musicaFase4;

        somInimigoAtual = somPolvo;
    }

    private Music obterMusic(String caminhoMusica) {
        return Gdx.audio.newMusic(Gdx.files.internal(caminhoMusica));
    }

    private Sound obterSound(String caminhoSom) {
        return Gdx.audio.newSound(Gdx.files.internal(caminhoSom));
    }

    private void iniciarTexturas() {
        fundos = new Texture[6];
        fundos[0] = new Texture("backgrounds/background_1.png");
        fundos[1] = new Texture("backgrounds/background_2.png");
        fundos[2] = new Texture("backgrounds/background_3.png");
        fundos[3] = new Texture("backgrounds/background_4.png");
        fundos[4] = new Texture("backgrounds/background_tela_inicial.jpg");
        fundos[5] = new Texture("backgrounds/background_tela_gameover.jpg");

        nomeJogo = new Texture("textos/nomeJogo.png");
        toqueParaIniciar = new Texture("textos/toqueParaIniciar.png");
        gameOver = new Texture("textos/game_over.png");
        toqueParaReiniciar = new Texture("textos/tap_to_restart.png");
        bolha = new Texture("bolha_transparente.png");

        player = new Texture[13];
        popularArrayTexturas0("player", player.length, "player/fish_player_");

        objetosFundo = new Texture[17];
        objetosFundo[0] = new Texture("objetosFundo/Anchor.png");
        objetosFundo[1] = new Texture("objetosFundo/Barrel_1.png");
        objetosFundo[2] = new Texture("objetosFundo/Barrel_2.png");
        objetosFundo[3] = new Texture("objetosFundo/Bomb.png");
        objetosFundo[4] = new Texture("objetosFundo/Mast.png");
        objetosFundo[5] = new Texture("objetosFundo/Seaweed_1.png");
        objetosFundo[6] = new Texture("objetosFundo/Seaweed_2.png");
        objetosFundo[7] = new Texture("objetosFundo/Steering_wheel.png");
        objetosFundo[8] = new Texture("objetosFundo/Stone_1.png");
        objetosFundo[9] = new Texture("objetosFundo/Stone_2.png");
        objetosFundo[10] = new Texture("objetosFundo/Stone_3.png");
        objetosFundo[11] = new Texture("objetosFundo/Stone_5.png");
        objetosFundo[12] = new Texture("objetosFundo/Stone_6.png");
        objetosFundo[13] = new Texture("objetosFundo/bau_fechado.png");
        objetosFundo[14] = new Texture("objetosFundo/bau_pouco_aberto.png");
        objetosFundo[15] = new Texture("objetosFundo/bau_aberto.png");
        objetosFundo[16] = new Texture("objetosFlutuantes/transparente.png");

        objetosTopo = new Texture[2];
        objetosTopo[0] = new Texture("objetosTopo/rede.png");
        objetosTopo[1] = new Texture("objetosTopo/pedra_topo.png");

        objetosFlutuantes = new Texture[12];
        objetosFlutuantes[0] = new Texture("objetosFlutuantes/shield.png");
        objetosFlutuantes[1] = new Texture("objetosFlutuantes/acceleration.png");
        objetosFlutuantes[2] = new Texture("objetosFlutuantes/coin.png");
        objetosFlutuantes[3] = new Texture("objetosFlutuantes/crown.png");
        objetosFlutuantes[4] = new Texture("objetosFlutuantes/heart.png");
        objetosFlutuantes[5] = new Texture("objetosFlutuantes/magnet.png");
        objetosFlutuantes[6] = new Texture("objetosFlutuantes/pearl.png");
        objetosFlutuantes[7] = new Texture("objetosFlutuantes/small_bomb.png");
        objetosFlutuantes[8] = new Texture("objetosFlutuantes/transparente.png");
        objetosFlutuantes[9] = new Texture("objetosFlutuantes/Bubble_1.png");
        objetosFlutuantes[10] = new Texture("objetosFlutuantes/Bubble_2.png");
        objetosFlutuantes[11] = new Texture("objetosFlutuantes/Bubble_3.png");

        bolhas = new Texture[9];
        popularArrayTexturas0("bolhas", bolhas.length, "bolhas/bolhas_");

        explosao = new Texture[8];
        popularArrayTexturas1("explosao", explosao.length, "explosao/Explosion_");

        efeitoBrilho = new Texture[10];
        popularArrayTexturas1("efeitoBrilho", efeitoBrilho.length, "efeitoBrilho/brilho_");

        explosao2 = new Texture[10];
        popularArrayTexturas1("explosao2", explosao2.length, "explosao2/Explosion_");

        explosao3 = new Texture[10];
        popularArrayTexturas1("explosao3", explosao3.length, "explosao3/Explosion_");

        vida = new Texture[13];
        vida[0] = new Texture("vida/vida_0.png");
        vida[1] = new Texture("vida/vida_0_1.png");
        vida[2] = new Texture("vida/vida_0_2.png");
        vida[3] = new Texture("vida/vida_1.png");
        vida[4] = new Texture("vida/vida_1_1.png");
        vida[5] = new Texture("vida/vida_1_2.png");
        vida[6] = new Texture("vida/vida_2.png");
        vida[7] = new Texture("vida/vida_2_1.png");
        vida[8] = new Texture("vida/vida_2_2.png");
        vida[9] = new Texture("vida/vida_3.png");
        vida[10] = new Texture("vida/vida_3_0.png");
        vida[11] = new Texture("vida/vida_3_1.png");
        vida[12] = new Texture("vida/vida_4.png");


        polvo = new Texture[6];
        popularArrayTexturas0("polvo", polvo.length, "inimigos/polvo/polvo_");

        baleia = new Texture[7];
        popularArrayTexturas1("baleia", baleia.length, "inimigos/whale/whale_");

        peixeDragao = new Texture[4];
        popularArrayTexturas0("peixeDragao", peixeDragao.length, "inimigos/peixeDragao/peixe_dragao_");

        peixeAzul = new Texture[12];
        popularArrayTexturas0("peixeAzul", peixeAzul.length, "inimigos/peixeAzul/peixe_azul_");

        redFish = new Texture[16];
        popularArrayTexturas0("redFish", redFish.length, "inimigos/redFish/fish_red_");

        inimigos = new Texture[5][];
        inimigos[0] = polvo;
        inimigos[1] = baleia;
        inimigos[2] = peixeAzul;
        inimigos[3] = peixeDragao;
        inimigos[4] = redFish;

        blueFish = new Texture[3];
        popularArrayTexturas0("blueFish", blueFish.length, "inimigos/blueFish/blue_fish_");

        orangeFish = new Texture[3];
        popularArrayTexturas0("orangeFish", orangeFish.length, "inimigos/orangeFish/orange_fish_");

        greenFish = new Texture[3];
        popularArrayTexturas0("greenFish", greenFish.length, "inimigos/greenFish/green_fish_");

        frog = new Texture[3];
        popularArrayTexturas0("frog", frog.length, "inimigos/frog/frog_");

        inimigosPequenos = new Texture[4][];
        inimigosPequenos[0] = blueFish;
        inimigosPequenos[1] = orangeFish;
        inimigosPequenos[2] = greenFish;
        inimigosPequenos[3] = frog;
    }

    private void popularArrayTexturas0(String nomeArray, int length, String caminho) {
        Texture[] auxArray = new Texture[length];
        for (int i = 0; i < length; i++) {
            auxArray[i] = new Texture(caminho + i + ".png");
        }
        verificarArray(nomeArray, auxArray);
    }

    private void popularArrayTexturas1(String nomeArray, int length, String caminho) {
        Texture[] auxArray = new Texture[length];
        for (int i = 0; i < length; i++) {
            auxArray[i] = new Texture(caminho + (i + 1) + ".png");
        }
        verificarArray(nomeArray, auxArray);
    }

    private void verificarArray(String nomeArray, Texture[] auxArray) {
        switch (nomeArray) {
            case "explosao2":
                explosao2 = auxArray.clone();
                break;
            case "explosao3":
                explosao3 = auxArray.clone();
                break;
            case "player":
                player = auxArray.clone();
                break;
            case "bolhas":
                bolhas = auxArray.clone();
                break;
            case "explosao":
                explosao = auxArray.clone();
                break;
            case "efeitoBrilho":
                efeitoBrilho = auxArray.clone();
                break;
            case "polvo":
                polvo = auxArray.clone();
                break;
            case "greenFish":
                greenFish = auxArray.clone();
                break;
            case "blueFish":
                blueFish = auxArray.clone();
                break;
            case "orangeFish":
                orangeFish = auxArray.clone();
                break;
            case "frog":
                frog = auxArray.clone();
                break;
            case "texturaCoracoesVida":
                texturaCoracoesVida = auxArray.clone();
                break;
            case "redFish":
                redFish = auxArray.clone();
                break;
            case "peixeAzul":
                peixeAzul = auxArray.clone();
                break;
            case "peixeDragao":
                peixeDragao = auxArray.clone();
                break;
            case "baleia":
                baleia = auxArray.clone();
                break;
        }
    }


    private float variacaoBolhas = 0;

    private void desenharTexturas() {
        batch.setProjectionMatrix(camera.combined);
        ScreenUtils.clear(1, 0, 0, 1);

        batch.begin();
        switch (estadoJogo) {
            case 0:
                batch.draw(fundos[4], 0, 0, larguraDispositivo, alturaDispositivo);
                batch.draw(nomeJogo, VIRTUAL_WIDTH / 2 - (float) nomeJogo.getWidth() / 2, VIRTUAL_HEIGHT / 2);
                batch.draw(toqueParaIniciar, VIRTUAL_WIDTH / 2 - (float) toqueParaIniciar.getWidth() / 2, VIRTUAL_HEIGHT / 2 - nomeJogo.getHeight() - 50);
                break;
            case 1:
                //Variação de fundos
                batch.draw(fundos[(int) variacaoFundos], 0, 0, larguraDispositivo, alturaDispositivo);

                //Bolhas
                batch.draw(bolha, posicaoBolhaX, posicaoBolhaY);
                batch.draw(bolha, posicaoBolha1X, posicaoBolha1Y);
                batch.draw(bolha, posicaoBolha2X, posicaoBolha2Y);
                batch.draw(bolha, posicaoBolha3X, posicaoBolha3Y);

                //Textura do jogador
                batch.draw(player[(int) variacao], posicaoPeixeX, posicaoPeixeY / 2);

                //Texturas animação da bolha ao redor do jogador
                batch.draw(bolhas[(int) variacaoBolhas], posicaoPeixeX + 20, posicaoPeixeY / 2 - 20);
                batch.draw(bolhas[(int) variacaoBolhas], posicaoPeixeX + 45, posicaoPeixeY / 2 - 30);

                //Texturas de objetos do fundo, topo e flutuantes
                batch.draw(objetosTopo[(int) variacaoObjetosTopo], posicaoObjTopoX, posicaoObjTopoY);
                batch.draw(objetosFundo[(int) indiceObjetosFundo], posicaoObjFundoX, posicaoObjFundoY);
                batch.draw(objetosFlutuantes[(int) indiceObjetosFlutuantes], posicaoObjFlutuanteX, posicaoObjFlutuanteY);

                //Texturas de vida
                batch.draw(vida[(int) indiceVida], larguraDispositivo - vida[(int) indiceVida].getWidth(), alturaDispositivo - vida[(int) indiceVida].getHeight());
                textoPontuacao.draw(batch, ((int) distaciaPercorrida) + " m", larguraDispositivo - 250, alturaDispositivo - vida[(int) indiceVida].getHeight() - 30);
                //batch.draw(texturaCoracoesVida[numeroVidas], larguraDispositivo - texturaCoracoesVida[numeroVidas].getWidth(), alturaDispositivo - texturaCoracoesVida[numeroVidas].getHeight() - vida[(int) indiceVida].getHeight() - 10);
                batch.draw(inimigos[(int) variacaoInimigoAtual][(int) variacaoTexturasInimigo], posicaoInimigoX, posicaoInimigoY);
                batch.draw(inimigosPequenos[(int) variacaoInimigoPequenoAtual][(int) variacaoTexturasInimigoPequeno], posicaoInimigoPequenoX, posicaoInimigoPequenoY);
                detectarColisoes();
                break;
            case 2:
                batch.draw(fundos[5], 0, 0, larguraDispositivo, alturaDispositivo);
                batch.draw(gameOver, VIRTUAL_WIDTH / 2 - (float) gameOver.getWidth() / 2, VIRTUAL_HEIGHT / 2);
                batch.draw(toqueParaReiniciar, VIRTUAL_WIDTH / 2 - (float) toqueParaReiniciar.getWidth() / 2, VIRTUAL_HEIGHT / 2 - nomeJogo.getHeight() - 50);
                textoMelhorPontuacao.draw(batch, "Maximum distance: " + (int) pontuacaoMaxima + " m", VIRTUAL_WIDTH / 2 - (VIRTUAL_WIDTH / 5), VIRTUAL_HEIGHT - gameOver.getHeight());
                break;
        }
        batch.end();
    }

    private boolean objFlutuanteSumiuX = false;

    private void controleObjetoFlutuante() {
        if (!objFlutuando && !objFlutuanteSumiuX) {
            valorRandomicoParaObjFlutuante();
        } else {
            verificaPosicoesObjFlutuante();
        }
    }

    private void valorRandomicoParaObjFlutuante() {
        float minX = 300;
        objFlutuando = true;
        posicaoObjFlutuanteY = random.nextInt((int) faixaAcimaTopoTela) + alturaDispositivo;
        posicaoObjFlutuanteX = random.nextInt((int) larguraDispositivo) + minX;
    }

    private void verificaPosicoesObjFlutuante() {
        if (posicaoObjFlutuanteY < 0) {
            //objFlutuando = false;
            posicaoObjFlutuanteY = -20;
            posicaoObjFlutuanteX -= Gdx.graphics.getDeltaTime() * velocidadeObjetosFlutuantesX;

            if (posicaoObjFlutuanteX < -objetosFlutuantes[(int) indiceObjetosFlutuantes].getWidth()) {
                objFlutuando = false;
                objFlutuanteSumiuX = false;
                indiceObjetosFlutuantes = random.nextInt(8);
                indiceExplosao = 0;
                indiceBrilho = 0;
                controleVida = true;
                perdaVida = false;
                ganhoVida = false;
            }
        } else if (objFlutuando && posicaoObjFlutuanteY < alturaDispositivo) {
            posicaoObjFlutuanteY -= Gdx.graphics.getDeltaTime() * velocidadeObjetosFlutuantesY;
            posicaoObjFlutuanteX -= Gdx.graphics.getDeltaTime() * velocidadeObjetosFlutuantesX;
        } else if (objFlutuando && posicaoObjFlutuanteY >= alturaDispositivo) {
            posicaoObjFlutuanteY -= Gdx.graphics.getDeltaTime() * velocidadeObjetosFlutuantesY;
        }
    }

    private void detectarColisoes() {
        Texture peixePlayer = player[(int) variacao];
        Texture objetoFundo = objetosFundo[(int) indiceObjetosFundo];
        Texture objetoTopo = objetosTopo[(int) variacaoObjetosTopo];
        Texture inimigoAtual = inimigos[variacaoInimigoAtual][(int) variacaoTexturasInimigo];
        Texture inimigoPequenoAtual = inimigosPequenos[(int) variacaoInimigoPequenoAtual][(int) variacaoTexturasInimigoPequeno];
        Texture objetoFlutuante = objetosFlutuantes[(int) indiceObjetosFlutuantes];

        retanguloPeixe.set(posicaoPeixeX, posicaoPeixeY / 2, peixePlayer.getWidth(), peixePlayer.getHeight());
        retanguloObjetoFundo.set(posicaoObjFundoX, posicaoObjFundoY, objetoFundo.getWidth(), objetoFundo.getHeight());
        retanguloObjetoTopo.set(posicaoObjTopoX, posicaoObjTopoY, objetoTopo.getWidth(), objetoTopo.getHeight());
        retanguloObjetoFlutuante.set(posicaoObjFlutuanteX, posicaoObjFlutuanteY, objetoFlutuante.getWidth(), objetoFlutuante.getWidth());
        retanguloInimigoAtual.set(posicaoInimigoX, posicaoInimigoY, inimigoAtual.getWidth(), inimigoAtual.getHeight());
        retanguloInimigoPequenoAtual.set(posicaoInimigoPequenoX, posicaoInimigoPequenoY, inimigoPequenoAtual.getWidth(), inimigoPequenoAtual.getHeight());

        boolean colidiuObjetoFlutuante = Intersector.overlaps(retanguloPeixe, retanguloObjetoFlutuante);
        boolean colidiuObjetoFundo = Intersector.overlaps(retanguloPeixe, retanguloObjetoFundo);
        boolean colidiuObjetoTopo = Intersector.overlaps(retanguloPeixe, retanguloObjetoTopo);
        boolean colidiuInimigo = Intersector.overlaps(retanguloPeixe, retanguloInimigoAtual);
        boolean colidiuInimigoPequeno = Intersector.overlaps(retanguloPeixe, retanguloInimigoPequenoAtual);

        checkCollisions(colidiuObjetoFlutuante, colidiuObjetoFundo, colidiuObjetoTopo, colidiuInimigo, colidiuInimigoPequeno);
    }

    private void checkCollisions(boolean colidiuObjetoFlutuante, boolean colidiuObjetoFundo, boolean colidiuObjetoTopo, boolean colidiuInimigo, boolean colidiuInimigoPequeno) {
        if (colidiuObjetoFlutuante) {
            if (estadoJogo == 1) {
                checarObjetoFlutuante();
            }
        }

        if (colidiuObjetoTopo) checarObjetoTopo();

        if (colidiuInimigo) {
            if (colidiuPeixeGrande) {
                indiceVida += 3;
                colidiuPeixeGrande = false;
                if (indiceVida >= vida.length) {
                    mudarParaEstadoGameOver();
                }
            }
        }

        if (colidiuInimigoPequeno) {
            if (colidiuPeixePequeno) {
                indiceVida++;
                colidiuPeixePequeno = false;
                if (indiceVida >= vida.length) {
                    mudarParaEstadoGameOver();
                }
            }
        }
        if (colidiuObjetoFundo) checarObjetoFundo();
    }

    private void checarObjetoTopo() {
        switch ((int) variacaoObjetosTopo) {
            case 0:
            case 1:
                reproduzirExplosaoObjetoTopo();
                break;
        }
    }

    boolean controleVidaObjTopo = true;
    boolean controleSomColisaoObjTopo = true;
    float varX = 0;
    float varY = 0;

    private void reproduzirExplosaoObjetoTopo() {
        if (controleSomColisaoObjTopo) {
            somPerdaVida.play();
            controleSomColisaoObjTopo = false;
        }
        if (controleVidaObjTopo) {
            varX = posicaoPeixeX;
            varY = posicaoPeixeY / 2;
            indiceVida += 3;
            if (indiceVida >= vida.length) {
                mudarParaEstadoGameOver();
            }
            controleVidaObjTopo = false;
        }

        if (indiceExplosao3 < explosao3.length) {
            batch.draw(explosao2[(int) indiceExplosao3], varX, varY);
            indiceExplosao3 += Gdx.graphics.getDeltaTime() * 10;
        }
    }

    boolean colidiuPeixePequeno = true;

    private void checarObjetoFundo() {
        switch ((int) indiceObjetosFundo) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                reproduzirExplosao2();
                break;
            case 5:
            case 6:
                reproduzirSomArbusto();
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                reproduzirExplosao2();
                break;
            case 13:
            case 14:
            case 15:
                executarAnimacaoBau();
                incrementarVida();
                break;
        }
    }

    private void reproduzirSomArbusto() {
        if (controleSomColisaoObjFundo) {
            somArbusto.play();
            controleSomColisaoObjFundo = false;
        }
    }

    private boolean controleVidaExplosao2 = true;
    boolean controleSomColisaoObjFundo = true;

    private void reproduzirExplosao2() {
        if (controleSomColisaoObjFundo) {
            somPerdaVida.play();
            controleSomColisaoObjFundo = false;
        }

        if (controleVidaExplosao2) {
            indiceVida += 3;
            if (indiceVida >= vida.length) {
                mudarParaEstadoGameOver();
            }
            controleVidaExplosao2 = false;
        }

        if (indiceExplosao2 < explosao2.length) {
            batch.draw(explosao2[(int) indiceExplosao2], posicaoObjFundoX, posicaoObjFundoY);
            indiceExplosao2 += Gdx.graphics.getDeltaTime() * 10;
        }
    }

    private void mudarParaEstadoGameOver() {
        estadoJogo = 2;
        pararTodasMusicas();
    }

    private boolean controleBauVida = true;

    boolean controleSomVida = true;

    private void incrementarVida() {
        if (controleSomVida) {
            somGanhoVida.play();
            controleSomVida = false;
        }
        if (controleBauVida && indiceVida > 0) {
            indiceVida -= 3;
            if (indiceVida < 0) indiceVida = 0;

            controleBauVida = false;
        }
    }


    private void executarAnimacaoBau() {
        indiceObjetosFundo += Gdx.graphics.getDeltaTime() * 10;
        if ((int) indiceObjetosFundo < 16) {
            batch.draw(objetosFundo[(int) indiceObjetosFundo], posicaoObjFundoX, posicaoObjFundoY);
        } else {
            indiceObjetosFundo = 15;
        }
    }

    private void checarObjetoFlutuante() {
        switch ((int) indiceObjetosFlutuantes) {
            case 0:
            case 5:
            case 7:
                gerarEfeitoExplosao();
                break;
            case 1:
            case 2:
            case 3:
            case 6:
            case 4:
                reproduzirEfeitoAumentoVida();
                break;
            case 8:
                if (perdaVida) gerarEfeitoExplosao();
                if (ganhoVida) {
                    gerarEfeitoBrilho();
                }
                break;
        }
    }

    private void gerarEfeitoBrilho() {
        if (indiceBrilho < efeitoBrilho.length) {
            Texture texturaBrilhoAtual = efeitoBrilho[(int) indiceBrilho];
            batch.draw(texturaBrilhoAtual, posicaoObjFlutuanteX, posicaoObjFlutuanteY);
            indiceBrilho += Gdx.graphics.getDeltaTime() * 10;
        }
    }

    boolean ganhoVida = false;
    boolean perdaVida = false;

    private void reproduzirEfeitoAumentoVida() {
        indiceObjetosFlutuantes = 8;
        if (controleVida) {
            if (indiceVida > 0) {
                indiceVida -= 3;
                somGanhoVida.play();
                if (indiceVida < 0) indiceVida = 0;
            }
            controleVida = false;
            ganhoVida = true;
        }
    }

    private float indiceExplosao = 0;
    private boolean controleVida = true;

    private void gerarEfeitoExplosao() {
        reduzirVida();
        if (indiceExplosao < explosao.length) {
            batch.draw(explosao[(int) indiceExplosao], posicaoObjFlutuanteX, posicaoObjFlutuanteY);
            indiceExplosao += Gdx.graphics.getDeltaTime() * 10;
        }
    }

    private void reduzirVida() {
        indiceObjetosFlutuantes = 8;
        if (controleVida) {
            indiceVida += 3;
            somExplosao.play();
            if (indiceVida >= vida.length) {
                mudarParaEstadoGameOver();
            }
            controleVida = false;
            perdaVida = true;
        }
    }

    private void pararTodasMusicas() {
        musicaFase1.stop();
        musicaFase2.stop();
        musicaFase3.stop();
        musicaFase4.stop();
    }

    private void verificarEstadoJogo() {
        boolean toqueTela = Gdx.input.justTouched();
        boolean toqueLongo = Gdx.input.isTouched();

        switch (estadoJogo) {
            case 0:
                variacao = 0;
                musicaTema.setLooping(true);
                musicaTema.play();

                if (toqueTela) {
                    estadoJogo = 1;
                }
                break;
            case 1:
                musicaTema.dispose();

                controleAd = false;
                checarToque(toqueTela);
                checarObjetosPassadosNaTela();
                controleObjetoFlutuante();
                chacarToqueLongo(toqueLongo);
                aplicarVariacoes();
                verificarSeObjetoFundoSumiuDaTela();
                aplicarGravidade(toqueTela);
                verificaParteSuperiorAtingida();

                //Incrementar gravidade
                gravidade++;
                break;
            case 2:
                if (!controleAd) {
                    adController.showIntersticialAd();
                    controleAd = true;
                }

                if (distaciaPercorrida > pontuacaoMaxima) {
                    pontuacaoMaxima = (int) distaciaPercorrida;
                    preferencias.putFloat("distanciaMaxima", pontuacaoMaxima);
                    preferencias.flush();
                }
                eventoDeToqueGameOver(toqueTela);
                break;
        }
    }

    private boolean controleAd = false;

    private void configuraBooleansDeControleObjTopo() {
        controleSomColisaoObjTopo = true;
        controleVidaObjTopo = true;
    }

    private void chacarToqueLongo(boolean toqueLongo) {
        if (toqueLongo) {
            if (Gdx.input.getX() > posicaoPeixeX) {
                posicaoPeixeX += 10;
            } else {
                posicaoPeixeX -= 10;
            }
        }
    }

    private void checarToque(boolean toqueTela) {
        if (toqueTela) {
            somPeixe.play();
            gravidade = -25;
        }
    }

    int distaciaAnterior = -200;

    private void checarObjetosPassadosNaTela() {
        if (estadoJogo != 2) {
            int distanciaAtual = (int) distaciaPercorrida;
            if ((distanciaAtual - distaciaAnterior) >= 200) {
                distaciaAnterior = distanciaAtual;
                if (indiceMusicas == 0) {
                    musicas[musicas.length - 1].stop();
                } else {
                    musicas[indiceMusicas - 1].stop();
                }
                musicas[indiceMusicas].setLooping(true);
                musicas[indiceMusicas].play();
                musicas[indiceMusicas].setVolume(VOLUME);
                increaseSpeed();

                indiceMusicas = indiceMusicas < musicas.length - 1 ? indiceMusicas + 1 : 0;
                variacaoFundos = variacaoFundos < fundos.length - 1 ? variacaoFundos + 1 : 0;
            }
        }
    }

    private void increaseSpeed() {
        velocidadeInimigo += 100;
        velocidadeObjetosFundo += 100;
        velocidadeObjetoTopo += 100;
        velocidadeObjetosFlutuantesX += 100;
        velocidadeObjetosFlutuantesY += 100;
        incrementoDistancia += 2;
    }

    private void eventoDeToqueGameOver(boolean toqueTela) {
        if (toqueTela) {
            resetVariables();
        }
    }

    private void resetVariables() {
        estadoJogo = 1;
        distaciaPercorrida = 0;
        indiceVida = 0;
        indiceObjetosFundo = 0;
        indiceObjetosFlutuantes = 0;
        variacaoFundos = 0;
        distaciaAnterior = -200;
        posicaoPeixeX = 100;
        posicaoPeixeY = alturaDispositivo;
        posicaoObjFundoY = -20;
        posicaoObjFundoX = larguraDispositivo;
        velocidadeInimigo = 400;
        velocidadeObjetosFundo = 400;
        indiceMusicas = 0;
        incrementoDistancia = 2;
        velocidadeObjetoTopo = 400;
        velocidadeObjetosFlutuantesX = 400;
        velocidadeObjetosFlutuantesY = 400;
        velocidadeInimigoPequeno = 400;
    }

    private void aplicarVariacoes() {
        //Player
        variacao += Gdx.graphics.getDeltaTime() * 10;

        //Distância percorrida
        distaciaPercorrida += Gdx.graphics.getDeltaTime() * incrementoDistancia;

        //Objetos do fundo
        variacaoObjetos = random.nextInt(13);
        posicaoObjFundoX -= Gdx.graphics.getDeltaTime() * velocidadeObjetosFundo;

        //Inimigos maiores
        posicaoInimigoX -= Gdx.graphics.getDeltaTime() * velocidadeInimigo;
        variacaoTexturasInimigo += Gdx.graphics.getDeltaTime() * 10;
        verificaLimiteArrayInimigoMaiorAtual();
        verificaSeInimigoMaiorPassouATela();

        //Inimigos pequenos
        posicaoInimigoPequenoX -= Gdx.graphics.getDeltaTime() * velocidadeInimigoPequeno;
        variacaoTexturasInimigoPequeno += Gdx.graphics.getDeltaTime() * 10;
        verificaLimiteArrayInimigoPequenoAtual();
        verificaSeInimigoPequenoPassouATela();

        //Objetos do topo
        posicaoObjTopoX -= Gdx.graphics.getDeltaTime() * velocidadeObjetoTopo;
        verificaSeObjetoTopoDesapareceu();

        //Bolhas player
        variacaoBolhas += Gdx.graphics.getDeltaTime() * 5;

        //Bolhas do mar
        posicaoBolhaY += Gdx.graphics.getDeltaTime() * random.nextInt(400);
        posicaoBolha1Y += Gdx.graphics.getDeltaTime() * random.nextInt(200);
        posicaoBolha2Y += Gdx.graphics.getDeltaTime() * random.nextInt(100);
        posicaoBolha3Y += Gdx.graphics.getDeltaTime() * random.nextInt(500);
        verificaSeAsBolhasAtingiramOTopo();

        //Zerar variações
        if (variacaoBolhas > 7) variacaoBolhas = 0;
        if (variacao > 12) variacao = 0;
    }

    private void verificaSeInimigoPequenoPassouATela() {
        if (posicaoInimigoPequenoX < -inimigosPequenos[(int) variacaoInimigoPequenoAtual][(int) variacaoTexturasInimigoPequeno].getWidth()) {
            variacaoInimigoPequenoAtual = random.nextInt(inimigosPequenos.length);
            if (tocandoSomInimigoPequeno) somInimigoPequenoAtual.stop();
            tocandoSomInimigoPequeno = false;
            checarInimigoPequenoAtual();
            variacaoTexturasInimigoPequeno = 0;
            posicaoInimigoPequenoX = larguraDispositivo + random.nextInt(2000);
            posicaoInimigoPequenoY = random.nextInt((int) alturaDispositivo);
            colidiuPeixePequeno = true;
        }
    }

    private void checarInimigoPequenoAtual() {
        switch ((int) variacaoInimigoPequenoAtual) {
            case 0:
                /*if (!tocandoSomInimigoPequeno) {
                    somInimigoPequenoAtual = somPolvo;
                    somInimigoPequenoAtual.loop();
                    tocandoSomInimigoPequeno = true;
                }*/

                break;
            case 1:
               /* if (!tocandoSomInimigoPequeno) {
                    somInimigoPequenoAtual = somBaleia;
                    somInimigoPequenoAtual.loop();
                    tocandoSomInimigoPequeno = true;
                }*/
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    private void verificaLimiteArrayInimigoPequenoAtual() {
        if (variacaoTexturasInimigoPequeno > inimigosPequenos[(int) variacaoInimigoPequenoAtual].length) {
            variacaoTexturasInimigoPequeno = 0;
        }
    }

    private void verificaSeObjetoTopoDesapareceu() {
        Texture texturaObjetoTopoAtual = objetosTopo[indiceObjetosTopo];
        int distaciaAleatoriaObjTopo = 2000;
        if (posicaoObjTopoX < -texturaObjetoTopoAtual.getWidth()) {
            posicaoObjTopoY = alturaDispositivo - objetosTopo[(int) variacaoObjetosTopo].getHeight() + 20;
            posicaoObjTopoX = larguraDispositivo + random.nextInt(distaciaAleatoriaObjTopo);
            variacaoObjetosTopo = random.nextInt(objetosTopo.length);
            configuraBooleansDeControleObjTopo();
            indiceExplosao3 = 0;
        }
    }

    private void verificaLimiteArrayInimigoMaiorAtual() {
        if (variacaoTexturasInimigo > inimigos[variacaoInimigoAtual].length) {
            variacaoTexturasInimigo = 0;
        }
    }

    private void verificaSeInimigoMaiorPassouATela() {
        if (posicaoInimigoX < -inimigos[variacaoInimigoAtual][(int) variacaoTexturasInimigo].getWidth()) {
            variacaoInimigoAtual = random.nextInt(inimigos.length);
            if (tocandoSomInimigo) somInimigoAtual.stop();
            tocandoSomInimigo = false;
            checarInimigoAtual();
            variacaoTexturasInimigo = 0;
            posicaoInimigoX = larguraDispositivo;
            posicaoInimigoY = random.nextInt((int) alturaDispositivo);
            colidiuPeixeGrande = true;
        }
    }

    boolean tocandoSomInimigo = false;

    private void checarInimigoAtual() {
        switch (variacaoInimigoAtual) {
            case 0:
                definirSomInimigo(somPolvo);
                break;
            case 1:
                definirSomInimigo(somBaleia);
                break;
            case 2:
                break;
            case 3:
                definirSomInimigo(somDragao);
                break;
            case 4:
                break;
        }
    }

    private void definirSomInimigo(Sound somInimigo) {
        if (!tocandoSomInimigo) {
            somInimigoAtual = somInimigo;
            somInimigoAtual.loop();
            tocandoSomInimigo = true;
        }
    }

    private void verificaSeAsBolhasAtingiramOTopo() {
        if (posicaoBolhaY > alturaDispositivo) {
            posicaoBolhaX = random.nextInt((int) larguraDispositivo);
            posicaoBolhaY = -bolha.getHeight();
        }
        if (posicaoBolha1Y > alturaDispositivo) {
            posicaoBolha1X = random.nextInt((int) larguraDispositivo);
            posicaoBolha1Y = -bolha.getHeight();
        }
        if (posicaoBolha2Y > alturaDispositivo) {
            posicaoBolha2X = random.nextInt((int) larguraDispositivo);
            posicaoBolha2Y = -bolha.getHeight();
        }
        if (posicaoBolha3Y > alturaDispositivo) {
            posicaoBolha3X = random.nextInt((int) larguraDispositivo);
            posicaoBolha3Y = -bolha.getHeight();
        }
    }

    private void verificarSeObjetoFundoSumiuDaTela() {
        Texture texturaObjetoFundoAtual = objetosFundo[(int) indiceObjetosFundo];
        if (posicaoObjFundoX < -texturaObjetoFundoAtual.getWidth()) {
            posicaoObjFundoX = larguraDispositivo;
            indiceObjetosFundo = random.nextInt(14);
            configuraBooleansDeControleObjFundo();
            indiceExplosao2 = 0;
        }
    }

    private void configuraBooleansDeControleObjFundo() {
        controleBauVida = true;
        controleVidaExplosao2 = true;
        controleSomColisaoObjFundo = true;
        controleSomVida = true;
    }

    private void aplicarGravidade(boolean toqueTela) {
        if (posicaoPeixeY > 0 || toqueTela) {
            posicaoPeixeY = posicaoPeixeY - gravidade;
        } else {
            posicaoPeixeY = 0;
        }
    }

    boolean atingiuBordaSuperior = true;

    private void verificaParteSuperiorAtingida() {
        int alturaTexturaPeixe = player[(int) variacao].getHeight();
        int limiteSuperiorTela = (int) alturaDispositivo * 2 - alturaTexturaPeixe * 2;
        if (posicaoPeixeY > limiteSuperiorTela) {
            if (atingiuBordaSuperior) {
                somChoqueBorda.play();
                atingiuBordaSuperior = false;
            }
            posicaoPeixeY = limiteSuperiorTela;
        } else {
            atingiuBordaSuperior = true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
