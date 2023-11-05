package Implementation.DataFile;

import Implementation.DBMS.Page;
import Implementation.DBMS.Record;

import java.util.*;

class BTreeNode {
    int threshHold;
    int[] keys;
    BTreeNode[] children;
    int noKeys; // number of keys
    int noChild; // number of children
    boolean leaf;

    static List<Page> pageList = new ArrayList<>();

    Page page;
    public static Map<Integer,Page> keyPageMap = new HashMap<>();

    static public int pageCount=1;

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
        children = new BTreeNode[threshHoldChildren()];
        noKeys = 0;// telling me how many keys are in this BtTreeNode.
        noChild = 0;
        this.page = new Page(pageCount);
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

    public BTreeNode search(int k) {
        System.out.println("IO CALL FOR CLUSTER INDEX");
        int i = 0;
        while (i < noKeys && k > keys[i])
            i++;

        if (i < noKeys && keys[i] == k)
            return this;

        if (leaf)
            return null;

        return children[i].search(k);
    }


    public void insertNonFull(Record record) {
        int lastKey = noKeys - 1;

        if (leaf) {
            /*
             * when it has no children -> leaf node.
             * current node is not full
             */
            while (lastKey >= 0 && record.id < keys[lastKey]) {
                keys[lastKey + 1] = keys[lastKey];
                lastKey--;
            }

            keys[lastKey + 1] = record.id;
            noKeys++;
            insertIntoPage(record);
        } else {
            /*
             * Not a leaf, when it has children first try to put element into children
             * current node is not full
             */
            while (lastKey >= 0 && record.id < keys[lastKey])
                lastKey--;

            lastKey++;
            if (children[lastKey].noKeys == threshHoldKey()) {
                splitChild(lastKey, children[lastKey]);
                if (keys[lastKey] < record.id)
                    lastKey++;
            }
            children[lastKey].insertNonFull(record);// current node's children...  recursively.
        }
    }

    private BTreeNode makeRightChild(BTreeNode fullNode ){
        BTreeNode rightChild = new BTreeNode(fullNode.threshHold, fullNode.leaf);
        rightChild.noKeys = threshHold - 1;
        rightChild.page = fullNode.page;

        for(int j = 0; j < threshHold - 1; j++)
            rightChild.keys[j] = fullNode.keys[j + threshHold];

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
        fullNode.keys[threshHold - 1]=-1;
        noKeys++;
    }

    void splitChild(int idOfParentChild, BTreeNode fullNode) {
        BTreeNode rightChild = makeRightChild(fullNode);
        addToParentRigthChild(idOfParentChild,rightChild);
        addMidKeyToParent(idOfParentChild,fullNode);
    }


    public void insertIntoPage(Record record){
        int check = this.page.insert(record);
        BTreeNode.keyPageMap.put(record.id,page);// we need to update it as well.
        if(check == -1){
            /*
             * if other index's has only id for this bt -tree then no need to update that
             * trade of between time and space complexit
             * If : while page spliting time reduce, then do make pointer from all other indexes to id's of cluster index bt tree.
             * otherwise direct record.
             */
            pageCount++;
            Page page2 = this.page.split(pageCount);
            pageList.add(page2);
            this.page.insert(record);
            Set<Integer> idPage2 = page2.getListOfRecords();
            updatePageOnTree(idPage2, page2);

            // all are already page1 so update to page2 whereever possible.
        }
        System.out.println("------- Bt tree updated ----- ");
        for(Page page : pageList){
            System.out.println("Size of "+ page.PAGE_ID + "-> "+ page.currentSize);
        }
        System.out.println("------- -----  ----- ");
    }

    // Linear complexity. page splitting.
    public void updatePageOnTree( Set<Integer> id, Page updatedPage) {
        int i;
        for (i = 0; i < noKeys; i++) {
            if (!leaf)
                children[i].updatePageOnTree(id,updatedPage);
            if(id.contains(keys[i])){
                System.out.println("Page of id: "+ keys[i]+ " Updated to -> "+ updatedPage.PAGE_ID);
                page = updatedPage;
                keyPageMap.put(keys[i],page);// for printing.
            }
        }

        if (!leaf)
            children[i].updatePageOnTree(id,page);
    }

}
class BTree {
    private BTreeNode root;
    private final int threshHold;


    public void makeNewKeyRoot(Record record){
        root = new BTreeNode(threshHold, true);
        root.keys[0] = record.id;
        root.noKeys = 1;
        root.page.insert(record);
        BTreeNode.pageList.add(root.page);
        BTreeNode.keyPageMap.put(record.id,root.page);
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
        return (root == null) ? null : root.search(k);
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
                newRoot.page = root.page;
                newRoot.splitChild(0, root);
                int i = 0;
                if (newRoot.keys[0] < record.id)
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
                    System.out.print("Key: "+ cur.keys[j]+" Page: "+ BTreeNode.keyPageMap.get(cur.keys[j]).PAGE_ID+ "  | ");
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

public class ClusterIndex{

    static int THRESHOLD ;
    static BTree tree ;
    public ClusterIndex(int threshold){
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

    public static Record search(int id) {// Find with index id.
        return getTree().search(id).page.find(id);
    }
}

