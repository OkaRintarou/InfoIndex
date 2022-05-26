import java.io.File

/**
 * # 倒排索引
 */
class InvertedIndex {
    /**
     * 读取文件内容并分词
     * @param filePath 文件路径
     * @return 分词结果的链表
     */
    private fun readContentToSlice(filePath: String): ArrayList<String> {
        val content = File(filePath).readText()
        val sb = StringBuilder(content)
        for (i in sb.indices) {
            if (!sb[i].isLetterOrDigit()) sb[i] = ' '
        }
        return ArrayList(sb.split(" ").filter { it.isNotBlank() })
    }

    /**
     * 文件数
     */
    fun getCount() = records.size

    /**
     * 获取文件属性
     * @param i 文件ID
     */
    fun getRecord(i: Int) = records[i]

    /**
     * 文件属性链表
     */
    private val records: ArrayList<Record> = ArrayList()

    /**
     * 读取文件属性
     * @param fileFolder 存放文件的文件夹
     * @param count 文件数，内容+属性算1条，就是实际文件数除以2
     * @return 文件属性链表
     */
    private fun getRecords(fileFolder: String, count: Int): ArrayList<Record> {
        val list = ArrayList<Record>(count)
        for (i in 0 until count) {
            val fp = File("$fileFolder\\$i.property")
            val lines = fp.readLines()
            list.add(Record(lines[0].trim(), lines[1].trim(), lines[2].trim(), lines[3].trim()))
        }
        return list
    }
    /**
     * 单词词典
     */
    private val lexicon: ArrayList<Lexicon> = ArrayList()

    /**
     * 构建索引
     *
     * @param outFile 索引缓存位置
     */
    private fun build(outFile: String) {
        println("Read ${records.size} files. Indexing...")
        for (i in 0 until records.size) {
            val contentSlice = readContentToSlice(records[i].filePath)
            for (j in 0 until contentSlice.size) {
                lexicon.addIndex(contentSlice[j], i, j)
            }
        }
        println("Get ${lexicon.size} words.")
        val file = File(outFile)
        file.bufferedWriter().use { out ->
            for (item in lexicon) {
                out.write(item.toString() + "\n")
            }
        }
        println("Write index to $outFile")
    }

    /**
     * 查询功能
     *
     * @param fileID 文件ID
     * @param word 关键词
     * @return 只包含关键词和对应文件索引项的词典条目
     */
    fun find(fileID: Int, word: String): Lexicon? {
        val findWord = lexicon.findWord(word)
        if (findWord == -1) return null
        val findFileID = lexicon[findWord].wordIndex.findFileID(fileID)
        return if (findFileID == -1) null
        else Lexicon(
            lexicon[findWord].word,
            ArrayList<WordIndex>().apply { add(lexicon[findWord].wordIndex[findFileID]) })
    }

    /**
     * 初始化（构建）
     *
     * @param fileFolder 文件夹
     * @param count 文件数
     * @param outFile 缓存输出位置
     */
    fun init(fileFolder: String, count: Int, outFile: String) {
        records.addAll(getRecords(fileFolder, count))
        build(outFile)
    }

    /**
     * 初始化（已有缓存）
     *
     * @param source 缓存位置
     * @param fileFolder 文件夹
     * @param count 文件数
     */
    fun init(source: String, fileFolder: String, count: Int) {
        records.addAll(getRecords(fileFolder, count))
        val lines = mutableListOf<String>()
        File(source).useLines { lines.addAll(it) }
        var status = 0
        var lex: Lexicon? = null
        var wordIndex: WordIndex? = null
        for (l in lines) {
            val x = l.trim()
            when (status) {
                0 -> {
                    if (x.isBlank()) break
                    lex = Lexicon(x, ArrayList())
                    status++
                }
                1 -> {
                    if (x.isBlank()) {
                        lexicon.add(lex!!)
                        status = 0
                        continue
                    }
                    wordIndex = WordIndex(x.toInt(), ArrayList())
                    status++
                }
                2 -> {
                    x.split(" ").filter { it.isNotBlank() }.forEach {
                        wordIndex!!.index.add(it.toInt())
                    }
                    lex!!.wordIndex.add(wordIndex!!)
                    status--
                }
            }
        }
        println("Get ${lexicon.size} words from tmp $source")
    }


}
