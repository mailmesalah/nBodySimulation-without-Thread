package nbodysimulation.algorithm;

//required to paint on screen
import java.awt.Color;

import nbodysimulation.basic.Body;

//Start the applet and define a few necessary variables
public class BruteForce {

    public int N;
    public Body bodies[];
    public boolean shouldrun = false;

    // The first time we call the applet, this function will start
    public BruteForce(int numOfBodies) {      
        startthebodies(numOfBodies);
    }

    // This method gets called when the applet is terminated. It stops the code
    public void stop() {
        shouldrun = false;
    }

    // the bodies are initialized in circular orbits around the central mass.
    // This is just some physics to do that
    public static double circlev(double rx, double ry) {
        double solarmass = 1.98892e30;
        double r2 = Math.sqrt(rx * rx + ry * ry);
        double numerator = (6.67e-11) * 1e6 * solarmass;
        return Math.sqrt(numerator / r2);
    }

    // Initialize N bodies with random positions and circular velocities
    public void startthebodies(int N) {
        this.N=N;
        bodies= new Body[N];
        double radius = 1e18; // radius of universe
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
            // Orient a random 2D circular orbit

            if (Math.random() <= .5) {
                vx = -vx;
                vy = -vy;
            }

            double mass = Math.random() * solarmass * 10 + 1e20;
            // Color the masses in green gradients by mass
            int red = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
            int blue = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
            int green = 255;
            Color color = new Color(red, green, blue);
            bodies[i] = new Body(px, py, vx, vy, mass, color);
        }
		// Put the central mass in
        // put a heavy body in  the center
        bodies[0] = new Body(0, 0, 0, 0, 1e6 * solarmass, Color.red);

    }

    // Use the method in Body to reset the forces, then add all the new forces
    public void addforces(int N) {
        for (int i = 0; i < N; i++) {
            bodies[i].resetForce();
            // Notice-2 loops-->N^2 complexity
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    bodies[i].addForce(bodies[j]);
                }
            }
        }
        // Then, loop again and update the bodies using timestep dt
        for (int i = 0; i < N; i++) {
            bodies[i].update(1e11);
        }
    }

    public static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }

}
