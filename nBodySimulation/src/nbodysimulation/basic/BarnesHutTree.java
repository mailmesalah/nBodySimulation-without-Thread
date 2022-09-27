/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbodysimulation.basic;

/**
 *
 * @author Sely
 */

public class BarnesHutTree {

    private Body body;     // body or aggregate body stored in this node
    private Quad quad;     // square region that the tree represents
    private BarnesHutTree NW;     // tree representing northwest quadrant
    private BarnesHutTree NE;     // tree representing northeast quadrant
    private BarnesHutTree SW;     // tree representing southwest quadrant
    private BarnesHutTree SE;     // tree representing southeast quadrant
    
    //
    private double theta=1.0;

    //Create and initialize a new bhtree. Initially, all nodes are null and will be filled by recursion
    //Each BarnesHutTree represents a quadrant and a body that represents all bodies inside the quadrant
    public BarnesHutTree(Quad q) {
        this.quad = q;
        this.body = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }

    //If all nodes of the BarnesHutTree are null, then the quadrant represents a single body and it is "external"
    public Boolean isExternal(BarnesHutTree t) {
        if (t.NW == null && t.NE == null && t.SW == null && t.SE == null) {
            return true;
        } else {
            return false;
        }
    }

    //We have to populate the tree with bodies. We start at the current tree and recursively travel through the branches
    public void insert(Body b) {
        //If there's not a body there already, put the body there.
        if (this.body == null) {
            this.body = b;
        } //If there's already a body there, but it's not an external node
        //combine the two bodies and figure out which quadrant of the 
        //tree it should be located in. Then recursively update the nodes below it.
        else if (this.isExternal(this) == false) {
            this.body.addForce(b);

            Quad northwest = this.quad.NW();
            Quad northeast = this.quad.NE();
            Quad southeast = this.quad.SE();
            Quad southwest = this.quad.SW();
            
            if (b.in(northwest)) {
                if (this.NW == null) {
                    this.NW = new BarnesHutTree(northwest);
                }
                NW.insert(b);
            } else if (b.in(northeast)) {
                if (this.NE == null) {
                    this.NE = new BarnesHutTree(northeast);
                }
                NE.insert(b);
            } else if (b.in(southeast)) {
                if (this.SE == null) {
                    this.SE = new BarnesHutTree(southeast);
                }
                SE.insert(b);
            } else {

                if (this.SW == null) {
                    this.SW = new BarnesHutTree(southwest);
                }
                SW.insert(b);
            }

        } //If the node is external and contains another body, create BarnesHuts
        //where the bodies should go, update the node, and end 
        //(do not do anything recursively)
        else if (this.isExternal(this)) {
            Body c = this.body;
            Quad northwest = this.quad.NW();
            Quad northeast = this.quad.NE();
            Quad southeast = this.quad.SE();
            Quad southwest = this.quad.SW();

            if (c.in(northwest)) {
                if (this.NW == null) {
                    this.NW = new BarnesHutTree(northwest);
                }
                NW.insert(c);
            } else if (c.in(northeast)) {
                if (this.NE == null) {
                    this.NE = new BarnesHutTree(northeast);
                }
                NE.insert(c);
            } else if (c.in(southeast)) {
                if (this.SE == null) {
                    this.SE = new BarnesHutTree(southeast);
                }
                SE.insert(c);
            } else {

                if (this.SW == null) {
                    this.SW = new BarnesHutTree(southwest);
                }
                SW.insert(c);
            }

            this.insert(b);
        }
    }

    //Start at the main node of the tree. Then, recursively go each branch
    //Until either we reach an external node or we reach a node that is sufficiently
    //far away that the external nodes would not matter much.
    public void updateForce(Body b) {
        if (this.isExternal(this)) {
            if (this.body != b) {
                b.addForce(this.body);
            }
        } else if (this.quad.length() / (this.body.distanceTo(b)) < theta) {
            b.addForce(this.body);
        } else {
            if (this.NW != null) {
                this.NW.updateForce(b);
            }
            if (this.SW != null) {
                this.SW.updateForce(b);
            }
            if (this.SE != null) {
                this.SE.updateForce(b);
            }
            if (this.NE != null) {
                this.NE.updateForce(b);
            }
        }
    }

    // convert to string representation for output
    public String toString() {
        if (NE != null || NW != null || SW != null || SE != null) {
            return "*" + body + "\n" + NW + NE + SW + SE;
        } else {
            return " " + body + "\n";
        }
    }
}
