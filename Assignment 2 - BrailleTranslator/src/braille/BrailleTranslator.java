package braille;

import java.util.ArrayList;

import javax.swing.tree.TreeCellRenderer;

/**
 * Contains methods to translate Braille to English and English to Braille using
 * a BST.
 * Reads encodings, adds characters, and traverses tree to find encodings.
 * 
 * @author Seth Kelley
 * @author Kal Pandit
 */
public class BrailleTranslator {

    private TreeNode treeRoot;

    /**
     * Default constructor, sets symbols to an empty ArrayList
     */
    public BrailleTranslator() {
        treeRoot = null;
    }

    /**
     * Reads encodings from an input file as follows:
     * - One line has the number of characters
     * - n lines with character (as char) and encoding (as string) space-separated
     * USE StdIn.readChar() to read character and StdIn.readLine() after reading
     * encoding
     * 
     * @param inputFile the input file name
     */
    public void createSymbolTree(String inputFile) {

        /* PROVIDED, DO NOT EDIT */

        StdIn.setFile(inputFile);
        int numberOfChars = Integer.parseInt(StdIn.readLine());
        for (int i = 0; i < numberOfChars; i++) {
            Symbol s = readSingleEncoding();
            addCharacter(s);
        }
    }

    /**
     * Reads one line from an input file and returns its corresponding
     * Symbol object
     * 
     * ONE line has a character and its encoding (space separated)
     * 
     * @return the symbol object
     */
    public Symbol readSingleEncoding() {
        // WRITE YOUR CODE HERE

        char character = StdIn.readChar(); // reading the character in each line
        String encoding = StdIn.readString(); // reading the encoding (RLRLRL...) in each line
        StdIn.readLine(); // ensuring that it gets the correct character/encoding
        
        Symbol symbolInfo = new Symbol(character, encoding);
        return symbolInfo;

        //return null; // Replace this line, it is provided so your code compiles
    }

    /**
     * Adds a character into the BST rooted at treeRoot.
     * Traces encoding path (0 = left, 1 = right), starting with an empty root.
     * Last digit of encoding indicates position (left or right) of character within
     * parent.
     * 
     * @param newSymbol the new symbol object to add
     */
    public void addCharacter(Symbol newSymbol) {
        // WRITE YOUR CODE HERE

        String fullEncoding = newSymbol.getEncoding();
        String partialEncoding = "";
        
        Symbol rootSymbol = new Symbol("");

        if (treeRoot == null) { // left and right are both null, 
            treeRoot = new TreeNode(rootSymbol, null, null);
        }

        TreeNode ptr = treeRoot; // now ptr not null anymore

        for (int i = 0; i < fullEncoding.length(); i++) {

            char digit = fullEncoding.charAt(i); // to get the R or L and check it
            partialEncoding += digit; // adding the R or L to the new encoding

            if (digit == 'L') {
                if (ptr.getLeft() == null) {
                    Symbol leftIntermediateSymbol = new Symbol(partialEncoding);
                    ptr.setLeft(new TreeNode(leftIntermediateSymbol, null, null));
                }
                ptr = ptr.getLeft();
            }

            else if (digit == 'R') {
                if (ptr.getRight() == null) {
                    Symbol rightIntermediateSymbol = new Symbol(partialEncoding);
                    ptr.setRight(new TreeNode(rightIntermediateSymbol, null, null));
                }
                ptr = ptr.getRight();
            }

        }

        Symbol leafSymbol = new Symbol(newSymbol.getCharacter(), fullEncoding);
        ptr.setSymbol(leafSymbol);


    }

    /**
     * Given a sequence of characters, traverse the tree based on the characters
     * to find the TreeNode it leads to
     * 
     * @param encoding Sequence of braille (Ls and Rs)
     * @return Returns the TreeNode of where the characters lead to, or null if there is no path
     */
    public TreeNode getSymbolNode(String encoding) {
        // WRITE YOUR CODE HERE

        TreeNode ptr = treeRoot;

        for (int i = 0; i < encoding.length(); i++) {

            char digit = encoding.charAt(i);

            if (digit == 'L') {
                if (ptr.getLeft() == null) {
                    return null;
                }
                ptr = ptr.getLeft();
            }
            else if (digit == 'R') {
                if (ptr.getRight() == null) {
                    return null;
                }
                ptr = ptr.getRight();
            }
        }

        return ptr;

        //return null; // Replace this line, it is provided so your code compiles
    }

    /**
     * Given a character to look for in the tree will return the encoding of the
     * character
     * 
     * @param character The character that is to be looked for in the tree
     * @return Returns the String encoding of the character
     */
    public String findBrailleEncoding(char character) {
        // WRITE YOUR CODE HERE

        TreeNode ptr = treeRoot;
        return preOrderHelper(ptr, character);
        
        //return null; // Replace this line, it is provided so your code compiles
    }

    // PRIVATE HELPER METHOD INCLUDED BELOW
    private String preOrderHelper(TreeNode ptr, char character) {
        
        if (ptr == null) { // BASE CASE
            return null;
        }
        
        if (ptr.getSymbol().getCharacter() == character) { // if the character is the target, returns the encoding
            return ptr.getSymbol().getEncoding();
        }
        
        if (preOrderHelper(ptr.getLeft(), character) != null) { // goes left until the ptr reaches the encoding
            return preOrderHelper(ptr.getLeft(), character);
        }

        return preOrderHelper(ptr.getRight(), character);

    }

    

    /**
     * Given a prefix to a Braille encoding, return an ArrayList of all encodings that start with
     * that prefix
     * 
     * @param start the prefix to search for
     * @return all Symbol nodes which have encodings starting with the given prefix
     */
    public ArrayList<Symbol> encodingsStartWith(String start) {
        // WRITE YOUR CODE HERE

        TreeNode startNode = getSymbolNode(start);

        ArrayList<Symbol> emptyArrayList = new ArrayList<>();
        ArrayList<Symbol> traversal = new ArrayList<>();

        if (startNode == null) {
            return emptyArrayList;
        }

        else {
            preOrderHelper2(startNode, traversal);
            return traversal;
        }

        //return null; // Replace this line, it is provided so your code compiles
    }


    // PRIVATE HELPER METHOD INCLUDED BELOW
    private void preOrderHelper2(TreeNode startNode, ArrayList<Symbol> list)  {

        if (startNode == null) { // BASE CASE
            return;
        }

        if (startNode.getLeft() == null && startNode.getRight() == null) {
            list.add(startNode.getSymbol());
        }

        preOrderHelper2(startNode.getLeft(), list);
        preOrderHelper2(startNode.getRight(), list);

    }

    /**
     * Reads an input file and processes encodings six chars at a time.
     * Then, calls getSymbolNode on each six char chunk to get the
     * character.
     * 
     * Return the result of all translations, as a String.
     * @param input the input file
     * @return the translated output of the Braille input
     */
    public String translateBraille(String input) {
        // WRITE YOUR CODE HERE

        StdIn.setFile(input);
        input = StdIn.readString();

        String partialEncoding = "";

        for (int i = 0; i < input.length(); i += 6) {
            TreeNode ptr = getSymbolNode(input.substring(i, i+6));
            if (ptr != null) { // ensures that all encodings exist to prevent null exception error for ptr
                partialEncoding += ptr.getSymbol().getCharacter();
            }
        }

        return partialEncoding;

        //return null; // Replace this line, it is provided so your code compiles
    }


    /**
     * Given a character, delete it from the tree and delete any encodings not
     * attached to a character (ie. no children).
     * 
     * @param symbol the symbol to delete
     */
    public void deleteSymbol(char symbol) {
        // WRITE YOUR CODE HERE

        String encoding = findBrailleEncoding(symbol);

        if (encoding == null) {
            return;
        }

        TreeNode target = treeRoot;
        TreeNode parent = null;


        for (int i = 0; i < encoding.length(); i++) {

            char digit = encoding.charAt(i);

            if (digit == 'L') {
                parent = target;
                target = target.getLeft();
            }

            else if (digit == 'R') {
                parent = target;
                target = target.getRight();
            }

        }


        if (parent.getLeft() == target) {
            parent.setLeft(null);
        }
        else if (parent.getRight() == target){
            parent.setRight(null);
        }
            

        for (int i = encoding.length() - 1; i > 0; i--) {

            String partialEncoding = encoding.substring(0, i);
            target = treeRoot; // ensures target is not null

            for (int j = 0; j < partialEncoding.length(); j++) {
                char digit = partialEncoding.charAt(j);
                if (digit == 'L') {
                    target = target.getLeft();
                }
                else if (digit == 'R'){
                    target = target.getRight();
                }
            }


            if (target.getLeft() == null && target.getRight() == null) { // if the target node has no children, it's a leaf node
                
                parent = treeRoot; // ensures parent is not null
                
                for (int k = 0; k < partialEncoding.length() - 1; k++) {
                    char digit = partialEncoding.charAt(k);
                    if (digit == 'L') {
                        parent = parent.getLeft();
                    }
                    else if (digit == 'R') {
                        parent = parent.getRight();
                    }
                }
                
                char digit = partialEncoding.charAt(partialEncoding.length() - 1);
     
                if (digit == 'L') {
                    parent.setLeft(null);
                }
                else if (digit == 'R') {
                    parent.setRight(null);
                }
                
            }
            
        }

    }



    public TreeNode getTreeRoot() {
        return this.treeRoot;
    }

    public void setTreeRoot(TreeNode treeRoot) {
        this.treeRoot = treeRoot;
    }

    public void printTree() {
        printTree(treeRoot, "", false, true);
    }

    private void printTree(TreeNode n, String indent, boolean isRight, boolean isRoot) {
        StdOut.print(indent);

        // Print out either a right connection or a left connection
        if (!isRoot)
            StdOut.print(isRight ? "|+R- " : "--L- ");

        // If we're at the root, we don't want a 1 or 0
        else
            StdOut.print("+--- ");

        if (n == null) {
            StdOut.println("null");
            return;
        }
        // If we have an associated character print it too
        if (n.getSymbol() != null && n.getSymbol().hasCharacter()) {
            StdOut.print(n.getSymbol().getCharacter() + " -> ");
            StdOut.print(n.getSymbol().getEncoding());
        }
        else if (n.getSymbol() != null) {
            StdOut.print(n.getSymbol().getEncoding() + " ");
            if (n.getSymbol().getEncoding().equals("")) {
                StdOut.print("\"\" ");
            }
        }
        StdOut.println();

        // If no more children we're done
        if (n.getSymbol() != null && n.getLeft() == null && n.getRight() == null)
            return;

        // Add to the indent based on whether we're branching left or right
        indent += isRight ? "|    " : "     ";

        printTree(n.getRight(), indent, true, false);
        printTree(n.getLeft(), indent, false, false);
    }

}
