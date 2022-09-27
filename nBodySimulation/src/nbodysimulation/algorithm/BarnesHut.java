/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbodysimulation.algorithm;

/**
 *
 * @author Sely
 */
// required to paint on screen
import java.awt.*;
import nbodysimulation.basic.BarnesHutTree;
import nbodysimulation.basic.Body;
import nbodysimulation.basic.Quad;

public class BarnesHut {

    public int N = 100;
    public Body bodies[];
    public boolean shouldrun = false;
    Quad q = new Quad(0, 0, 2 * 1e18);

// BarnesHut Constructor
    public BarnesHut(int numOfBodies) {
        startthebodies(numOfBodies);
    }

// It stops the code
    public void stop() {
        shouldrun = false;
    }

  //the bodies are initialized in circular orbits around the central mass.
    //This is just some physics to do that
    public static double circlev(double rx, double ry) {
        double solarmass = 1.98892e30;
        double r2 = Math.sqrt(rx * rx + ry * ry);
        double numerator = (6.67e-11) * 1e6 * solarmass;
        return Math.sqrt(numerator / r2);
    }

    //Initialize N bodies
    public void startthebodies(int N) {
        this.N = N;
        bodies = new Body[N];
        double radius = 1e18;        // radius of universe
        double solarmass = 1.98892e30;
        for (int i = 0; i < N; i++) {
            double px = 1e18 * exp(-1.8) * (.5 - Math.random());
            double py = 1e18 * exp(-1.8) * (.5 - Math.random());
            double magv = circlev(px, py);

            double absangle = Math.atan(Math.abs(py / px));
            double thetav = Math.PI / 2 - absangle;
            double phiv = Math.random() * Math.PI;
            double vx = -1 * Math.signum(py) * Math.cos(thetav) * magv;
            double vy = Math.signum(px) * Math.sin(thetav) * magv;
      //Orient a random 2D circular orbit

            if (Math.random() <= .5) {
                vx = -vx;
                vy = -vy;
            }
            double mass = Math.random() * solarmass * 10 + 1e20;
            //Color a shade of blue based on mass
            int red = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
            int blue = 255;
            int green = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
            Color color = new Color(red, green, blue);
            bodies[i] = new Body(px, py, vx, vy, mass, color);
        }
        bodies[0] = new Body(0, 0, 0, 0, 1e6 * solarmass, Color.red);//put a heavy body in the center

    }

    //The BH algorithm: calculate the forces
    public void addforces(int N) {
        BarnesHutTree thetree = new BarnesHutTree(q);
        // If the body is still on the screen, add it to the tree
        for (int i = 0; i < N; i++) {
            if (bodies[i].in(q)) {
                thetree.insert(bodies[i]);
            }
        }
            //Now, use out methods in BarnesHutTree to update the forces,
        //traveling recursively through the tree
        for (int i = 0; i < N; i++) {
            bodies[i].resetForce();
            if (bodies[i].in(q)) {
                thetree.updateForce(bodies[i]);
                //Calculate the new positions on a time step dt (1e11 here)
                bodies[i].update(1e11);
            }
        }
    }

    //A function to return an exponential distribution for position
    public static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }

}
