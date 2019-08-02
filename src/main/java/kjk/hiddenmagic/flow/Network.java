package kjk.hiddenmagic.flow;

import java.util.HashSet;
import java.util.Set;

public class Network<T> {

    Set<Node> nodes = new HashSet<>();

    public void addEdge(Node a, Node b) {
        nodes.add(a);
        nodes.add(b);
        a.neighbors.add(b);
        b.neighbors.add(a);
    }

    class Node {
//        BLockPos data;
        Set<Node> neighbors = new HashSet<>();

        public Node(T data) {
//            this.data = data;
        }
    }

}
