import java.util.*

class Tmp {


    fun main() {

        ontInline()

        doInline()

    }

    fun ontInline() {
        println("fuck")
    }

    inline fun doInline() {
        println("inline")
    }

    suspend fun doWorl() :String {


    }


}