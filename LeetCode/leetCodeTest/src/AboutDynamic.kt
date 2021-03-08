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
 * 70.爬楼梯
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
    if (prices.isNotEmpty()) {
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

/**
 * 198. 打家劫舍
 * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额
 */
fun rob(nums: IntArray): Int {
    val l = nums.size
    if (l == 0) {
        return 0
    } else if (l == 1) {
        return nums[0]
    }
    nums.indices.forEach {
        if (it > 2) {
            nums[it] = nums[it] + kotlin.math.max(nums[it - 2], nums[it - 3])
        } else if (it == 2) {
            nums[it] = nums[2] + nums[0]
        }
    }
    return kotlin.math.max(nums[l - 2], nums[l - 1])
}

/**
 * 746. 使用最小花费爬楼梯
 * 数组的每个索引作为一个阶梯，第 i个阶梯对应着一个非负数的体力花费值 cost[i](索引从0开始)。
 * 每当你爬上一个阶梯你都要花费对应的体力花费值，然后你可以选择继续爬一个阶梯或者爬两个阶梯。
 * 您需要找到达到楼层顶部的最低花费。在开始时，你可以选择从索引为 0 或 1 的元素作为初始阶梯。
 */
fun minCostClimbingStairs(cost: IntArray): Int {
    val l = cost.size
    if (l < 2 || l > 1000) {
        return 0
    }

    cost.indices.forEach {
        if (it > 1) {
            cost[it] = cost[it] + if (cost[it - 1] > cost[it - 2]) cost[it - 2] else cost[it - 1]
        }
    }
    return if (cost[l - 1] > cost[l - 2]) cost[l - 2] else cost[l - 1]
}

/**
 * 面试题 08.01. 三步问题
 * 三步问题。有个小孩正在上楼梯，楼梯有n阶台阶，小孩一次可以上1阶、2阶或3阶。实现一种方法，计算小孩有多少种上楼梯的方式。结果可能很大，你需要对结果模1000000007
 *
 */
fun waysToStep(n: Int): Int {
    val times = IntArray(n + 1)
    times[0] = 1
    times.indices.forEach {
        for (steps in 1..3) {
            if (it - steps >= 0) {
                times[it] = (times[it - steps] + times[it]) % 1000000007
            }
        }
    }
    return times[n]
}

/**
 * 338. 比特位计数
 * 给定一个非负整数 num。对于 0 ≤ i ≤ num 范围中的每个数字 i ，计算其二进制数中的 1 的数目并将它们作为数组返回.
 */
fun countBits(num: Int): IntArray {
    var nums = IntArray(num + 1)
    nums[0] = 0
    var binary = 2
    for (index in 1..num) {
        val temp = index % binary
        when (temp) {
            0 -> {
                binary = index
                nums[index] = 1
            }
            else -> {
                nums[index] = nums[temp] + 1
            }
        }
    }
    return nums
}


/**
 * 剑指 Offer 10- II. 青蛙跳台阶问题
 * 一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。
 */

fun numWays(n: Int): Int {
    val list = IntArray(n + 1)
    list[0] = 1
    for (i in 0..n) {
        if (i + 1 <= n) list[i + 1] = (list[i] + list[i + 1]) % 1000000007
        if (i + 2 <= n) list[i + 2] = (list[i] + list[i + 2]) % 1000000007
    }
    return list[n]
}


/**
 * 斐波那契数列
 */
fun fib(n: Int): Int {
    if (n <= 1) return n
    if (n == 2) return 1

    var a = 0
    var b = 1
    var c = 0
    for (i in 2..n) {
        c = a + b
        a = b
        b = c
    }
    return b
}

/**
 * 剑指 Offer 16. 数值的整数次方
 * 实现函数double Power(double base, int exponent)，求base的exponent次方。不得使用库函数，同时不需要考虑大数问题。
 */
fun myPow(x: Double, n: Int): Double {
    if (x == 0.0) return 0.0
    if (x == 1.0) return 1.0
    var x1 = x
    var l = n.toLong()

    if (l < 0) {
        l = -l
        x1 = 1 / x1
    }
    var res = 1.toDouble()
    while (l > 0) {
        if ((l and 1) == 1L) res *= x1
        x1 *= x1
        l = l.shr(1)
    }
    return res
}


fun main() {
    myPow(2.00000,
            10)
}