import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AboutSlidingWindow {


    public static void main(String[] args) {

        //minWindow("ADOBECODEBANC", "ABC");


        System.out.println(findAnagrams(s, t).toString());
    }

    /**
     * 76. 最小覆盖子串
     * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
     *
     * @param s
     * @param t
     * @return
     */
    public static String minWindow(String s, String t) {
        HashMap<Character, Integer> need = new HashMap<>(), window = new HashMap<>();
        char[] ss = s.toCharArray();
        char[] tt = t.toCharArray();

        for (char c : tt) {
            if (need.containsKey(c)) {
                need.put(c, need.get(c) + 1);
            } else {
                need.put(c, 1);
            }
        }

        int right = 0, left = 0;

        int valid = 0, len = Integer.MAX_VALUE;
        int[] result = {0, 0};
        while (right < ss.length) {

            if (need.containsKey(ss[right])) {
                if (window.containsKey(ss[right])) {
                    window.put(ss[right], window.get(ss[right]) + 1);
                } else {
                    window.put(ss[right], 1);
                }
                if (window.get(ss[right]).equals(need.get(ss[right]))) {
                    valid++;
                }
            }
            right++;
            while (valid == need.size()) {

                if (right - left < len) {
                    len = right - left;
                    result[0] = left;
                    result[1] = right;
                }

                if (need.containsKey(ss[left])) {
                    if (window.get(ss[left]) == need.get(ss[left])) {
                        valid--;
                    }
                    window.put(ss[left], window.get(ss[left]) - 1);
                }

                left++;
            }

        }

        return len == Integer.MAX_VALUE ? "" : String.copyValueOf(ss, result[0], len);
    }


    /**
     * 438. 找到字符串中所有字母异位词
     * 给定两个字符串 s 和 p，找到 s 中所有 p 的 异位词 的子串，返回这些子串的起始索引。不考虑答案输出的顺序。
     * <p>
     * 异位词 指字母相同，但排列不同的字符串。
     *
     * @param s
     * @param p
     * @return
     */
    public static List<Integer> findAnagrams(String s, String p) {

        HashMap<Character, Integer> need = new HashMap<>(), window = new HashMap<>();
        char[] ss = s.toCharArray();
        char[] tt = p.toCharArray();

        if (ss.length < tt.length) {
            return new ArrayList<>();
        }

        for (char c : tt) {
            if (need.containsKey(c)) {
                need.put(c, need.get(c) + 1);
            } else {
                need.put(c, 1);
            }
        }
        int valid = 0;
        for (int i = 0; i < tt.length; i++) { //初始的窗口做判断
            if (need.containsKey(ss[i])) {
                if (window.containsKey(ss[i])) {
                    window.put(ss[i], window.get(ss[i]) + 1);
                } else {
                    window.put(ss[i], 1);
                }
                if (window.get(ss[i]).equals(need.get(ss[i]))) {
                    valid++;
                }
            }
        }

        int left = 0;
        int right = left + tt.length;

        List<Integer> result = new ArrayList<Integer>();

        if (valid == need.size()) {//如果初始的时候也满足则把0加进去
            result.add(left);
        }
        while (right < ss.length) {
            char c = ss[left];
            left++;
            if (need.containsKey(c)) {//left往右一格，记得这里先去除再-1
                if (window.get(c).equals(need.get(c))) valid = valid <= 0 ? 0 : valid - 1;
                window.put(c, window.get(c) - 1);
            }

            char a = ss[right];
            right++;
            if (need.containsKey(a)) {//right往右一格
                if (window.containsKey(a)) {
                    window.put(a, window.get(a) + 1);
                } else {
                    window.put(a, 1);
                }
                if (window.get(a).equals(need.get(a))) {
                    valid++;
                }
            }

            if (valid == need.size()) {
                result.add(left);
            }
        }

        return result;
    }


    static String s = "vwwvv";
    static String t = "vwv";


    /**
     * 567. 字符串的排列
     * 给你两个字符串 s1 和 s2 ，写一个函数来判断 s2 是否包含 s1 的排列。
     * <p>
     * 换句话说，s1 的排列之一是 s2 的 子串 。
     *
     * @param s1
     * @param s2
     * @return
     */
    public boolean checkInclusion(String s1, String s2) {
        HashMap<Character, Integer> need = new HashMap<>(), window = new HashMap<>();
        char[] ss = s2.toCharArray();
        char[] tt = s1.toCharArray();

        if (ss.length < tt.length) {
            return false;
        }

        for (char c : tt) {
            if (need.containsKey(c)) {
                need.put(c, need.get(c) + 1);
            } else {
                need.put(c, 1);
            }
        }
        int valid = 0;
        for (int i = 0; i < tt.length; i++) {
            if (need.containsKey(ss[i])) {
                if (window.containsKey(ss[i])) {
                    window.put(ss[i], window.get(ss[i]) + 1);
                } else {
                    window.put(ss[i], 1);
                }
                if (window.get(ss[i]).equals(need.get(ss[i]))) {
                    valid++;
                }
            }
        }

        int left = 0;
        int right = left + tt.length;

        if (valid == need.size()) {
            return true;
        }
        while (right < ss.length) {
            char c = ss[left];
            left++;
            if (need.containsKey(c)) {//left往右一格，记得这里先去除再-1
                if (window.get(c).equals(need.get(c))) valid = valid <= 0 ? 0 : valid - 1;
                window.put(c, window.get(c) - 1);
            }

            char a = ss[right];
            right++;
            if (need.containsKey(a)) {//right往右一格
                if (window.containsKey(a)) {
                    window.put(a, window.get(a) + 1);
                } else {
                    window.put(a, 1);
                }
                if (window.get(a).equals(need.get(a))) {
                    valid++;
                }
            }

            if (valid == need.size()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 3. 无重复字符的最长子串
     * 给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
     *
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        HashSet<Character> set = new HashSet<>();
        char[] ss = s.toCharArray();

        int right = 0, left = 0;

        int result = 0;
        while (right < ss.length) {
            char c = ss[right];
            right++;

            while (set.contains(c)) {
                char temp = ss[left];
                left++;
                set.remove(temp);
            }

            set.add(c);
            result = Math.max(result,right-left);
        }

        return result;
    }

}
