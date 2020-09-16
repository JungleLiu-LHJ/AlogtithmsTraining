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

fun main() {
    println(waysToChange(10))
}