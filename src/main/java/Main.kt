
import org.jsoup.Jsoup
import java.io.*
import javax.swing.text.BadLocationException
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.rtf.RTFEditorKit



/**
 * Created by joe on 7/22/17.
 */
val getName=Regex("^(.*)\\/(\\w+)\\.\\w+$")
val getTitle=Regex("^.*?<h5>(.*?)</h5>")

fun main(args : Array<String>) {
    println("Hello, world!")
    val base= File("/Users/joe/Desktop/FromLinux/Alt/Loliwood/www.asstr.org/files/Authors/LS/www/authors/temp")
    val files=base.listFiles()
    var theFN=""
    var theF=""
    var theA=""
    for (f in files) {
        val m=getName.matchEntire(f.absolutePath)
        if (m!=null) {
            val dir=m.destructured.component1()
            val fn=m.destructured.component2()
            val writer= PrintWriter(dir+"/processed/"+theF+theA+".rtf")
            val text=f.readText()
            val doc= Jsoup.parse(text)
            val divs=doc.getElementsByTag("div")
            for (d in divs) {
                if (d.id()=="column") {
                    val m2=getTitle.matchEntire(d.html())
                    if (m2!=null) {
                        val title=m2.destructured.component1()
                        println(title)
                    }
                    val r=convertToRTF(d.html())
                    writer.append(r)
                } else if (d.id()=="author") {
                    val author=d.text()
                    val b=author.replace("by ","_by_")
                    theA=b
                } else if (d.id()=="stories") {
                    val b=d.getElementsByTag("h2")
                    val title=b.text().replace(" ","_").replace("'","").replace("#","_").replace(",","").replace(":","")
                            .replace("(","").replace(")","").replace("&","and").replace(".","").replace("!","")
                            .replace("/","-").replace(".","").replace("__","_")
                    theF=title
                }
            }
            writer.close()
        }

    }
}

private fun convertToRTF(htmlStr: String): String? {
    var htmlStr = htmlStr

    val os = ByteArrayOutputStream()
    val htmlEditorKit = HTMLEditorKit()
    val rtfEditorKit = RTFEditorKit()
    var rtfStr: String? = null

    htmlStr = htmlStr.replace("<br.*?>".toRegex(), "#NEW_LINE#")
    htmlStr = htmlStr.replace("</p>".toRegex(), "#NEW_LINE#")
    htmlStr = htmlStr.replace("<p.*?>".toRegex(), "")
    val `is` = ByteArrayInputStream(htmlStr.toByteArray())
    try {
        val doc = htmlEditorKit.createDefaultDocument()
        htmlEditorKit.read(`is`, doc, 0)
        rtfEditorKit.write(os, doc, 0, doc.length)
        rtfStr = os.toString()
        rtfStr = rtfStr.replace("#NEW_LINE#".toRegex(), "\\\\par \\\\par ")
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: BadLocationException) {
        e.printStackTrace()
    }

    return rtfStr
}