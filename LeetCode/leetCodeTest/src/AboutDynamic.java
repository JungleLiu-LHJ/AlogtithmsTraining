import java.util.Arrays;

public class AboutDynamic {

    /**
     * 剑指 Offer 42. 连续子数组的最大和
     * 输入一个整型数组，数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
     * <p>
     * 要求时间复杂度为O(n)。
     *
     * @param nums
     * @return
     */
    public int maxSubArray(int[] nums) {
        int[] array = new int[nums.length + 1];
        array[0] = 0;
        int out = Integer.MIN_VALUE;
        for (int i = 1; i <= nums.length; i++) {
            array[i] = Math.max(array[i - 1] + nums[i - 1], nums[i - 1]);
            out = Math.max(out, array[i]);
        }
        return out;
    }


    /**
     * 剑指 Offer 10- II. 青蛙跳台阶问题
     * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
     * <p>
     * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
     *
     * @param n
     * @return
     */
    public int numWays(int n) {
        if (n == 0) return 1;
        int[] l = new int[n + 1];
        l[0] = 1;
        for (int i = 0; i < n; i++) {
            l[i + 1] = l[i] + l[i + 1];
            if (i + 2 <= n) l[i + 2] = l[i] + l[i + 2];
        }
        return l[n] % 1000000007;
    }

    /**
     * 198. 打家劫舍
     * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
     *
     * 给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
     * @param nums
     * @return
     */
    public int rob(int[] nums) {
        if(nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        if(nums.length>1) {
            dp[1] = Math.max(dp[0],nums[1]);
        }

        for(int i = 2;i<nums.length;i++) {
            dp[i] = Math.max(dp[i-1],dp[i-2]+nums[i]);
        }
        return dp[nums.length-1];
    }

    /**
     * 322. 零钱兑换
     * 给你一个整数数组 coins ，表示不同面额的硬币；以及一个整数 amount ，表示总金额。
     * <p>
     * 计算并返回可以凑成总金额所需的 最少的硬币个数 。如果没有任何一种硬币组合能组成总金额，返回 -1 。
     * <p>
     * 你可以认为每种硬币的数量是无限的。
     *
     * @param coins
     * @param amount
     * @return
     */
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 0; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] < Integer.MAX_VALUE && i + coins[j] <= amount) {
                    //注意这里integer容易溢出
                    dp[i + coins[j]] = Math.min(dp[i + coins[j]], dp[i] + 1);
                }
            }
        }
        return dp[amount] == amount + 1 ? -1 : dp[amount];
    }

    /**
     * 518. 零钱兑换 II
     * 给你一个整数数组 coins 表示不同面额的硬币，另给一个整数 amount 表示总金额。
     * <p>
     * 请你计算并返回可以凑成总金额的硬币组合数。如果任何硬币组合都无法凑出总金额，返回 0 。
     * <p>
     * 假设每一种面额的硬币有无限个。
     * <p>
     * 题目数据保证结果符合 32 位带符号整数。
     *
     * @param amount
     * @param coins
     * @return
     */
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int j = 0; j < coins.length; j++) {
            for (int i = 0; i <= amount; i++) {
                if (i + coins[j] <= amount) {
                    dp[i + coins[j]] = dp[i]+ dp[i + coins[j]];
                }
            }
        }
        return dp[amount];
    }

    /**
     * 剑指 Offer II 095. 最长公共子序列
     * 给定两个字符串 text1 和 text2，返回这两个字符串的最长 公共子序列 的长度。如果不存在 公共子序列 ，返回 0 。
     * <p>
     * 一个字符串的 子序列 是指这样一个新的字符串：它是由原字符串在不改变字符的相对顺序的情况下删除某些字符（也可以不删除任何字符）后组成的新字符串。
     * <p>
     * 例如，"ace" 是 "abcde" 的子序列，但 "aec" 不是 "abcde" 的子序列。
     * 两个字符串的 公共子序列 是这两个字符串所共同拥有的子序列。
     *
     * @param text1
     * @param text2
     * @return
     */
    public int longestCommonSubsequence(String text1, String text2) {
        char[] s1 = text1.toCharArray();
        char[] s2 = text2.toCharArray();

        int[][] dp = new int[s1.length + 1][s2.length + 1];

        for (int i = 1; i <= s1.length; i++) {
            for (int j = 1; j <= s2.length; j++) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i][j - 1], dp[i - 1][j]);
                }
            }
        }

        return dp[s1.length][s2.length];

    }

    /**
     * 剑指 Offer 14- I. 剪绳子
     * 给你一根长度为 n 的绳子，请把绳子剪成整数长度的 m 段（m、n都是整数，n>1并且m>1），每段绳子的长度记为 k[0],k[1]...k[m-1] 。
     * 请问 k[0]*k[1]*...*k[m-1] 可能的最大乘积是多少？例如，当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，此时得到的最大乘积是18。
     * 有两种方法，动态规划和贪心算法，贪心算法建立在数学推导上，复杂度很低。
     *
     * @param n
     * @return
     */
    public int cuttingRope(int n) {
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 1;
        for (int i = 3; i < n + 1; i++) {

            for (int j = 2; j <= i; j++) {
                dp[i] = Math.max(dp[i], Math.max((i - j) * dp[j], (i - j) * j));
            }

        }
        return dp[n];
    }

    /**
     * 使用贪心算法来做，根据数学推导，接近e的乘机是最大的，那就是3或者2
     *
     * @param n
     * @return
     */
    public int cuttingRope2(int n) {
        if (n < 4) {
            return n - 1;
        }
        if (n == 4) {
            return n;
        }
        int s = 1;
        while (n > 4) {
            n = n - 3;
            s = s * 3;
        }
        return s * n;
    }


    /**
     * 这其实是回溯的题，可以用dfs或者bfs来解决
     * 剑指 Offer 13. 机器人的运动范围
     * 地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，它每次可以向左、
     * 右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进
     * 入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？
     *
     * @param m
     * @param n
     * @param k
     * @return
     */
    int m, n, k;
    boolean[][] visited;

    public int movingCount(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        visited = new boolean[m][n];
        return dfs(0, 0);

    }

    private int sums(int x) {
        int s = 0;
        while (x != 0) {
            s = x % 10 + s;
            x = x / 10;
        }
        return s;
    }

    private int dfs(int x, int y) {
        if (x > m || y > n || sums(x) + sums(y) > k || visited[x][y]) {
            return 0;
        }
        visited[x][y] = true;
        return dfs(x + 1, y) + dfs(x, y + 1) + 1;

    }


}
