/**
 * 814.钥匙和房间
 */
class KeysAndRoom {
    lateinit var keysSet : Array<Boolean?>
    var num = 0
    fun canVisitAllRooms(rooms: List<List<Int>>): Boolean {
        val l = rooms.size
        keysSet = Array(l) {false}
        search(0, rooms)

        return l == num
    }

    fun search(index: Int, rooms: List<List<Int>>) {
        keysSet[index] = true
        num += 1
        if(rooms[index].isEmpty()) return
        rooms[index].forEach {
            if (!keysSet[it]!!) {
                search(it,rooms)
            }
        }
    }
}