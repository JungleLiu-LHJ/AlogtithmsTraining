public class AboutList {

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    ListNode pre, mid, after;

    public ListNode reverseBetween(ListNode head, int left, int right) {

        pre = head;

        ListNode first = null, last = null, temp1 = null, temp2 = null;
        int index = 1;
        if (left == right) {
            return head;
        }

        while (true) {
            if (pre == null) {
                break;
            }
            if (index == left) {
                mid = pre.next;

                after = mid.next;

                first = pre;
                pre.next = null;
                mid.next = pre;
                pre = mid;
                mid = after;
                if (after != null)
                    after = after.next;

            } else if (index > left && index < right) {

                mid.next = pre;
                pre = mid;
                mid = after;
                if (after != null)
                    after = after.next;

            } else if (index == right) {
                last = pre;
                pre = mid;
            } else {
                if (index == left - 1) {
                    temp1 = pre;
                }
                if (index == right + 1) {
                    temp2 = pre;
                }
                pre = pre.next;
            }
            index++;
        }
        if (first != null && temp2 != null) {
            first.next = temp2;
        }
        if (last != null && temp1 != null) {
            temp1.next = last;
        }
        if(temp1==null) return last;
        return head;


    }


}
