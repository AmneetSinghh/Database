package Implementation.IndexFile;

import Implementation.DBMS.Record;

import java.util.*;


class BTreeNode {
    int threshHold;
    int[] keys;
    public Map<Integer,Integer> KeyIdAgeMap;// age,id
    BTreeNode[] children;
    int noKeys; // number of keys
    int noChild; // number of children
    boolean leaf;

    public int threshHoldKey(){
        return 2*threshHold-1;// max no of keys
    }
    public int threshHoldChildren(){
        return 2*threshHold;// max no of children
    }



    public BTreeNode(int t, boolean leaf) {
        this.threshHold = t;
        this.leaf = leaf;
        keys = new int[threshHoldKey()];
        KeyIdAgeMap = new HashMap<>();
        children = new BTreeNode[threshHoldChildren()];
        noKeys = 0;// telling me how many keys are in this BtTreeNode.
        noChild = 0;
    }

    public void traverse() {
        // root.traverse.
        int i;
        for (i = 0; i < noKeys; i++) {
            if (!leaf)
                children[i].traverse();
            System.out.print(" " + keys[i]);
        }

        if (!leaf)
            children[i].traverse();
    }

    public static int IO_COUNT=0;
    public BTreeNode search(int k) {
        System.out.println("IO CALL : "+ IO_COUNT+ " Fetching from hardisk/ssd");
        int i = 0;
        while (i < noKeys && k > keys[i])
            i++;

        if (i < noKeys && keys[i] == k)
            return this;

        if (leaf)
            return null;
        IO_COUNT++;
        return children[i].search(k);
    }


    public void insertNonFull(Record record) {
        int lastKey = noKeys - 1;

        if (leaf) {
            /*
             * when it has no children -> leaf node.
             * current node is not full
             */
            while (lastKey >= 0 && record.age < keys[lastKey]) {
                keys[lastKey + 1] = keys[lastKey];
                lastKey--;
            }

            keys[lastKey + 1] = record.age;
            KeyIdAgeMap.put(record.age,record.id);
            noKeys++;
        } else {
            /*
             * Not a leaf, when it has children first try to put element into children
             * current node is not full
             */
            while (lastKey >= 0 && record.age < keys[lastKey])
                lastKey--;

            lastKey++;
            if (children[lastKey].noKeys == threshHoldKey()) {
                splitChild(lastKey, children[lastKey]);
                if (keys[lastKey] < record.age)
                    lastKey++;
            }
            children[lastKey].insertNonFull(record);// current node's children...  recursively.
        }
    }

    private BTreeNode makeRightChild(BTreeNode fullNode){
        BTreeNode rightChild = new BTreeNode(fullNode.threshHold, fullNode.leaf);
        rightChild.noKeys = threshHold - 1;

        for(int j = 0; j < threshHold - 1; j++){
            rightChild.keys[j] = fullNode.keys[j + threshHold];
            rightChild.KeyIdAgeMap.put(rightChild.keys[j],fullNode.KeyIdAgeMap.get(fullNode.keys[j + threshHold]));// age..
        }



        /*
         * If full node is not leaf, it means, we need to add their children as well
         * children will always be 1 more than keys.
         */
        if (!fullNode.leaf) {
            for (int j = 0; j < threshHold; j++){
                rightChild.children[j] = fullNode.children[j + threshHold];
                fullNode.children[j + threshHold] = null;
                fullNode.noChild--;
            }
        }

        for(int i=threshHold;i<fullNode.noKeys;i++){
            fullNode.keys[i] = -1;
            fullNode.KeyIdAgeMap.remove(fullNode.keys[i]);
        }
        fullNode.noKeys = threshHold - 1;
        return rightChild;
    }

    private void addToParentRigthChild(int idOfParentChild, BTreeNode rightChild){
        for (int j = noKeys; j > idOfParentChild; j--)
            children[j + 1] = children[j];

        children[idOfParentChild + 1] = rightChild;
        this.noChild++;
    }

    private void addMidKeyToParent(int idOfParentChild, BTreeNode fullNode){
        for (int j = noKeys - 1; j >= idOfParentChild; j--)
            keys[j + 1] = keys[j];

        keys[idOfParentChild] = fullNode.keys[threshHold - 1];
        KeyIdAgeMap.put(fullNode.keys[threshHold - 1],fullNode.KeyIdAgeMap.get(fullNode.keys[threshHold - 1]));
        fullNode.keys[threshHold - 1]=-1;
        fullNode.KeyIdAgeMap.remove(fullNode.keys[threshHold - 1]);// removing id.
        noKeys++;
    }

    void splitChild(int idOfParentChild, BTreeNode fullNode) {
        BTreeNode rightChild = makeRightChild(fullNode);
        addToParentRigthChild(idOfParentChild,rightChild);
        addMidKeyToParent(idOfParentChild,fullNode);
    }

}

class BTree {
    private BTreeNode root;
    private final int threshHold;


    private void makeNewKeyRoot(Record record){
        root = new BTreeNode(threshHold, true);
        root.keys[0] = record.age;
        root.noKeys = 1;
        root.KeyIdAgeMap.put(record.age,record.id);
    }

    public BTree(int t) {
        this.root = null;
        this.threshHold = t;
    }

    public void traverse() {
        if (root != null)
            root.traverse();
    }

    public BTreeNode search(int k) {
        if (root == null) {
            return null;
        }
        else {
            BTreeNode.IO_COUNT=0;
           return  root.search(k);
        }
    }

    public void insert(Record record) {
        if (root == null) {
            makeNewKeyRoot(record);
        } else {
            if (root.noKeys == root.threshHoldKey()) {
                /*
                 * New Root node
                 */
                BTreeNode newRoot = new BTreeNode(threshHold, false);
                newRoot.children[0] = root;
                newRoot.noChild = 1;
                newRoot.splitChild(0, root);
                int i = 0;
                if (newRoot.keys[0] < record.age)
                    i++;
                newRoot.children[i].insertNonFull(record);
                root = newRoot;
            } else{
                root.insertNonFull(record);
            }

        }
    }

    public void printTree(){
        System.out.println("------------------------------------------------------------------------------------------");
        Queue<BTreeNode> queue = new LinkedList();
        queue.add(root);
        int level=1;
        while(!queue.isEmpty()){
            System.out.println("Level-> "+ level);
            int sz = queue.size();
            for(int i=0;i<sz;i++){
                BTreeNode cur = queue.peek();
                queue.poll();
                for(int j=0;j<cur.noKeys;j++){
                    System.out.print("Key: "+ cur.keys[j]+" " + "Id from Keymap: "+ cur.KeyIdAgeMap.get(cur.keys[j])+" | ");
                }
                if(i != sz-1) System.out.println("  |  ");
                for(int l=0;l<cur.noChild;l++){
                    if(cur.children[l]!=null){
                        queue.add(cur.children[l]);
                    }
                    else break;
                }
            }
            level++;
            System.out.println("\n");
        }
    }

}


public class Age_Index_File{

    static int THRESHOLD ;
    static BTree tree ;
    public Age_Index_File(int threshold){
        THRESHOLD = threshold;
        tree = new BTree(threshold);
    }

    public static BTree getTree() {
        return tree;
    }

    public static void insert(Record record){
        getTree().insert(record);
    }

    public static void printTree(){
        getTree().printTree();
    }

    public static void traverse() {
        getTree().traverse();
    }

    public static int search(int age) {// Find with index id.
        return getTree().search(age).KeyIdAgeMap.get(age);

        //it will give the id..
    }
}



