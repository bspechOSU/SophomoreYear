import components.binarytree.BinaryTree;
import components.map.Map;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program will output html files reflecting the input words and
 * definitions.
 *
 * @author bobby spech
 *
 */
public final class GlossaryProject {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private GlossaryProject() {
    }

    /**
     *
     * @param fileName
     *            Name of the file to access
     * @param defMap
     *            map required
     * @requires fileName is valid and has inputs
     * @ensures defMap will be filled with the terms and definitions
     */
    public static void getDefMap(String fileName, Map<String, String> defMap) {
        SimpleReader in = new SimpleReader1L(fileName);
        String word = "";
        String def = "";
        int track = 0;
        // creates variables

        while (!in.atEOS()) {
            // continues until the document is empty
            String x = in.nextLine();
            if (x.length() > 0 && track == 0) {
                // first part is the term
                word = x;
                track = 1;
            } else if (x.length() > 0) {
                // second part and other parts are definition
                def = def + x;
            } else if (x.length() == 0) {
                // if the line is empty the word and def are complete
                defMap.add(word, def);
                // adds them
                track = 0;
                word = "";
                def = "";
                // resets stuff
            }
            if (in.atEOS()) {
                defMap.add(word, def);
            }
            //adds the last word and definition

        }

        in.close();

    }

    /**
     * Puts the words in an array in alphabetical order.
     *
     * @param given
     *            the inputted file map
     * @return array with terms in alphabetical order
     * @requires map of type <String, String>
     * @ensures array is in alphabetical order
     */
    public static String[] alphabeticalOrder(Map<String, String> given) {
        String[] ret = new String[given.size()];
        Map<String, String> rep = given.newInstance();
        int arrayTrack = 0;
        // makes all the variables needed
        while (given.size() > 0) {
            // goes through given
            Map.Pair<String, String> holder = given.removeAny();
            String key = holder.key();
            ret[arrayTrack] = key;
            // adds the key to the array
            arrayTrack++;
            rep.add(holder.key(), holder.value());
            // adds the set of key and value to the
        }

        for (int i = 0; i < ret.length; i++) {
            // organizes the array into alphabetical order
            for (int j = i + 1; j < ret.length; j++) {
                int q = ret[i].compareTo(ret[j]);
                if (q > 0) {
                    String hold = ret[i];
                    String holdTwo = ret[j];
                    ret[i] = holdTwo;
                    ret[j] = hold;
                }
            }
        }
        given.transferFrom(rep);
        // gives the values back to the map
        return ret;
    }

    /**
     * Returns the size of the given {@code BinaryTree<T>}.
     *
     * @param <T>
     *            the type of the {@code BinaryTree} node labels
     * @param t
     *            the {@code BinaryTree} whose size to return
     * @return the size of the given {@code BinaryTree}
     * @ensures size = |t|
     */
    public static <T> int size(BinaryTree<T> t) {
        int size = 0;
        /**
         * if (!(t.size() > 0)) { BinaryTree<T> left = t.newInstance();
         * BinaryTree<T> right = t.newInstance(); T root = t.disassemble(left,
         * right); size++; size = size + size(left); size = size + size(right);
         * t.assemble(root, left, right); }
         */

        for (T s : t) {
            size++;
        }
        return size;
    }

    /**
     *
     * @param originalFile
     *            The original text name
     * @param term
     *            Term given
     * @param htmlForm
     *            html version of the term
     * @param impMap
     *            map of all terms and def
     * @param folderN
     *            folder name given
     * @requires originalFile exists,impMap is not empty, folder exists
     * @ensures the creation of an html file
     */
    public static void individualPage(String originalFile, String term,
            String htmlForm, Map<String, String> impMap, String folderN) {
        SimpleWriter outNew = new SimpleWriter1L(folderN + "/" + htmlForm);
        // header in italic and red
        outNew.println("<title>" + term + "</title>");
        outNew.println("<i><h1 style =color:red;>" + term + "</h1></i>");
        // writes the term header
        final String separatorStr = "\t, ?!;.";
        // separators
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorStr, separatorSet);
        // gets the set of separators
        String def = impMap.value(term);
        // acquire the Definition form the term
        outNew.println("\t");
        outNew.print("<p>");
        int tracker = 0;
        while (tracker < def.length()) {
            String next = nextWordOrSeparator(def, tracker, separatorSet);
            if (impMap.hasKey(next)) {
                // links if the value exists
                outNew.print("<a href =" + next + ".html>" + next + "</a>");
            } else {
                outNew.print(next);
                // just prints the term

            }
            tracker = tracker + next.length();
        }
        outNew.println("</p>");
        outNew.println();
        outNew.println("<hr>");
        // prints the little line

        outNew.println(
                "Return to <a href = " + originalFile + ">index" + "</a>.");
        //return print statement

        outNew.close();
        // closes the stream
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int j = position;
        String ret = "";
        boolean switcher = true;
        // keeps track of if its a separator or if its a character
        boolean sep = false;
        if (separators.contains(text.charAt(j))) {
            sep = true;
        }
        ret = ret + text.charAt(j);
        j++;
        while (switcher && j < text.length()) {
            // runs through the hole piece until it hits a separator or a character
            char check = text.charAt(j);
            if (sep && separators.contains(check)) {
                ret = ret + check;
                // if it matches a separator it gets added to the return string
            } else if (!sep && !separators.contains(check)) {
                // if it matches with characters it gets added to the return string
                ret = ret + check;
            } else {
                // if it doesn't match the previous stuff, it doesn't get added
                switcher = false;
            }
            j++;
        }

        return ret;

    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        strSet.clear();
        // clears the set
        for (int i = 0; i < str.length(); i++) {
            // adds characters without repeats
            char check = str.charAt(i);
            if (!strSet.contains(check)) {
                strSet.add(check);
            }
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        double j = Math.random() * 64;
        int q = (int) j;
//        while (q != 1) {
//            out.println(q);
//            j = Math.random() * 68;
//            q = (int) j;
//        }
        out.println(q);
        /*
         * Put your main program code here; it may call myMethod as shown
         */
//        final int four = 4;
//        boolean validInput = false;
//        String fileName = "";
//        while (!validInput) {
//            out.print("Enter a file name with definitions: ");
//            fileName = in.nextLine();
//            if (fileName.length() > four && fileName
//                    .substring(fileName.indexOf(".")).equals(".txt")) {
//                // ensures .txt is part of the file name
//                validInput = true;
//            } else {
//                out.println("Invalid, ensure file name ends in .txt");
//            }
//        }
//        Map<String, String> defintMap = new Map1L<>();
//        getDefMap(fileName, defintMap);
//
//        //Folder Saving Stuff
//        String folderName = "";
//
//        out.print("Enter a name for the folder to save the files: ");
//        folderName = in.nextLine();
//        SimpleWriter outFile = new SimpleWriter1L(folderName + "/index.html");
//
//        String[] ordered = alphabeticalOrder(defintMap);
//
//        outFile.println("<html> <head> <title>");
//        outFile.print("Glossary </title>");
//        // gets  the base layer of html
//
//        outFile.println("<h1> Index </h1>");
//        // prints out index as the heading
//        outFile.println("<ul>");
//        for (int k = 0; k < ordered.length; k++) {
//            // runs through the ordered array and prints them in a list
//            String htmlAdder = ordered[k] + ".html";
//            individualPage("index.html", ordered[k], htmlAdder, defintMap,
//                    folderName);
//            outFile.print("<li>");
//            outFile.print("<a href =" + htmlAdder + ">" + ordered[k] + " </a>");
//            // links them to the appropriate page
//            outFile.println("</li>");
//        }
//        // print out the list of items and link them to their own page
//        outFile.println("</ul>");
//
//        /*
//         * Close input and output streams
//         */
        in.close();
        out.close();
        //outFile.close();
    }

}
