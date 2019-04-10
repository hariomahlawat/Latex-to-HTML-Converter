/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                    PARSER RULES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
------------------------------------------------------------------------------------------------------------------------
*/
grammar Tex_grammar;
root : docclass preamble* begindocument title* author* date*  body_text enddocument EOF ;
docclass    :   DOCCLASS;
preamble    :   PKGAMSMATH
            |   PKGBALANCE
            |   PKGCAPTION
            |   PKGGRAPHICS
            |   PKGGRAPHICX
            |   PKGMATHTOOLS
            |   PKGMULTIROW
            |   PKGSUBCAPTION
            |   PKGTABULARX
            |   PKGURL
            ;

begindocument   :   BEGINDOC    ;
enddocument     :   ENDDOC  ;


body_text   :   body_text body_parts
            |   body_parts
            ;


body_parts  :    optionalAbstract|section | subsection | label|ref| ordered_list |unordered_list | maketitle | figure|fulltext  ;

title           :   TITLE NEWLINE*  ;
author          :   AUTHOR NEWLINE* ;
date            :   DATE NEWLINE*  ;
maketitle       :   MAKE_TITLE;


optionalAbstract    :   BEGIN_ABSTRACT NEWLINE* fulltext NEWLINE* END_ABSTRACT   ;
section     :    sectionName fulltext* text* fulltext*
            ;

sectionName :   SECTION' '*'{' text'}'NEWLINE*  ;

subsection  :   subsectionName  fulltext text* fulltext*
            ;

subsectionName  :   SUBSECTION ' '*'{' text'}'NEWLINE*   ;

par             :   PAR ;
center          :   BEGIN_CENTER fulltext* END_CENTER ;
graphicsHW      :   '[' TEXT ']';
graphicsName    :   '{' WORD '}' NEWLINE*;
graphics        :   INCLUDEGRAPHICS graphicsHW? graphicsName (' '|'\r\n')*
                ;
figure          :   BEGINFIGURE graphics  label? caption? label? ENDFIGURE    | BEGINFIGURE  caption? graphics  ENDFIGURE ;

ordered_list    :   (BEGIN_ENUMERATE item+ END_ENUMERATE)

                ;

unordered_list  :   BEGIN_ITEMIZE item+ END_ITEMIZE
                ;

item            :   (ITEM listtext)+
                |   ordered_list item
                |   unordered_list item
                ;

listtext        :   ' '* text* NEWLINE*;

stat            :   expr    NEWLINE?
                |   ID '=' expr NEWLINE?
                |   NEWLINE
                ;
expr            :   expr    ('*'|'/'|'+'|'-') expr
                |   INTEGER
                |   ID
                |   '(' expr ')'

                ;

operator        :   OPERATOR;
superscript     :   SUPEQN;
subscript       :   SUBEQN;
frac            :   FRAC  fracdata NEWLINE*;
sqrt            :   SQRT    mathdata    NEWLINE*
                |   SQRT frac
                |   SQRT integral
                ;
sum             :   SUM SUBSCRIPT sumsubscript SUPERSCRIPT sumsuperscript NEWLINE*;
integralSign    :   INTEGRATION;
integral        :   integralSign SUBSCRIPT sumsubscript SUPERSCRIPT sumsuperscript NEWLINE*
                |   integralSign
                ;
integration     :   integral expr*;
sumsubscript    :   LEFTCURLYB (EQUALTO|ID|WORD|LETTERS|' '|SINGLECHAR) EQUALTO? (EQUALTO|ID|WORD|LETTERS|' '|SINGLECHAR)? RIGHTCURLYB;
sumsuperscript    :   LEFTCURLYB (EQUALTO|ID|WORD|LETTERS|' '|SINGLECHAR) EQUALTO? (EQUALTO|ID|WORD|LETTERS|' '|SINGLECHAR)? RIGHTCURLYB;
mathdata        :   LEFTCURLYB (EQUALTO|ID|WORD|LETTERS|' '|SINGLECHAR|sqrt|sum|integral) RIGHTCURLYB ;
fracdata        :   fracPart fracPart;
fracPart        :   LEFTCURLYB text RIGHTCURLYB
                |   LEFTCURLYB (sqrt|sum|integral) RIGHTCURLYB

                ;
inlinemath      :   SINGLEDOLLAR (text | superscript |integration| subscript |sum|frac|sqrt ' '|operator)+ SINGLEDOLLAR ;
displaymath     :   DOUBLEDOLLAR (superscript | subscript |sum|frac|sqrt|integration|text+|LEFTCURLYB|RIGHTCURLYB|LEFTPARENTHESES|RIGHTPARENTHESES | superscript | subscript |sum|frac|sqrt| ' '|operator)+ DOUBLEDOLLAR;

begin_table     :   BEGIN_TABLE;
end_table       :   END_TABLE;
begin_tabular   :   BEGIN_TABULAR;
end_tabular     :   END_TABULAR;
table_alignment :   TABLE_ALIGNMENT;
table_row       :   ((' '*(table_column ' '+)+ ' '+)+   '\t'*' '* ) doubleslash?|'\\\\' ' '* ;
doubleslash     :   DOUBLESLASH;
table_column    :   (' ' | '\t')* text* (' ' | '\t')* AMPERSAND?(' ' | '\t')* ;
table           :   (begin_table? begin_tabular table_alignment?) (table_row ' '* doubleslash?)+  (end_tabular end_table?) ;


latex_newline   :   DOUBLESLASH | LATEX_NEWLINE | HFILL ' '* BREAK ;
boldtext        :   TEXTBF '{' text '}' ' '*;
italictext      :   TEXTIT  '{' text    '}';
underline       :   UNDERLINE  '{' text '}'  ;
caption         :   CAPTION '{' expr '}';
label           :   LABEL '{' text '}';
ref             :   REF '{' text '}';
text            :   NEWLINE*  TEXT | WORD|INTEGER | LETTERS|'-'|' '|SINGLECHAR|EQUALTO|PLUS|MUL|'='|stat|'('|')';
fulltext        :   fulltext ' '* text
                |   fulltext boldtext text?
                |   fulltext italictext text?
                |   fulltext underline text?
                |   fulltext caption
                |   fulltext frac
                |   fulltext    sqrt
                |   fulltext sum
                |   fulltext integration
                |   fulltext inlinemath
                |   fulltext displaymath
                |   fulltext table
                |   fulltext label
                |   fulltext ref
                |   fulltext center
                |   fulltext par
                |   text underline
                |   text boldtext
                |   text italictext
                |   inlinemath
                |   displaymath
                |   table
                |   boldtext
                |   italictext
                |   underline
                |   caption
                |   frac
                |   sqrt
                |   sum
                |   integration
                |   label
                |   ref
                |   par
                |   center
                |   latex_newline* text latex_newline* text*
                ;



word            :   WORD;
letter          :   LETTERS;
integer         :   INTEGER;
sentence        :   (word|letter|integer|' ')+;



line            :   (' '|'\t')* (WORD(' '|'\t')*)* ('\r'|'\n'|'\r\n');






/*----------------------------------------------------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                LEXER RULES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
------------------------------------------------------------------------------------------------------------------------
*/

ID              :   [a-zA-Z]+ ; // match lower-case identifiers



DOUBLESLASH     :   '\\\\'      ;
LATEX_NEWLINE   :   '\\newline' ;
HFILL           :   '\\hfill'   ;
BREAK           :   '\\break'   ;
FRAC            :   '\\frac'    ;
SQRT            :   '\\sqrt'    ;
SUM             :   '\\sum'    ;
INTEGRATION     :   '\\int'     ;
LEFTCURLYB      :   '{' ;
RIGHTCURLYB     :   '}' ;
LEFTSQUAREB     :   '[' ;
RIGHTSQUAREB    :   ']' ;
LEFTPARENTHESES :   '(' ;
RIGHTPARENTHESES   :    ')' ;
EQUALTO         :   '=';
PLUS            :   '+';
MUL             :   '*';
SUPERSCRIPT     :   '^';
SUBSCRIPT       :   '_';
AMPERSAND       :   '&';
DOUBLEDOLLAR    :   '$$';
SINGLEDOLLAR    :   '$';
SUPEQN          :   (ID|WORD|LETTERS|SINGLECHAR) SUPERSCRIPT (ID|WORD|LETTERS|SINGLECHAR);
SUBEQN          :   (ID|WORD|LETTERS|SINGLECHAR) SUBSCRIPT (ID|WORD|LETTERS|SINGLECHAR);

DOCCLASS        :   '\\documentclass[sigconf]{acmart}' NEWLINE*;
PKGBALANCE      :   '\\usepackage{balance}' NEWLINE*;
PKGGRAPHICX     :   '\\usepackage{graphicx}' NEWLINE*;
PKGURL          :   '\\usepackage{url}' NEWLINE*;
PKGAMSMATH      :   '\\usepackage{amsmath}' NEWLINE*;
PKGMATHTOOLS    :   '\\usepackage{mathtools}' NEWLINE*;
PKGTABULARX     :   '\\usepackage{tabularx}' NEWLINE*;
PKGCAPTION      :   '\\usepackage{caption}' NEWLINE*;
PKGSUBCAPTION   :   '\\usepackage{subcaption}' NEWLINE*;
PKGMULTIROW     :   '\\usepackage{multirow}' NEWLINE*;
PKGGRAPHICS     :   '\\usepackage{graphics}' NEWLINE*;


BEGINDOC        :   '\\begin{document}' NEWLINE*;
ENDDOC          :   '\\end{document}' NEWLINE*;

BEGINFIGURE     :   '\\begin{figure}'   ;
ENDFIGURE       :   '\\end{figure}' ;
INCLUDEGRAPHICS :   '\\includegraphics';
CAPTION         :   '\\caption';

BEGIN_CENTER    :   '\\begin{center}';
END_CENTER      :   '\\end{center}';
TITLE           :   '\\title''{' TEXTPART '}';
AUTHOR          :   '\\author''{'TEXTPART'}';
DATE            :   '\\date''{'TEXTPART'}';
MAKE_TITLE      :   '\\maketitle';

BEGIN_ABSTRACT  :   '\\begin{abstract}';
END_ABSTRACT    :  '\\end{abstract}';
SECTION         :   '\\section';
SUBSECTION      :   '\\subsection';
PARAGRAPH       :   '\\paragraph'  ;
TEXTBF          :   '\\textbf';
TEXTIT          :   '\\textit';
UNDERLINE       :   '\\underline';
PAR             :   '\\par' ;


LABEL           :   '\\label';
REF             :   '\\ref';
BEGIN_ENUMERATE :   '\\begin{enumerate}'  NEWLINE*;
ITEM            :   '\\item';
END_ENUMERATE   :   '\\end{enumerate}'NEWLINE*;
BEGIN_ITEMIZE   :   '\\begin{itemize}'NEWLINE*;
END_ITEMIZE     :   '\\end{itemize}'NEWLINE*;
//table tokens
BEGIN_TABLE     :   '\\begin{table}';
END_TABLE       :   '\\end{table}';
BEGIN_TABULAR   :   '\\begin{tabular}';
END_TABULAR     :   '\\end{tabular}';
TABLE_ALIGNMENT :   ('{'('|'? ('l'|'r'|'c')? '|'? ('l'|'r'|'c')? '|'? ('l'|'r'|'c')? '|'? )'}') ;


HLINE           :   '\\hline';




WORD		    :   (((PUNCTUATION)|[a-zA-Z0-9]|(GREEK_LETTERS))+|'('|')');

GREEK_LETTERS   :    '\\alpha'
                |'\\beta'|'\\gamma'|'\\delta'|'\\epsilon'
                |'\\rho'|'\\sigma'|'\\tau'|'\\upsilon'|'\\phi'|'\\psi'|'\\omega'|'\\Alpha'
                |'\\zeta'|'\\eta'|'\\theta'|'\\iota'|'\\kappa'
                |'\\lambda'|'\\mu'|'\\nu'|'\\xi'|'\\omicron'|'\\pi'
                |'\\Tau'|'\\Upsilon'|'\\Phi'|'\\Psi'|'\\Omega'|'\\int'|'\\inf'|'\\exp'
                |'\\Beta'|'\\Gamma'|'\\Delta'|'\\Epsilon'|'\\Zeta'|'\\Eta'|'\\Theta'|'\\Iota'
                |'\\Kappa'|'\\Lambda'|'\\Mu'|'\\Nu'|'\\Xi'|'\\Omicron'|'\\Pi'|'\\Rho'|'\\Sigma'
                ;
RESERVEWORD     :   ('\\' [a-zA-Z] | '\\'[a-zA-Z*]+);
SPECIALCHAR     : ('_'|'&'|'$'|'#'|'@');
OPERATOR        :	'+'|'-'|'=';
SKIP2           :   '\r\n'  ->skip;
NEWLINE         : ('\r'? '\n')   ->skip ;

WS              : (' '|'\t'|'\n'|'\r')+  ->skip    ;
INTEGER	        :	[0-9]+ ;
SINGLECHAR      :   '.';
LETTERS         :		[a-zA-Z];
TEXT            :   [a-zA-Z0-9_]TEXTPART*('\r'|'\n')*| ':' | '.' | '&' | '/'  | ';'|' '|'//'|' '*  ;




fragment HEXCHARS       :   '#' [0-9a-fA-F]+ ;
fragment HEXCHARS2      :   '&#'[0-9a-fA-F]+ ;
fragment PUNCTUATION    :   ('.'|'\'|!|'?'|:|;|');
fragment TEXTPART       :   [a-zA-Z0-9][a-zA-Z0-9 '\t''.''?'',''!'':'';'' ''-''=']*;



