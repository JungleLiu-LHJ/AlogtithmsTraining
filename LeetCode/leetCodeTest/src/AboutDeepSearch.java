import java.util.HashSet;

public class AboutDeepSearch {
    /**
     * 剑指 Offer 12. 矩阵中的路径
     * 请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一格开始，每一步可以在矩阵中向左、右、上、下移动一格。如果一条路径经过了矩阵的某一格，那么该路径不能再次进入该格子。例如，在下面的3×4的矩阵中包含一条字符串“bfce”的路径（路径中的字母用加粗标出）。
     */

    char[] chars;

    public boolean exist(char[][] board, String word) {
        chars = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[0].length; k++) {
                if (dfs(board, i, k, 0)) return true;
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, int j, int k, int i) {
        if (j < 0 || j >= board.length || k >= board[0].length || k < 0) {
            return false;
        }

        if (board[j][k] != chars[i]) {
            return false;
        }
        char a = board[j][k];
        if (board[j][k] == chars[i]) {
            i = i + 1;
            if (i == chars.length) return true;
        }
        board[j][k] = '\0';
        boolean res = dfs(board, j + 1, k, i) || dfs(board, j - 1, k, i)
                || dfs(board, j, k + 1, i) || dfs(board, j, k - 1, i);
        board[j][k] = a;
        return res;
    }


}
