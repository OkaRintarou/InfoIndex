/**
 * # 单词词典
 *
 * @property word 单词
 * @property wordIndex 所在文件及出现位置的链表
 */
data class Lexicon(
    val word: String, val wordIndex: ArrayList<WordIndex>
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(word + "\n")
        for (item in wordIndex) {
            sb.append(item.fileID.toString() + "\n")
            for (index in item.index) {
                sb.append("$index ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

/**
 * 记录所属单词出现的文件ID及位置
 * @property fileID 文件ID
 * @property index 单词在文件的出现位置
 */
data class WordIndex(
    val fileID: Int, val index: ArrayList<Int>
)

/**
 * # 文件属性
 *
 * @property title 标题
 * @property url URL
 * @property date 日期
 * @property filePath 文件绝对路径
 */
data class Record(
    val title: String, val url: String, val date: String, val filePath: String
) {
    override fun toString() = "Title: $title    URL: $url    Date: $date"
}


/**
 * 查询关键词是否在词典中存在
 * @param word 关键词
 * @return 词典中的索引值，-1为不存在
 */
fun ArrayList<Lexicon>.findWord(word: String): Int {
    for (i in 0 until this.size) {
        if (this[i].word == word) return i
    }
    return -1
}

/**
 * 词典添加索引项
 * @param word 单词
 * @param fileID 文件ID
 * @param index 出现在文件的位置
 */
fun ArrayList<Lexicon>.addIndex(word: String, fileID: Int, index: Int) {
    val i = this.findWord(word)
    if (i == -1) {
        val wordIndex = WordIndex(fileID, ArrayList<Int>().apply { add(index) })
        this.add(Lexicon(word, ArrayList<WordIndex>().apply { add(wordIndex) }))
    } else {
        val fileIDr = this[i].wordIndex.findFileID(fileID)
        if (fileIDr == -1) {
            this[i].wordIndex.add(WordIndex(fileID, ArrayList<Int>().apply { add(index) }))
        } else {
            this[i].wordIndex[fileIDr].index.add(index)
        }
    }
}

/**
 * 查询某个单词是否在对应文件存在
 * @param fileID 文件ID
 * @return 链表索引，-1为不存在
 */
fun ArrayList<WordIndex>.findFileID(fileID: Int): Int {
    for (i in 0 until this.size) {
        if (this[i].fileID == fileID) return i
    }
    return -1
}
