import java.util.LinkedList;

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
     * @param root
     * @return 深度
     */
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return max(maxDepth(root.left)  , maxDepth(root.right)) + 1;
    }

    /**
     * 剑指 Offer 27. 二叉树的镜像
     * 请完成一个函数，输入一个二叉树，该函数输出它的镜像。
     */
    public TreeNode mirrorTree(TreeNode root) {
        if(root == null) {
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
    int k,value;
    public int kthLargest(TreeNode root, int k) {
        this.k = k;
        dfs(root);
        return value;
    }

    private void dfs(TreeNode root) {
        if(root == null) {
            return;
        }
        dfs(root.right);
        this.k--;
        if(k == 0) value = root.val;
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
        if(root==null) {
            return null;
        }

        if(p.val == root.val || q.val == root.val) {
            return root;
        }

        temp1 = lowestCommonAncestor(root.left,p,q);
        temp2 = lowestCommonAncestor(root.right,p,q);


        if(temp1==null && temp2==null) {
            return null;
        } else if (temp1 == null) {
            return temp2;
        } else if(temp2 == null) {
            return temp1;
        }

       return root;
    }






}
