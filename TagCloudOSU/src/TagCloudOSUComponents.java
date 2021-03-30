import java.util.Comparator;
import java.util.Iterator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine3;
import components.utilities.FormatChecker;

/**
 * Put a short phrase describing the program here.
 *
 * @author Abigail Homison
 * @author Bobby Spech
 *
 */
public final class TagCloudOSUComponents {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudOSUComponents() {
    }

    private static class keyLT implements Comparator<Pair<String, Integer>> {
        @Override
        public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
            String o1 = p1.key();
            String o2 = p2.key();
            int check = o1.compareTo(o2);
            if (check == 0) {
                check = p1.value().compareTo(p2.value());
            }
            return check;
        }
    }

    //create the comparator for the SortingMachine
    private static class valueLT implements Comparator<Pair<String, Integer>> {
        @Override
        public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
            return p2.value().compareTo(p1.value());
        }
    }

    /**
     * Populates the given html file with a header
     *
     * @param out
     *            {@code SimpleWriter} object in which to add a header
     * @param title
     *            the name of the input file to add to the title
     * @requires out.isOpen
     * @ensures the out file is populated with an html header and <title>
     *          element is populated with the given string
     */
    private static void createHeader(SimpleWriter out, String fileName,
            int num) {
        assert out.isOpen() : "Violation of out.isOpen";

        //print an html header into out
        out.println("<html>");
        out.println("<head>\n<title>Top " + num + " Words in: " + fileName
                + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>\n<body>");
    }

    /**
     * Populates the given html file with the meat of the sorted map
     *
     * @param out
     *            {@code SimpleWriter} object in which to add a header
     * @param sortApha
     * @param name
     * @param number
     * @requires out.isOpen
     * @ensures the out file is populated with an html header and <title>
     *          element is populated with the given string
     */
    private static void createBody(SimpleWriter out,
            SortingMachine<Pair<String, Integer>> words, String name,
            Map<String, Integer> number, Queue<Integer> minAndMax) {
        assert out.isOpen() : "Violation of out.isOpen";

        out.println("<h2>Top " + words.size() + " Words in " + name + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");

        while (words.size() > 0) {
            Pair<String, Integer> next = words.removeFirst();
            String font = getFontSize(minAndMax, next.value());
            // figure out font
            out.println("<span style=\"cursor:default\" class=\"" + font
                    + "\" title=\"count: " + next.value() + "\">" + next.key()
                    + "</span>");
        }
        out.println("</p>");
        out.println("</div>");

    }

    public static String getFontSize(Queue<Integer> minAndMax, int count) {

        //according to the css sheet valid fonts are sizes 11 - 48
        final int maxFont = 48;
        final int minFont = 11;

        //get the min and max counts for scaling font sizes
        int maxCount = minAndMax.front();
        minAndMax.rotate(1);
        int minCount = minAndMax.front();
        minAndMax.rotate(1);

        int font = (((maxFont - minFont) * (count - minCount))
                / (maxCount - minCount)) + minFont;

        String fontString = "f" + font;
        return fontString;
    }

    /**
     * Populates the given {@code SimpleWriter} file with the footer code
     *
     * @param out
     *            {@code SimpleWriter} object in which to add the footer
     * @requires out.isOpen
     * @ensures the {@code SimpleWriter} file is appended with closing tags for
     *          <body> and <html>.
     */
    private static void createFooter(SimpleWriter out) {
        assert out.isOpen() : "Violation of out.isOpen";

        out.println("</body>\n</html>");
    }

    /**
     * characters to separate.
     */
    private static final String separators = " \t\n\r,-.!?[]';:/()";

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code SEPARATORS}) or "separator string" (maximal length string of
     * characters in {@code SEPARATORS}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection entries(SEPARATORS) = {}
     * then
     *   entries(nextWordOrSeparator) intersection entries(SEPARATORS) = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection entries(SEPARATORS) /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of entries(SEPARATORS)  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of entries(SEPARATORS))
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int j = position;
        int endJ = position;
        boolean switcher = true;
        // keeps track of if its a separator or if its a character
        boolean sep = false;
        if (separators.indexOf(text.charAt(j)) > -1) {
            sep = true;
        }
        endJ++;
        while (switcher && endJ < text.length()) {
            if ((!sep && !((separators.indexOf(text.charAt(endJ))) > -1))
                    || (sep && ((separators
                            .indexOf(text.charAt(endJ))) > -1))) {
                endJ++;
            } else {
                switcher = false;
            }
        }
        String ret = text.substring(j, endJ);
        return ret;

    }

    /**
     * Collects every word in the given input file placing them into a Map as
     * well as a SortingMachine
     *
     * @param in
     *            the inputStream.
     * @return the map of words.
     * @requires in is open and sort is empty and in insertion mode
     * @ensures the file will be completely read and a map will be returned.
     *
     */
    public static Map<String, Integer> getWords(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";

        Map<String, Integer> words = new Map1L<>();
        int pos = 0;
        while (!in.atEOS()) {
            pos = 0;
            String line = in.nextLine();
            while (pos < line.length()) {
                String oldCheck = nextWordOrSeparator(line, pos);
                String check = oldCheck.toLowerCase();
                if (separators.indexOf(check.charAt(0)) <= -1) {
                    if (words.hasKey(check)) {
                        words.replaceValue(check, words.value(check) + 1);
                    } else {
                        words.add(check, 1);
                    }
                }
                pos += check.length();
            }
        }

        return words;
    }

    public static SortingMachine<Pair<String, Integer>> getTopWords(
            Map<String, Integer> words,
            SortingMachine<Pair<String, Integer>> sort, int num,
            Queue<Integer> minAndMax, Comparator<Pair<String, Integer>> order) {

        Iterator<Pair<String, Integer>> it = words.iterator();
        while (it.hasNext()) {
            sort.add(it.next());
        }
        sort.changeToExtractionMode();

        SortingMachine<Pair<String, Integer>> topWords = new SortingMachine3<>(
                order);

        //instantiate needed variables for the while loop
        int i = 1;
        int size = sort.size();

        //get the min
        Pair<String, Integer> biggest = sort.removeFirst();

        //add the min to the minAndMax queue
        minAndMax.enqueue(biggest.value());

        topWords.add(biggest);

        while (i < num - 1 && i < size - 1) {
            topWords.add(sort.removeFirst());
            i++;
        }

        //get the max and add it to the queue
        Pair<String, Integer> smallest = sort.removeFirst();
        minAndMax.enqueue(smallest.value());

        topWords.add(smallest);

        return topWords;
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
        /*
         * Put your main program code here
         */
        out.print("Enter a text file: ");
        String inFile = in.nextLine();

        SimpleReader file = new SimpleReader1L(inFile);

        out.print("Enter a file name for output: ");
        String outFile = in.nextLine();
        SimpleWriter oFile = new SimpleWriter1L(outFile);

        out.print("Enter a number for the number of words to be generated: ");

        String numString = in.nextLine();
        boolean valid = false;
        int num = 0;
        while (!valid) {
            if (FormatChecker.canParseInt(numString)) {
                int j = Integer.parseInt(numString);
                if (j > -1) {
                    valid = true;
                    num = j;
                } else {
                    out.println("Invalid Integer");
                    out.print(
                            "Enter a number for the number of words to be generated: ");
                    numString = in.nextLine();
                }
            } else {
                out.println("Please input an Integer");
                out.print(
                        "Enter a number for the number of words to be generated: ");
                numString = in.nextLine();
            }
        }

        Map<String, Integer> words = getWords(file);
        file.close();

        Comparator<Pair<String, Integer>> countOrder = new valueLT();
        SortingMachine<Pair<String, Integer>> sortByCount = new SortingMachine3<>(
                countOrder);

        //make a new queue to hold the min and max count values
        Queue<Integer> minAndMax = new Queue1L<>();

        Comparator<Pair<String, Integer>> alphabeticalOrder = new keyLT();

        //fill the sortingMachine, sort, and extract the top number of words
        SortingMachine<Pair<String, Integer>> sortAlphabetically = getTopWords(
                words, sortByCount, num, minAndMax, alphabeticalOrder);

        //now that we have all of the entries in the sortingMachine, change it to extraction mode
        sortAlphabetically.changeToExtractionMode();

        //start filling in the html file
        createHeader(oFile, inFile, num);

        createBody(oFile, sortAlphabetically, inFile, words, minAndMax);

        //finish the html file
        createFooter(oFile);
        /*
         * Close input and output streams
         */
        oFile.close();
        in.close();
        out.close();
    }

}
