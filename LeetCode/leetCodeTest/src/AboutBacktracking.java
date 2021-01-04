import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AboutBacktracking {

    ArrayList<String> results = new ArrayList<>();
    public String[] permutation(String s) {
        char[] chas = s.toCharArray();
        ArrayList array = new ArrayList<Character>();
        for(int i = 0;i< chas.length;i++) {
            array.add(chas[i]);
        }
        backTracking(new StringBuilder(),array);
        String[] out = new String[results.size()];
        for(int i = 0;i< results.size();i++) {
            out[i] = results.get(i);
        }
        return out;
    }

    private void backTracking(StringBuilder s, ArrayList<Character> list) {
        if(list.size() == 0) {
            results.add(s.toString());
            return;
        }

        for (int i =0;i<list.size();i++) {

            if()

            backTracking(s.append(a),list);

        }
    }

    public static void main(String[] args) {

    }

}
