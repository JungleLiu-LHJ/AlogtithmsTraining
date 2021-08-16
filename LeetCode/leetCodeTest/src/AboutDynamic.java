public class AboutDynamic {

    /**
     * 剑指 Offer 42. 连续子数组的最大和
     * 输入一个整型数组，数组中的一个或连续多个整数组成一个子数组。求所有子数组的和的最大值。
     *
     * 要求时间复杂度为O(n)。
     * @param nums
     * @return
     */
    public int maxSubArray(int[] nums) {
        int[] array = new int[nums.length+1];
        array[0] =0;
        int out = Integer.MIN_VALUE;
        for(int i=1;i<= nums.length;i++) {
            array[i] =  Math.max(array[i-1]+nums[i-1],nums[i-1]) ;
            out = Math.max(out, array[i]);
        }
        return out;
    }


    /**
     * 剑指 Offer 10- II. 青蛙跳台阶问题
     * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
     *
     * 答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。
     * @param n
     * @return
     */
    public int numWays(int n) {
        if(n==0) return 1;
        int[] l = new int[n+1];
        l[0] =1;
        for (int i=0;i<n;i++) {
            l[i+1] = l[i]+l[i+1] ;
            if(i+2<=n) l[i+2] = l[i]+l[i+2];
        }
        return l[n]%1000000007;
    }

    /**
     * 这其实是回溯的题，可以用dfs或者bfs来解决
     * 剑指 Offer 13. 机器人的运动范围
     * 地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，它每次可以向左、
     * 右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进
     * 入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？
     * @param m
     * @param n
     * @param k
     * @return
     */
    int m,n,k;
    boolean[][] visited;
    public int movingCount(int m, int n, int k) {
        this.m=m;
        this.n=n;
        this.k = k;
        visited = new boolean[m][n];
        return dfs(0,0);

    }

    private int sums(int x) {
        int s = 0;
        while (x!=0) {
            s = x%10+s;
            x=x/10;
        }
        return s;
    }

    private int dfs(int x,int y) {
        if(x>m||y>n||sums(x)+sums(y)>k||visited[x][y]) {
            return 0;
        }
        visited[x][y] = true;


        return dfs(x+1,y)+dfs(x,y+1)+1;


    }


}
