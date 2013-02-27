package com.gravitydev.optimizer

import scala.collection.JavaConversions._
import org.apache.commons.io.FileUtils
import com.google.common.css.JobDescriptionBuilder
import com.google.common.css.SourceCode
import com.google.common.css.compiler.ast.{GssParser, BasicErrorManager}
import com.google.common.css.compiler.passes.{PassRunner, CompactPrinter, PrettyPrinter}
import java.io.{File, FileOutputStream, OutputStreamWriter}
    
class CssLoader (optimize: Boolean, assumeCompiled: Boolean, sourceDirFn: String => String, outputDir: String, includePath: String, 
    cacheBuster: String, logger: AnyRef => Unit) {
  
  def includeStyles (styles: List[String]) = {
    import java.io.{File, FileOutputStream, OutputStreamWriter}
    
    if (optimize) {
      val compiledCssName = styles.mkString(";").hashCode.toHexString
              
      if (!assumeCompiled) {
        val compiledCss = new FileOutputStream(new File(outputDir + "/"+compiledCssName+".css"))
        val sw = new OutputStreamWriter(compiledCss, "utf-8")

        val compiled = printCss(styles map sourceDirFn, logger)
  
        def fixColor (s:String) = {
          if (s.length == 4) {
            s.head + (s.tail map {a => a.toString + a} mkString(""))
          } else s 
        }
        
        // fix IE gradient filters
        val fixed = "Colorstr=(#[0-9a-fA-F]{6})".r.replaceAllIn(compiled, s => "Colorstr='" + fixColor(s.group(1).toUpperCase) + "'")
        
        sw.write( fixed )
        
        sw.close()
      }
  
      List(includePath+"/"+compiledCssName+".css?"+cacheBuster)
    } else styles
  }
    
  private def printCss (cssFiles: List[String], log: AnyRef => Unit) = {
    val job = new JobDescriptionBuilder()
      .setInputs(cssFiles map {f => new SourceCode(f, FileUtils.readFileToString(new java.io.File(f)))})
      .setAllowUnrecognizedFunctions(false)
      .setAllowUnrecognizedProperties(false)
      .setAllowedNonStandardFunctions(List(
          "color-stop",
          "progid:DXImageTransform.Microsoft.gradient",
          "progid:DXImageTransform.Microsoft.Shadow"
      ))
      .getJobDescription()
      
    val em = new BasicErrorManager {
      def print (s: String) = log(s)
    }
      
    val passRunner = new PassRunner(job, em)
    
    val s = new SourceCode(null, "")
    
    val parser = new GssParser(job.inputs)
    val tree = parser.parse()
    
    passRunner.runPasses(tree)
    
    //val printer = new CompactPrinter(tree)
    val printer = new PrettyPrinter(tree.getVisitController())
    printer.runPass()
    
    em.generateReport()
    
    //printer.getCompactPrintedString()
    printer.getPrettyPrintedString()
  }
}
