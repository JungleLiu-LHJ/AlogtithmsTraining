import java.util.*

class QuickSort {

    fun sort(sortArray: IntArray, leftIndex: Int, rightIndex: Int) {
        var leftIndexTemp = leftIndex
        var rightIndexTemp = rightIndex
        if (leftIndex > rightIndex)
            return
        //基准数
        val cardinalNum = sortArray[leftIndex]
        while (leftIndexTemp != rightIndexTemp) {
            //顺序很重要，要先从右边开始找
            while (sortArray[rightIndexTemp] >= cardinalNum && leftIndexTemp < rightIndexTemp)
                rightIndexTemp--
            //再找左边的
            while (sortArray[leftIndexTemp] <= cardinalNum && leftIndexTemp < rightIndexTemp)
                leftIndexTemp++
            //交换两个数在数组中的位置
            if (leftIndexTemp < rightIndexTemp) {
                val temp = sortArray[leftIndexTemp]
                sortArray[leftIndexTemp] = sortArray[rightIndexTemp]
                sortArray[rightIndexTemp] = temp
/*
                sortArray[leftIndexTemp] = sortArray[leftIndexTemp] xor sortArray[rightIndexTemp]
                sortArray[rightIndexTemp] = sortArray[leftIndexTemp] xor sortArray[rightIndexTemp]
                sortArray[leftIndexTemp] = sortArray[leftIndexTemp] xor sortArray[rightIndexTemp]*/
            }
        }
        sortArray[leftIndex] = sortArray[leftIndexTemp]
        sortArray[leftIndexTemp] = cardinalNum
        sort(sortArray, leftIndex, leftIndexTemp - 1)//继续处理左边的，这里是一个递归的过程
        sort(sortArray, leftIndexTemp + 1, rightIndex)//继续处理右边的 ，这里是一个递归的过程
    }
}


fun main(args: Array<String>) {
    //要进行排序的数组
    val sortArray = IntArray(10)
    val ra = Random()
    //生成随机数，对这个随机数数组进行排序
    println("随机数组：")
    for (i in sortArray.indices) {
        sortArray[i] = ra.nextInt(100)
        print("${sortArray[i]} ")
    }
    println()
    QuickSort().sort(sortArray, 0, sortArray.size - 1)
    println("排序后的数组：")
    for (i in sortArray) {
        print("${i} ")
    }
}