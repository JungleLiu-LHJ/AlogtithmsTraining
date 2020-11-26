import java.util.*;

import static java.lang.Integer.max;

public class AboutTree {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /**
     * 剑指 Offer 55 - I. 二叉树的深度
     *
     * @param root
     * @return 深度
     */
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    /**
     * 剑指 Offer 27. 二叉树的镜像
     * 请完成一个函数，输入一个二叉树，该函数输出它的镜像。
     */
    public TreeNode mirrorTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        mirrorTree(root.right);
        mirrorTree(root.left);
        return root;
    }

    /**
     * 剑指 Offer 54. 二叉搜索树的第k大节点
     * 给定一棵二叉搜索树，请找出其中第k大的节点。
     */
    int k, value;

    public int kthLargest(TreeNode root, int k) {
        this.k = k;
        dfs(root);
        return value;
    }

    private void dfs(TreeNode root) {
        if (root == null) {
            return;
        }
        dfs(root.right);
        this.k--;
        if (k == 0) value = root.val;
        dfs(root.left);
    }

    /**
     * 剑指 Offer 68 - II. 二叉树的最近公共祖先
     * 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先
     */

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        /**
         * 后序遍历，有等于q,p的值的时候再返回
         */
        TreeNode temp1;
        TreeNode temp2;
        if (root == null) {
            return null;
        }

        if (p.val == root.val || q.val == root.val) {
            return root;
        }

        temp1 = lowestCommonAncestor(root.left, p, q);
        temp2 = lowestCommonAncestor(root.right, p, q);


        if (temp1 == null && temp2 == null) {
            return null;
        } else if (temp1 == null) {
            return temp2;
        } else if (temp2 == null) {
            return temp1;
        }

        return root;
    }

    /**
     * 剑指 Offer 68 - I. 二叉搜索树的最近公共祖先
     * 给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先
     */
    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        int small = p.val;
        int big = q.val;
        if (p.val > q.val) {
            small = q.val;
            big = p.val;
        }
        if (root.val > small && root.val < big) {
            return root;
        } else if (root.val < small) {
            return lowestCommonAncestor2(root.right, p, q);
        } else if (root.val > big) {
            return lowestCommonAncestor2(root.left, p, q);
        }
        return root;
    }


    /**
     * 剑指 Offer 32 - II. 从上到下打印二叉树 II
     * 从上到下按层打印二叉树，同一层的节点按从左到右的顺序打印，每一层打印到一行。
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List out = new ArrayList<ArrayList<Integer>>();
        Queue<TreeNode> q = new LinkedList<TreeNode>();
        if (root != null) q.add(root);
        while (!q.isEmpty()) {
            List tempList = new ArrayList<Integer>();
            System.out.println(q.size());
            for (int i = q.size(); i > 0; i--) {
                TreeNode temp = q.poll();
                tempList.add(temp.val);
                if (temp.left != null) q.add(temp.left);
                if (temp.right != null) q.add(temp.right);
            }
            out.add(tempList);
        }
        return out;
    }

    /**
     * 剑指 Offer 28. 对称的二叉树
     *请实现一个函数，用来判断一棵二叉树是不是对称的。如果一棵二叉树和它的镜像一样，那么它是对称的。
     *
     * @param root
     * @return
     */
    public boolean isSymmetric(TreeNode root) {
        if(root==null) return true;
        return recur(root.left,root.right);
    }

    private boolean recur(TreeNode left,TreeNode right) {
        if(left == null && right==null) return true;
        if(left == null ||right ==null||left.val!=right.val)return false;
        return recur(left.left,right.right) && recur(left.right , right.left);
    }

}

