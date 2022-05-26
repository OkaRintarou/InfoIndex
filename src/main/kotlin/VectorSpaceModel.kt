import java.io.File
import kotlin.math.min
import kotlin.math.sqrt

/**
 * # 向量空间模型
 * @property invertedIndex 倒排索引
 */
class VectorSpaceModel(private val invertedIndex: InvertedIndex) {
    /**
     * # 查询结果
     *
     * @property fileID 文件ID
     * @property cos 相关度，取值: 0 < cos <= 1
     * @property vector 文档向量
     * @property lexicons 检索到的词典条目
     */
    data class Result(
        val fileID: Int,
        val cos: Double,
        val vector: Vector,
        val lexicons:ArrayList<Lexicon>
    ) {
        override fun toString(): String {
            val sb = StringBuilder()
            sb.append("fileID: $fileID    correlation: $cos    vector: ${vector.contentToString()}\n\n")
            sb.append("keywords location:\n")
            for (l in lexicons){
                sb.append("${l.word}: ${l.wordIndex[0].index}\n")
            }
            return sb.toString()
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Result

            if (fileID != other.fileID) return false
            if (cos != other.cos) return false
            if (!vector.contentEquals(other.vector)) return false
            if (lexicons != other.lexicons) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileID
            result = 31 * result + cos.hashCode()
            result = 31 * result + vector.contentHashCode()
            result = 31 * result + lexicons.hashCode()
            return result
        }
    }

    /**
     * 查询结果链表
     */
    private var results = ArrayList<Result>()

    /**
     * 输出结果
     * @param count 最大输出数
     * @param lines 预览输出行数
     */
    fun showInfo(count: Int,lines:Int) {
        val l = min(count, results.size)
        for (i in 0 until l) {
            val result = results[i]
            val record = invertedIndex.getRecord(result.fileID)
            println("Record ${i + 1}:")
            println("$result")
            println("Property:")
            println(record)
            println()
            println("Content Preview:")
            val readLines = File(record.filePath).readLines()
            val maxLine = min(readLines.size,lines)
            for (line in 0 until maxLine){
                println(readLines[line])
            }
            println("-".repeat(30))
        }
    }

    /**
     * 搜索功能
     *
     * @param content 关键词，由空格分隔
     */
    fun search(content: String) {
        val keywords = content.split(" ").filter { it.isNotBlank() }
        val fileCount = invertedIndex.getCount()
        val d = Vector(keywords.size)
        val results = ArrayList<Result>()
        for (i in 0 until fileCount) {
            val lexicons=ArrayList<Lexicon>()
            for (j in keywords.indices) {
                val lex=invertedIndex.find(i, keywords[j])
                lex?.let { lexicons.add(it)  }
                d[j] = if (lex != null) 1 else 0
            }
            val r = calculate(d, keywords.size)
            if (!r.isNaN() && r != 0.0) results.add(Result(i, r, d.clone(),lexicons))
        }
        results.sortByDescending { it.cos }
        with(this.results) {
            clear()
            addAll(results)
        }
        println("Get ${results.size} records.\n")

    }

    /**
     * 计算文档向量与查询向量的夹角余弦
     *
     * @param d 文档向量
     * @param len 向量长度
     * @return 余弦值
     */
    private fun calculate(d: Vector, len: Int): Double {
        var a = 0
        var b = 0
        for (i in 0 until len) {
            a += d[i]
            b += if (d[i] == 0) 0 else 1
        }
        return a.toDouble() / sqrt((b * len).toDouble())
    }
}

/**
 * 向量
 */
typealias Vector = IntArray
