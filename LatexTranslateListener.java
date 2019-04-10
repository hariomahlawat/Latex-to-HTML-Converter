import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.*;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.StringBuilder;

public  class LatexTranslateListener extends Tex_grammarBaseListener{
    int sectionNo=0;
    int subSectionNo=0;
    int makeTitle=0;
    int figureNo=0;
    int tableRowCount=0;
    int fracPart=0;
    boolean insidefigure = false;
    boolean insideSection = false;
    boolean insideSubSection=false;
    boolean insideLabel=false;
    boolean insideRef=false;
    String title="";
    String author="";
    String date="";
    String imageHeight="";
    String imageWidth="";
    String imageName="";
    String imageCaption="";
    String imageHTML="";
    String titleBox="";
    int inlineMath = 0;
    int displayMath = 0;
    StringBuilder sb = new StringBuilder();
    Map<String, String> map = new HashMap<String, String>();


    String test = "";

/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                  CSS STYLE CODES FOR PAGE LAYOUT, HEADING, SECTION, SUBSECTION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    String style="<!-- CSS code for the html file -->"+
            "<style>\n" +
        "    body {\n" +
        "       font-size:10px;\n" +
        "       margin-left:20%;\n" +
        "       margin-right:18%;\n" +
        "       margin-top:3%;\n" +
        "       text-align: justify;\n" +
        "       text-justify: inter-word;    }\n" +
        "    .headingBox{\n" +
        "       text-align: center;\n" +
        "    }\n" +
        "    .heading{\n" +
        "       font-size:14px;\n" +
        "        \n" +
        "    }\n" +
        "    .author{\n" +
        "       font-size:10px;\n" +
        "       text-align: center;\n" +
        "    }\n" +
        "   .abstractHeading{\n" +
        "       font-size:9px;\n" +
        "       font-weight:bold;\n" +
        "       text-align: center;   \t\n" +
        "   }   \n" +
        "   .abstract{\n" +
        "       font-size:9px;\n" +
        "       text-align: justify;\n" +
        "       margin-left:13%;\n" +
        "       margin-right:13%;    }    \n" +
        "    .section{\n" +
        "       font-size:12px;\n" +
        "       font-weight:bold;\n" +
        "    }\n" +
        "    .subsection{\n" +
        "       font-size:11px;\n" +
        "       font-weight:bold;\n" +
        "    }\n" +
        "    .displaymath{\n" +
        "       text-align: center;\n" +
        "    }\n" +
        "   .center{\n" +
        "       text-align: center;\n" +
        "    }    \n" +
        "    table {\n" +
        "        border-collapse: collapse;\n" +
        "    }\n" +
        "    \n" +
        "    table, th, td {\n" +
        "       border: 1px solid black;\n" +
        "    }    \n" +
        " </style> ";

    CustomMethods cm = new CustomMethods();

    public String findTextBetweenBraces(String s){
        s= s.trim();
        int firstIndex = s.indexOf('{');
        int lastIndex = s.indexOf('}');

        return  s.substring(firstIndex+1,lastIndex);
    }


        @Override public void enterRoot(Tex_grammarParser.RootContext ctx) {

            sb.append("<!DOCTYPE html>");
            sb.append("\n");
            sb.append("<html> \n <head> \n "+style+" \n </head>");
            sb.append("\n");

        }

        @Override public void exitRoot(Tex_grammarParser.RootContext ctx) {
            sb.append("</html>");
            sb.append("\n");

        }


/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                    Title, author and date
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
    @Override public void enterTitle(Tex_grammarParser.TitleContext ctx) {
        String s = ctx.getText();
        title = findTextBetweenBraces(s);

    }

    @Override public void enterAuthor(Tex_grammarParser.AuthorContext ctx) {
        String s = ctx.getText();
        author = findTextBetweenBraces(s);
    }

    @Override public void enterDate(Tex_grammarParser.DateContext ctx) {
        String s = ctx.getText();
        date = findTextBetweenBraces(s);
    }

    @Override public void enterMaketitle(Tex_grammarParser.MaketitleContext ctx) {
        if (makeTitle==0)  // To confirm that maketitile is occuring first time in latex document.
        {
            sb.append("\n");
            sb.append("<p class=\"headingBox\">");
            sb.append("\n");
            sb.append("<span class=\"heading\">");
            sb.append(title);
            sb.append("</span>");
            sb.append("<br><br>");
            sb.append("<span class=\"author\">");
            sb.append(author);
            sb.append("<br><br>");
            sb.append(date);
            sb.append("</span>");
            sb.append("\n");

        }
    }

    @Override public void exitMaketitle(Tex_grammarParser.MaketitleContext ctx) {

        if ( makeTitle==0){
            sb.append("</p>");
            sb.append("\n");
            sb.append("<br>");
        }
        makeTitle=1;  //To lock out the maketile tag. Any reoccurence of maketitle tag will not be entertained
    }

/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                           Begin Document, Section, Subsection, Paragraph, par
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
        @Override public void enterBegindocument(Tex_grammarParser.BegindocumentContext ctx) {
            sb.append("\n <!-- Main body starts -->");
            sb.append("\n<body>");
            sb.append("\n");

        }



        @Override public void exitEnddocument(Tex_grammarParser.EnddocumentContext ctx) {

            sb.append("\n</body>");
            sb.append("\n");
        }
        // Abstract
        @Override public void enterOptionalAbstract(Tex_grammarParser.OptionalAbstractContext ctx) {
            sb.append("<!-- Abstract starts from here --> \n");
            sb.append("\n<p class=\"abstractHeading\">");
            sb.append("Abstract");
            sb.append("</p>\n");
            sb.append("<p class=\"abstract\">");

        }

        @Override public void exitOptionalAbstract(Tex_grammarParser.OptionalAbstractContext ctx) {
            sb.append("\n</p>\n");
        }
        // Section and subsection

        @Override public void enterSection(Tex_grammarParser.SectionContext ctx) {
            insideSection = true;
            insideSubSection = false;
            sectionNo = sectionNo+1;
            subSectionNo=0;
            sb.append("\n");
            sb.append("<!-- Section starts from here --> \n");
            sb.append("<p>");
            sb.append("\n");
        }

        @Override public void exitSection(Tex_grammarParser.SectionContext ctx) {
           // insideSection=false;
            //sb.append("\n");
            //sb.append("</p>");

        }

        @Override public void enterSectionName(Tex_grammarParser.SectionNameContext ctx) {

            sb.append("<span class=\"section\">");
            sb.append(sectionNo+"&emsp;");


        }

        @Override public void exitSectionName(Tex_grammarParser.SectionNameContext ctx) {
            sb.append("</span>");
            sb.append("<br>");
            sb.append("<br>");
        }

        @Override public void enterSubsection(Tex_grammarParser.SubsectionContext ctx) {

            insideSubSection =true;
            subSectionNo = subSectionNo+1;
            sb.append("\n");
            sb.append("<!-- SubSection starts from here --> \n");
            sb.append("<p>");
            sb.append("\n");

        }

        @Override public void exitSubsection(Tex_grammarParser.SubsectionContext ctx) {
            insideSubSection = false;
            //sb.append("<br>");
           // sb.append("</p>");


        }

        @Override public void enterSubsectionName(Tex_grammarParser.SubsectionNameContext ctx) {

            sb.append("<span class=\"subsection\">");
            sb.append(sectionNo+"."+subSectionNo+"&emsp;");
        }

        @Override public void exitSubsectionName(Tex_grammarParser.SubsectionNameContext ctx) {
            sb.append("</span>");
            sb.append("<br>");
            sb.append("<br>");
        }

        @Override public void enterPar(Tex_grammarParser.ParContext ctx) {
            sb.append("\n  </br>&emsp; &emsp;");
        }


/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                           Ordered and Unordered list
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
        @Override public void enterOrdered_list(Tex_grammarParser.Ordered_listContext ctx) {
            sb.append("\n");
            sb.append("<!-- Ordered List (Enumerate) starts from here --> \n");
            sb.append("<ol>");
            sb.append("\n");
        }

        @Override public void exitOrdered_list(Tex_grammarParser.Ordered_listContext ctx) {

            sb.append("</ol>");
            sb.append("\n");
        }

        @Override public void enterUnordered_list(Tex_grammarParser.Unordered_listContext ctx) {
            sb.append("\n");
            sb.append("<!-- Unordered List (Itemize) starts from here --> \n");
            sb.append("<ul>");
            sb.append("\n");
        }

        @Override public void exitUnordered_list(Tex_grammarParser.Unordered_listContext ctx) {

            sb.append("</ul>");
            sb.append("\n");
        }


        @Override public void enterListtext(Tex_grammarParser.ListtextContext ctx) {
        test="";
        test=ctx.getText();

        test="<li>"+test;

        sb.append("\t<li>");
        }

        @Override public void exitListtext(Tex_grammarParser.ListtextContext ctx) {
        String s;
        sb.append("\t</li>");
        sb.append("\n");
        }

/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                TEXT TRAMSFORMATIONS : Bold , Italic, underline and latex-newline text segments, CENTRE
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

       @Override public void enterBoldtext(Tex_grammarParser.BoldtextContext ctx) {
            sb.append("<b>");
       }

        @Override public void exitBoldtext(Tex_grammarParser.BoldtextContext ctx) {
            sb.append("</b> ");
        }

        @Override public void enterItalictext(Tex_grammarParser.ItalictextContext ctx) {
            sb.append("<i>");
        }

        @Override public void exitItalictext(Tex_grammarParser.ItalictextContext ctx) {
            sb.append("</i> ");
        }

        @Override public void enterLatex_newline(Tex_grammarParser.Latex_newlineContext ctx) {
            sb.append("<br>");
        }

        @Override public void enterUnderline(Tex_grammarParser.UnderlineContext ctx) {
        sb.append("<u>");
        }

        @Override public void exitUnderline(Tex_grammarParser.UnderlineContext ctx) {
        sb.append("</u> ");
        }

        @Override public void enterCenter(Tex_grammarParser.CenterContext ctx) {
           sb.append("\n" +
                   "<p class=\"center\">" +
                   "\n");
        }

        @Override public void exitCenter(Tex_grammarParser.CenterContext ctx) {
           sb.append("\n");
           sb.append("</p>");
           sb.append("\n");
        }



/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                GRAPHICS AND IMAGE SECTION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
        @Override public void enterFigure(Tex_grammarParser.FigureContext ctx) {
            insidefigure =true;
            figureNo = figureNo+1;
            sb.append("\n");
            sb.append("<!-- Figure Environment starts from here --> \n");
            sb.append("<figure> ");
            sb.append("\n");
        }



        @Override public void exitGraphicsHW(Tex_grammarParser.GraphicsHWContext ctx) {
            String s = ctx.getText();
            imageHeight = cm.extractImageHeight(s);
            imageWidth = cm.extractImageWidth(s);

        }



        @Override public void exitGraphicsName(Tex_grammarParser.GraphicsNameContext ctx) {
            String s = ctx.getText();
            imageName = cm.extractImageName(s);

        }



        @Override public void exitGraphics(Tex_grammarParser.GraphicsContext ctx) {
            imageHTML=
                    "    <img src=\""+imageName+"\"  style=\"width:" +imageWidth +"; height:"+imageHeight+"\">\n"



                    ;

            sb.append(imageHTML);
        }


        @Override public void enterCaption(Tex_grammarParser.CaptionContext ctx) {
            //String caption="";
            imageCaption = cm.findTextBetweenBraces(ctx.getText());
            //sb.append("<caption>");
            //sb.append(caption);
            //sb.append("</caption>");
        }

        @Override public void exitCaption(Tex_grammarParser.CaptionContext ctx) {
            sb.append("    <figcaption> " +"Fig:" +figureNo+" "+imageCaption+ "</figcaption>\n");
        }

    @Override public void exitFigure(Tex_grammarParser.FigureContext ctx) {
            insidefigure=false;
            sb.append("</figure>");
            sb.append("\n");
        }
    //------------------------------------------------------------------


/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                MATH MODES AND MATH FUNCTIONS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

        @Override public void enterInlinemath(Tex_grammarParser.InlinemathContext ctx) {
            inlineMath =1;
        }

        @Override public void exitInlinemath(Tex_grammarParser.InlinemathContext ctx) {
            inlineMath=0;
        }

        @Override public void enterDisplaymath(Tex_grammarParser.DisplaymathContext ctx) {
            displayMath=1;
            sb.append("\n");
            sb.append("<p class=\"displaymath\">");
            sb.append("\n");
        }

        @Override public void exitDisplaymath(Tex_grammarParser.DisplaymathContext ctx) {
            displayMath=0;
            sb.append("\n");
            sb.append("</p>");
        }




        @Override public void exitSubscript(Tex_grammarParser.SubscriptContext ctx) {
            String s = ctx.getText();
            s.trim();
            int index = s.indexOf('_');
            String first = s.substring(0,index);
            String secong = s.substring(index+1);
            sb.append(first+"<sub>"+secong+"</sub> ");
        }

        @Override public void exitSuperscript(Tex_grammarParser.SuperscriptContext ctx) {
            String s = ctx.getText();
            s.trim();
            int index = s.indexOf('^');
            String first = s.substring(0,index);
            String secong = s.substring(index+1);
            sb.append(first+"<sup>"+secong+"</sup> ");

        }

        @Override public void exitSqrt(Tex_grammarParser.SqrtContext ctx) {
            String s = ctx.getText();
            String mathPart = cm.findTextBetweenBraces(s);
            sb.append(" &#8730;"+mathPart);
        }

        @Override public void exitFrac(Tex_grammarParser.FracContext ctx) {
            //String s = ctx.getText();
            //String firstPart = cm.extractFirstFracPart(s);
            //String secondPart = cm.extractSecondFracPart(s);
            //String htmlFrac = firstPart + " &#x2215; " + secondPart +" " ;
            //sb.append(htmlFrac);
        }

        @Override public void enterFracPart(Tex_grammarParser.FracPartContext ctx) {
            if (fracPart==1) sb.append(" &#x2215; ");
            if (fracPart==0){
                fracPart=1;
            } else fracPart=0;
        }

        @Override public void enterSum(Tex_grammarParser.SumContext ctx) {
            sb.append("&sum;");
        }

        @Override public void exitSum(Tex_grammarParser.SumContext ctx) { }

        @Override public void exitSumsubscript(Tex_grammarParser.SumsubscriptContext ctx) {
            String sub = cm.findTextBetweenBraces(ctx.getText());
            sb.append("<sub>"+sub+"</sub>");
        }

        @Override public void exitSumsuperscript(Tex_grammarParser.SumsuperscriptContext ctx) {
            String sup = cm.findTextBetweenBraces(ctx.getText());
            sb.append("<sup>"+sup+"</sup> ");
        }

        //---Definite and Indefinite Integral
        @Override public void enterIntegralSign(Tex_grammarParser.IntegralSignContext ctx) {
            sb.append("&int;");
        }


        @Override public void exitOperator(Tex_grammarParser.OperatorContext ctx) {
            sb.append(ctx.getText());
         }

/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                TABULAR ENVIRONMENT
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

        @Override public void enterTable(Tex_grammarParser.TableContext ctx) { }

        @Override public void exitTable(Tex_grammarParser.TableContext ctx) { }

        @Override public void enterBegin_table(Tex_grammarParser.Begin_tableContext ctx) {
            sb.append("<!-- Tabular Environment starts from here --> \n");
            sb.append("<div class=\"table_div\">\n" );
        }

        @Override public void enterBegin_tabular(Tex_grammarParser.Begin_tabularContext ctx) {
            sb.append("<table class=\"table\">\n");
        }



        @Override public void enterTable_alignment(Tex_grammarParser.Table_alignmentContext ctx) { }

        @Override public void exitTable_alignment(Tex_grammarParser.Table_alignmentContext ctx) { }

        //-------------- Table Row-----------------------------
        @Override public void enterTable_row(Tex_grammarParser.Table_rowContext ctx) {

                sb.append("\t<tr> \n");


        }

        @Override public void exitTable_row(Tex_grammarParser.Table_rowContext ctx) {
            sb.append(" \n\t</tr> \n");
            tableRowCount=tableRowCount+1;
        }
        //------------------------------------------------
        //----------------- Table Column ----------------------------------
        @Override public void enterTable_column(Tex_grammarParser.Table_columnContext ctx) {
            if (  tableRowCount==0  ){
                sb.append("\t<th>");
            }
            else{
                sb.append("\t<td>");
            }

        }

         @Override public void exitTable_column(Tex_grammarParser.Table_columnContext ctx) {
             if (  tableRowCount==0  ){
                 sb.append("</th>");
             }
             else{
                 sb.append("</td>");
             }
         }

        //------------------------------------------------------------


        @Override public void exitEnd_tabular(Tex_grammarParser.End_tabularContext ctx) {
            tableRowCount=0;
            sb.append("</table>\n");
        }



        @Override public void exitEnd_table(Tex_grammarParser.End_tableContext ctx) {
            sb.append("</div>"+"\n");
        }



/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                REF AND LABELS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

        @Override public void enterLabel(Tex_grammarParser.LabelContext ctx) {
            insideLabel=true;
            sb.append("<a id=");
        }

        @Override public void exitLabel(Tex_grammarParser.LabelContext ctx) {
            String label = findTextBetweenBraces(ctx.getText());
            String labelValue="";
            if (insidefigure==true){
                labelValue= Integer.toString(figureNo) ;

            }else if (insideSubSection == true){
                labelValue = Integer.toString(sectionNo)+"."+Integer.toString(subSectionNo);

            }else {
                labelValue = Integer.toString(sectionNo);
            }
            map.put (label,labelValue);

            sb.append("\"");
            sb.append(label);

            sb.append("\">");
            sb.append("</a>");
            insideLabel=false;

        }

        @Override public void enterRef(Tex_grammarParser.RefContext ctx) {
            insideRef=true;
            String refName = findTextBetweenBraces(ctx.getText());
            String refValue=map.get(refName);
            sb.append("<a href=\"#"+refName+"\">");
            sb.append(refValue);


        }

        @Override public void exitRef(Tex_grammarParser.RefContext ctx) {

            sb.append("</a> ");
            insideRef=false;
        }


        @Override public void enterText(Tex_grammarParser.TextContext ctx) {
            if (insideLabel==false && insideRef==false) {
                sb.append(ctx.getText());
            }
        }







}