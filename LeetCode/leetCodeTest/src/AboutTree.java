
import java.util.*;

import static java.lang.Integer.max;
import static java.lang.Integer.sum;

public class AboutTree {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /****************BST************************/

    /**
     * 230. 二叉搜索树中第K小的元素
     * 给定一个二叉搜索树的根节点 root ，和一个整数 k ，请你设计一个算法查找其中第 k 个最小元素（从 1 开始计数）。
     *
     * @param root
     * @param k
     * @return
     */
    private int mK;
    private int kResult;

    public int kthSmallest(TreeNode root, int k) {
        if (root == null) return -1;

        mK = k;
        kthSmallest(root);

        return kResult;

    }

    private void kthSmallest(TreeNode root) {
        if (root == null) return;

        kthSmallest(root.left);

        mK = mK - 1;
        if (mK == 0) kResult = root.val;
        kthSmallest(root.right);

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
     * 538. 把二叉搜索树转换为累加树
     * 给出二叉 搜索 树的根节点，该树的节点值各不相同，请你将其转换为累加树（Greater Sum Tree），使每个节点 node 的新值等于原树中大于或等于 node.val 的值之和。
     * <p>
     * 提醒一下，二叉搜索树满足下列约束条件：
     * <p>
     * 节点的左子树仅包含键 小于 节点键的节点。
     * 节点的右子树仅包含键 大于 节点键的节点。
     * 左右子树也必须是二叉搜索树。
     *
     * @param root
     * @return
     */
    public TreeNode convertBST(TreeNode root) {
        sumdfs(root.left,0);
        return root;
    }

    private int sumdfs(TreeNode node,int val) {
        if (node == null) return val;
        int a = sumdfs(node.right,val);
        node.val = a + node.val;
        int r = sumdfs(node.left,node.val);
        return r;
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
     * 98. 验证二叉搜索树
     * 给定一个二叉树，判断其是否是一个有效的二叉搜索树。
     * <p>
     * 假设一个二叉搜索树具有如下特征：
     * <p>
     * 节点的左子树只包含小于当前节点的数。
     * 节点的右子树只包含大于当前节点的数。
     * 所有左子树和右子树自身必须也是二叉搜索树。
     *
     * @param root
     * @return
     */
    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, null, null);

    }

    private boolean isValidBST(TreeNode root, TreeNode min, TreeNode max) {
        if (root == null) return true;
        if (min != null && min.val >= root.val) return false;
        if (max != null && max.val <= root.val) return false;
        return isValidBST(root.right, root, max) && isValidBST(root.left, min, root);
    }


    /**
     * 701. 二叉搜索树中的插入操作
     * 给定二叉搜索树（BST）的根节点和要插入树中的值，将值插入二叉搜索树。 返回插入后二叉搜索树的根节点。 输入数据 保证 ，新值和原始二叉搜索树中的任意节点值都不同。
     * <p>
     * 注意，可能存在多种有效的插入方式，只要树在插入后仍保持为二叉搜索树即可。 你可以返回 任意有效的结果 。
     *
     * @param root
     * @param val
     * @return
     */
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) return new TreeNode(val);
        if (root.val == val) return root;

        if (val > root.val)
            root.right = insertIntoBST(root.right, val);
        else if (val < root.val)
            root.left = insertIntoBST(root.left, val);
        return root;
    }

    /**
     * 450. 删除二叉搜索树中的节点
     * 给定一个二叉搜索树的根节点 root 和一个值 key，删除二叉搜索树中的 key 对应的节点，并保证二叉搜索树的性质不变。返回二叉搜索树（有可能被更新）的根节点的引用。
     * <p>
     * 一般来说，删除节点可分为两个步骤：
     * <p>
     * 首先找到需要删除的节点；
     * 如果找到了，删除它。
     * 说明： 要求算法时间复杂度为 O(h)，h 为树的高度。
     *
     * @param root
     * @param key
     * @return
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) return root;
        if (root.val == key) {
            if (root.right == null) return root.left;
            if (root.left == null) return root.right;

            root.val = findLeftNode(root.right).val; //找到替换现在节点的值
            root.right = deleteNode(root.right, root.val); //删除掉替换的那个根节点
            return root;
        } else if (root.val > key) {
            root.left = deleteNode(root.left, key);
        } else {
            root.right = deleteNode(root.right, key);
        }

        return root;
    }

    private TreeNode findLeftNode(TreeNode node) {

        while (node.left != null) {
            node = node.left;
        }
        return node;

    }

    /************************BST*************************/


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
     * 116. 填充每个节点的下一个右侧节点指针
     * 给定一个 完美二叉树 ，其所有叶子节点都在同一层，每个父节点都有两个子节点。二叉树定义如下：
     */
    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }

    ;

    public Node connect(Node root) {

        if (root == null) return null;
        connect(root.left, root.right);
        return root;
    }

    private void connect(Node left, Node right) {
        if (left == null) return;

        left.next = right;

        connect(left.left, left.right);
        connect(left.right, right.left);
        connect(right.left, right.right);

    }


    /**
     * 114. 二叉树展开为链表
     * 给你二叉树的根结点 root ，请你将它展开为一个单链表：
     * <p>
     * 展开后的单链表应该同样使用 TreeNode ，其中 right 子指针指向链表中下一个结点，而左子指针始终为 null 。
     * 展开后的单链表应该与二叉树 先序遍历 顺序相同。
     *
     * @param root
     */
    public void flatten(TreeNode root) {
        if (root == null) return;

        flatten(root.left);
        flatten(root.right);

        TreeNode left = root.left;
        TreeNode right = root.right;

        if (left != null) {
            root.right = left;
            root.left = null;

            while (left.right != null) {
                left = left.right;
            }
            left.right = right;
        }

    }

    /**
     * 654. 最大二叉树
     * 给定一个不含重复元素的整数数组 nums 。一个以此数组直接递归构建的 最大二叉树 定义如下：
     * <p>
     * 二叉树的根是数组 nums 中的最大元素。
     * 左子树是通过数组中 最大值左边部分 递归构造出的最大二叉树。
     * 右子树是通过数组中 最大值右边部分 递归构造出的最大二叉树。
     * 返回有给定数组 nums 构建的 最大二叉树 。
     *
     * @param nums
     * @return
     */
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return constructMaximumBinaryTree(nums, 0, nums.length - 1);
    }

    public TreeNode constructMaximumBinaryTree(int[] nums, int left, int right) {
        if (left > right) return null;

        int max = 0, maxIndex = left;
        for (int i = left; i <= right; i++) {
            if (nums[i] > max) {
                max = nums[i];
                maxIndex = i;
            }
        }

        TreeNode root = new TreeNode(max);

        root.left = constructMaximumBinaryTree(nums, left, maxIndex - 1);
        root.right = constructMaximumBinaryTree(nums, maxIndex + 1, right);

        return root;
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

