import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    //setting height and width of game screen
    int boardWidth = 360;
    int boardHeight = 640;

    //Images variables
    Image backgroundimage, birdimage, toppipeimage, bottompipiimage;

    //Bird initial Position
    int birdX=boardWidth/8;
    int birdY=boardHeight/2;
    int birdwidth=34;
    int birdheight=24;

    class Bird {
        int x=birdX;
        int y=birdY;
        int width=birdwidth;
        int height=birdheight;
        Image img;

        Bird(Image img){
            this.img=img;
        }
    }

    //pipes logic
    int pipeX=boardWidth;
    int pipeY=0;
    int pipewidth=64;  //scaled to 1/6 or the image size
    int pipeheight=512;

    class Pipe{
        int x=pipeX;
        int y=pipeY;
        int height=pipeheight;
        int width=pipewidth;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img=img;
        }
    }

    //game logic variabls
    Bird bird;
    Timer gameloop;
    Timer placepipetimer;
    int velocityX=-4; //speed of pipes moving towards left
    int velocityY=0; //initial velocity of bird 
    int gravity = 1; //gravity acting on bird

    ArrayList<Pipe> pipes; //to stores different sizes of pipes
    Random random=new Random(); //for random heights of pipes

    boolean gameover=false; 
    double score = 0; //to store the score of game

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true); //to make Jpanel take the key events
        addKeyListener(this); //will check the three fucntions of KeyListeners

        //Loading all the image
        backgroundimage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdimage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        toppipeimage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottompipiimage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdimage);
        //pipes
        pipes = new ArrayList<Pipe>();
        //place pipe timer every 1.5 seconds
        placepipetimer=new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipe();
            }
        });
        placepipetimer.start();
        //time loop
        gameloop=new Timer(1000/60, this); //16.6 seconds
        gameloop.start();
    }

    public void placePipe(){
        int randompipeY = (int) (pipeY - (pipeheight/4) - Math.random()*(pipeheight/2)); //Range 1/4 to 3/4 of pipe height
        int openingspace = boardHeight/4;

        Pipe toppipe = new Pipe(toppipeimage);
        toppipe.y=randompipeY;
        pipes.add(toppipe);

        Pipe bottompipe = new Pipe(bottompipiimage);
        bottompipe.y=toppipe.y+pipeheight+openingspace; //to get the y position of bottom pipe
        pipes.add(bottompipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //backgroud image
        g.drawImage(backgroundimage,0,0,boardWidth,boardHeight,null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe=pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipewidth, pipeheight, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Ariel",Font.PLAIN ,32));
        if(gameover){
            g.drawString("Game Over : " +String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y=Math.max(bird.y,0);

        for(int i=0;i<pipes.size();i++){
            Pipe pipe=pipes.get(i);
            pipe.x+=velocityX;

            if(!pipe.passed && bird.x>pipe.x+pipe.width){
                pipe.passed=true;
                score+=0.5;  //because there are two pipes(top and bottom) hence 0.5+0.5 = 1 score for a set of pipes.
            }

            if(collision(bird, pipe)){
                gameover=true;
            }
        }

        if(bird.y>boardHeight){
            gameover=true;
        }
    }

    public boolean collision(Bird b, Pipe p){
        return  b.x<p.x+p.width &&  // b top left corner dont reach p top right corner
                b.x+b.width>p.x &&  // b top right corner passes p top left corner
                b.y<p.y+p.height &&  // b top left corner dont reach p bottom left corner
                b.y+b.height>p.y;  // b bottom left corner passes p top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            placepipetimer.stop();
            gameloop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY=-9;

            if(gameover){
                bird.y=birdY;
                velocityY=0;
                score=0;
                pipes.clear();
                gameover=false;
                gameloop.start();
                placepipetimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
