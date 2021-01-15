import java.util.HashSet;

public class AboutDeepSearch {
    /**
     * 剑指 Offer 12. 矩阵中的路径
     * 请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一格开始，每一步可以在矩阵中向左、右、上、下移动一格。如果一条路径经过了矩阵的某一格，那么该路径不能再次进入该格子。例如，在下面的3×4的矩阵中包含一条字符串“bfce”的路径（路径中的字母用加粗标出）。
     */
    HashSet<int[]> set = new HashSet<>();

    public boolean exist(char[][] board, String word) {
        char[] chars = word.toCharArray();
        int[] x = {0, 0};

        for (int i = 0; i < chars.length; i++) {

        }

    }

    private int[] nextPoint(int[] p,char[][] board,char s){
       if(p[0]>= board.length || p[1]>= board[0].length) {
           return new int[]{-1, -1};
       }
       for(int i =-1..1){}



    }




}
