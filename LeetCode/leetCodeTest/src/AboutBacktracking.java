import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AboutBacktracking {

    ArrayList<List> results = new ArrayList<>();

    public String[] permutation(String s) {
        char[] chas = s.toCharArray();
        backTracking(chas, new LinkedList<>());
        String[] out = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            StringBuilder temp = new StringBuilder();
            System.out.println("size = "+results.get(i).size());
            for (int j = 0; j < results.get(i).size(); j++) {
                temp.append(results.get(i).get(j));
            }
            out[i] = temp.toString();
        }
        return out;
    }

    private void backTracking(char[] s, LinkedList<Character> list) {
        if (list.size() == s.length) {
            System.out.println(" out" + list);
            results.add((List) list.clone());
            return;
        }

        for (int i = 0; i < s.length; i++) {
            if (list.contains(s[i])) {
                continue;
            }
            list.add(s[i]);
            backTracking(s, list);
            list.removeLast();
        }
    }

    public static void main(String[] args) {
        new AboutBacktracking().permutation("abc");
    }

}
