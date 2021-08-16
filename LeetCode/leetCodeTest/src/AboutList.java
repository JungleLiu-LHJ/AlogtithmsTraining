import java.util.Stack;

public class AboutList {

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    //*******easy**************************//


    /**
     * 剑指 Offer 06. 从尾到头打印链表
     * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。
     *
     * @param head
     * @return
     */
    public int[] reversePrint(ListNode head) {
        Stack<Integer> stack = new Stack<>();

        ListNode node = head;
        while (node != null) {
            stack.push(node.val);
            node = node.next;
        }

        int[] out = new int[stack.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = stack.pop();
        }
        return out;
    }

    /**
     * 剑指 Offer 18. 删除链表的节点
     * 给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。
     * <p>
     * 返回删除后的链表的头节点。
     *
     * @param head
     * @param val
     * @return
     */
    public ListNode deleteNode(ListNode head, int val) {
        ListNode node = head;
        if (node.val == val) {
            return head.next;
        }

        while (node != null) {
            if (node.next != null && node.next.val == val) {
                ListNode temp = node.next.next;
                node.next = temp;
                break;
            }
            node = node.next;
        }
        return head;
    }

    /**
     * 剑指 Offer 22. 链表中倒数第k个节点
     * 输入一个链表，输出该链表中倒数第k个节点。为了符合大多数人的习惯，本题从1开始计数，即链表的尾节点是倒数第1个节点。
     * <p>
     * 例如，一个链表有 6 个节点，从头节点开始，它们的值依次是 1、2、3、4、5、6。这个链表的倒数第 3 个节点是值为 4 的节点。
     *
     * @param head
     * @param k
     * @return
     */
    public ListNode getKthFromEnd(ListNode head, int k) {
        ListNode front = head, right = head;
        int i = 1;
        while (right != null) {
            if (i > k) front = front.next;
            right = right.next;
            i++;
        }
        return front;
    }


    /**
     * 剑指 Offer 24. 反转链表
     * 定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。
     *
     * @param head
     * @return
     */
    public ListNode reverseList(ListNode head) {
        ListNode fron, cur, behind;
        fron = null;
        cur = head;

        while (cur != null) {
            behind = cur.next;
            cur.next = fron;
            fron = cur;
            cur = behind;
        }
        return fron;
    }

    //递归版本
    public ListNode reverseList2(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode node = reverseList2(head.next);

        head.next.next = head;
        head.next = null;

        return node;
    }

    /**
     * 剑指 Offer 25. 合并两个排序的链表
     * 输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。
     *
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode first, second, result;
        if (l1 == null) return l2;
        else if (l2 == null) return l1;

        if (l1.val < l2.val) {
            first = l1;
            second = l2;
        } else {
            first = l2;
            second = l1;
        }
        result = first;
        while (second != null) {
            if (first.val < second.val || first.val == second.val) {
                if (first.next == null || second.val <= first.next.val) {
                    ListNode node = first.next;
                    ListNode node2 = second.next;

                    first.next = second;
                    second.next = node;

                    second = node2;
                }
            }
            first = first.next;


        }
        return result;

    }


    /**
     * 剑指 Offer 52. 两个链表的第一个公共节点
     * 输入两个链表，找出它们的第一个公共节点。
     * @param headA
     * @param headB
     * @return
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if(headA==null || headB ==null) return null;

        ListNode node1 = headA,node2 = headB;

        while (node1!=node2) {
            node1=node1==null?headA:node1.next;
            node2=node2==null?headB:node2.next;
        }

        return node1;
    }


}
