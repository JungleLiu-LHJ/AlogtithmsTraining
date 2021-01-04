import com.sun.source.tree.Tree;

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
     * 请实现一个函数，用来判断一棵二叉树是不是对称的。如果一棵二叉树和它的镜像一样，那么它是对称的。
     *
     * @param root
     * @return
     */
    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return recur(root.left, root.right);
    }

    private boolean recur(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null || left.val != right.val) return false;
        return recur(left.left, right.right) && recur(left.right, right.left);
    }

    /**
     * 剑指 Offer 55 - II. 平衡二叉树
     * 输入一棵二叉树的根节点，判断该树是不是平衡二叉树。如果某二叉树中任意节点的左右子树的深度相差不超过1，那么它就是一棵平衡二叉树
     */
    public boolean isBalanced(TreeNode root) {
        return reDeep(root) != -1;
    }

    private int reDeep(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = reDeep(root.left);

        if (left == -1) {
            return -1;
        }
        int right = reDeep(root.right);
        if (right == -1) {
            return -1;
        }
        return Math.abs(left - right) < 2 ? Math.max(left, right) + 1 : -1;

    }

    /**
     * 剑指 Offer 07. 重建二叉树
     * 输入某二叉树的前序遍历和中序遍历的结果，请重建该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length == 0 || inorder.length == 0) {
            return null;
        }
        if (preorder.length == 1) {
            return new TreeNode(preorder[0]);
        }
        int value = preorder[0];
        TreeNode node = new TreeNode(value);
        int[] nums = getIndex(inorder, value);
        node.left = buildTree(Arrays.copyOfRange(preorder, 1, nums[0] + 1), Arrays.copyOfRange(inorder, 0, nums[0]));
        node.right = buildTree(Arrays.copyOfRange(preorder, nums[0] + 1, preorder.length), Arrays.copyOfRange(inorder, nums[0] + 1, inorder.length));

        return node;
    }

    private int[] getIndex(int[] order, int num) {
        int[] out = {0, 0};
        for (int i = 0; i < order.length; i++) {
            if (order[i] == num) {
                out[0] = i;
                break;
            }
        }
        out[1] = order.length - out[0] - 1;
        return out;
    }

    HashMap<Integer, Integer> map = new HashMap();
    int[] preorder;

    public TreeNode buildTree2(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length == 0 || inorder.length == 0) {
            return null;
        }
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        this.preorder = preorder;

        return recur(0, 0, preorder.length - 1);
    }

    TreeNode recur(int root, int left, int right) {
        if (left > right) return null;                          // 递归终止
        TreeNode node = new TreeNode(preorder[root]);          // 建立根节点
        int i = map.get(preorder[root]);                       // 划分根节点、左子树、右子树
        node.left = recur(root + 1, left, i - 1);              // 开启左子树递归
        node.right = recur(root + i - left + 1, i + 1, right); // 开启右子树递归
        return node;                                           // 回溯返回根节点
    }

    /**
     * 剑指 Offer 32 - I. 从上到下打印二叉树
     * 从上到下打印出二叉树的每个节点，同一层的节点按照从左到右的顺序打印。(层序遍历)
     *
     * @param root
     * @return
     */

    public int[] levelOrder2(TreeNode root) {
        ArrayList<Integer> out = new ArrayList();
        LinkedList<TreeNode> q = new LinkedList<TreeNode>();
        if (root != null)
            q.add(root);
        while (!q.isEmpty()) {
            int l = q.size();
            for (int i = 0; i < l; i++) {
                TreeNode temp = q.poll();
                out.add(temp.val);
                if (temp.left != null) q.add(temp.left);
                if (temp.right != null) q.add(temp.right);
            }
        }
        int[] d = new int[out.size()];
        for (int i = 0; i < out.size(); i++) {
            d[i] = (int) out.get(i);
        }
        return d;
    }

    /**
     * 剑指 Offer 32 - III. 从上到下打印二叉树 III
     * 请实现一个函数按照之字形顺序打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右到左的顺序打印，第三行再按照从左到右的顺序打印，其他行以此类推。
     */
    public List<List<Integer>> levelOrder3(TreeNode root) {
        ArrayList<List<Integer>> out = new ArrayList();
        LinkedList<TreeNode> q = new LinkedList<TreeNode>();
        if (root != null)
            q.add(root);
        Boolean isLeft = false;
        while (!q.isEmpty()) {
            int l = q.size();
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int i = 0; i < l; i++) {
                if (isLeft) {
                    TreeNode temp = q.pollFirst();
                    tempList.add(temp.val);
                    if (temp.right != null) q.addLast(temp.right);
                    if (temp.left != null) q.addLast(temp.left);
                } else {
                    TreeNode temp = q.pollLast();
                    tempList.add(temp.val);
                    if (temp.left != null) q.addFirst(temp.left);
                    if (temp.right != null) q.addFirst(temp.right);
                }
            }
            isLeft = !isLeft;
            out.add(tempList);
        }
        return out;
    }

    /**
     * 剑指 Offer 34. 二叉树中和为某一值的路径
     * 输入一棵二叉树和一个整数，打印出二叉树中节点值的和为输入整数的所有路径。从树的根节点开始往下一直到叶节点所经过的节点形成一条路径。
     */
    LinkedList<List<Integer>> out = new LinkedList();
    LinkedList<Integer> path = new LinkedList();

    public List<List<Integer>> pathSum(TreeNode root, int sum) {

        recur(root, sum);
        return out;
    }

    private void recur(TreeNode node, int var) {
        if (node == null) {
            return;
        }
        int d = var - node.val;
        path.add(node.val);
        if (d == 0 && node.left == null && node.right == null) {
            out.add(new LinkedList<>(path));
        }

        recur(node.left, d);
        recur(node.right, d);
        path.pollLast();
    }


    /**
     * 剑指 Offer 26. 树的子结构
     * 输入两棵二叉树A和B，判断B是不是A的子结构。(约定空树不是任意一个树的子结构)
     * <p>
     * B是A的子结构， 即 A中有出现和B相同的结构和节点值。
     */

    public boolean isSubStructure(TreeNode A, TreeNode B) {
        if (B == null || A == null) {
            return false;
        }
        return recurBool(A, B) || isSubStructure(A.left, B) || isSubStructure(A.right, B);
    }

    private boolean recurBool(TreeNode A, TreeNode B) {
        if (B == null) return true;
        if (A == null || B.val != A.val) return false;
        return recurBool(A.left, B.left) && recurBool(A.right, B.right);
    }


    /**
     * 剑指 Offer 37. 序列化二叉树
     * 请实现两个函数，分别用来序列化和反序列化二叉树。
     */

    public class Codec {

        // Encodes a tree to a single string.
        public String serialize(TreeNode root) {
            StringBuilder out = new StringBuilder();
            out.append("[");
            LinkedList<TreeNode> q = new LinkedList<TreeNode>();
            boolean goingOn = true;
            if (root != null)
                q.add(root);
            else {
                q.add(null);
                goingOn = false;
            }
            while (goingOn) {
                int l = q.size();
                goingOn = false;
                for (int i = 0; i < l; i++) {
                    TreeNode temp = q.poll();
                    out.append(temp == null ? "null" : temp.val);
                    out.append(",");
                    if (temp.left != null) {
                        q.add(temp.left);
                        goingOn = true;
                    } else {
                        q.add(null);
                    }
                    if (temp.right != null) {
                        q.add(temp.right);
                        goingOn = true;
                    } else {
                        q.add(null);
                    }
                }
            }
            return out.toString();
        }

        // Decodes your encoded data to tree.
//        public TreeNode deserialize(String data) {
//
//        }
    }

    public static void main(String[] args) {
        StringBuilder a = new StringBuilder();
                a.append(2).append(4).append("null");
        System.out.println(a.toString());
    }


}

