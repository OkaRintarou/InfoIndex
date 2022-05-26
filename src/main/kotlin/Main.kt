/**
 * 使用说明
 *
 * 该系统是给英语用的
 *
 * 文件名由0开始依次加1
 * 文件由两部分组成
 *  0.content 存放文件内容
 *  0.property 存放文件属性，单行，由4部分组成，标题，URL，日期，content的绝对路径，每部分空格隔开
 *
 *  系统提示输入文件夹时不要以"\"结尾，正确示例: "c:\folder"
 *  所有斜杠都用"\"
 */
fun main() {
    println("Do you have a tmp file?(Y/N)")
    val invertedIndex = InvertedIndex()
    if (readln() == "Y") {
        println("Input tmp file path:")
        val tmp = readln()
        println("Input file folder:")
        val fileFolder = readln()
        println("How many records?")
        val count = readln().toInt()
        invertedIndex.init(tmp, fileFolder, count)
    } else {
        println("Input file folder:")
        val fileFolder = readln()
        println("How many records?")
        val count = readln().toInt()
        println("Where do you want to save index tmp file?")
        val tmp = readln()
        invertedIndex.init(
            fileFolder,
            count,
            tmp
        )
    }
    val vectorSpaceModel = VectorSpaceModel(invertedIndex)
    val flag = true
    while (flag) {
        println("Input keywords:(### for exit)")
        val keywords = readln()
        if (keywords == "###") break
        println("Max records for preview:")
        val num= readln().toInt()
        vectorSpaceModel.search(keywords)
        vectorSpaceModel.showInfo(num)
    }
    println("Exit.")
}
