import java.util.Arrays;

public class AboutSort {


    void quick_sort(int s[], int l, int r) {
        if (l >= r) return;

        int i = l, j = r, x = s[l];
        while (i < j) {
            while (i < j && s[j] >= x) // 从右向左找第一个小于x的数
                j--;
            if (i < j)
                s[i++] = s[j];

            while (i < j && s[i] < x) // 从左向右找第一个大于等于x的数
                i++;
            if (i < j)
                s[j--] = s[i];
        }
        s[i] = x;
        quick_sort(s, l, i - 1); // 递归调用
        quick_sort(s, i + 1, r);

    }


    private void buildMaxHeap(int[] arr, int len) {
        for (int i = (int) Math.floor(len / 2); i >= 0; i--) {
            heapify(arr, i, len);
        }
    }

    private void heapify(int[] arr, int i, int len) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;

        if (left < len && arr[left] > arr[largest]) {
            largest = left;
        }
 {
            largest = right;
        }
        if (right < len && arr[right] > arr[largest])

        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, largest, len); //这里是为了交换以后保持下面的堆也是大顶堆
        }
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public void sort(int[] sourceArray) {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = sourceArray;

        int len = arr.length;

        for (int i = (int) Math.floor(len / 2f); i >= 0; i--) {
            heapify(arr, i, len);
        }
        System.out.println(Arrays.toString(arr));
        for (int i = len - 1; i > 0; i--) {
            swap(arr, 0, i);
            len--;
            heapify(arr, 0, len);//交换后保持下面的也是大顶堆，注意这里Len-1了
        }
    }


    public static void main(String[] args) {
        int[] arr = new int[]{1,3,4,5,6,2,9};


        new AboutSort().sort(arr);
        System.out.println(Arrays.toString(arr));
    }

}
