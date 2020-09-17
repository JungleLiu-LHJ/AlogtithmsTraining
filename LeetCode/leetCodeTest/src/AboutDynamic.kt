import java.util.Collections.max

/**
 *08.11. 硬币
 * 硬币。给定数量不限的硬币，币值为25分、10分、5分和1分，编写代码计算n分有几种表示法。(结果可能会很大，你需要将结果模上1000000007)
 **/

fun waysToChange(n: Int): Int {
    var times = IntArray(n + 1)
    val coins = intArrayOf(25, 10, 5, 1)
    times[0] = 1
    coins.forEach {
        for (i in 0..n) {
            if (i - it >= 0) {
                times[i] = (times[i - it] + times[i]) % 1000000007
            }
        }
    }
    return times[n]
}

/**
 * 爬楼梯
 */
fun climbStairs(n: Int): Int {
    var times = IntArray(n + 1)
    val coins = intArrayOf(1, 2)
    times[0] = 1

    for (i in 0..n) {
        coins.forEach {
            if (i - it >= 0) {
                times[i] = (times[i - it] + times[i])
            }
        }
    }
    return times[n]
}

/**
 * 53. 最大子序和
 * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
 */

fun maxSubArray(nums: IntArray): Int {
    val n = IntArray(nums.size + 1)
    n[0] = 0
    var max = nums[0]
    nums.indices.forEach {

        n[it + 1] = kotlin.math.max(nums[it] + n[it], nums[it])
        if (it > 0) max = kotlin.math.max(n[it + 1], max)
    }
    return max
}


/**
 * 121. 买卖股票的最佳时机
 * 给定一个数组，它的第i 个元素是一支给定股票第 i 天的价格。
 * 如果你最多只允许完成一笔交易（即买入和卖出一支股票一次），设计一个算法来计算你所能获取的最大利润。
 *  注意：你不能在买入股票前卖出股票。
 *
 */
fun maxProfit(prices: IntArray): Int {
    var max = 0
    var min = 0
    if(prices.isNotEmpty()) {
       min = prices[0]
    }
    prices.indices.forEach {
        if (prices[it] > min) {
            max = kotlin.math.max(max, prices[it] - min)
        } else {
            min = prices[it]
        }
    }
    return max
}

fun main() {
    println(waysToChange(10))
}