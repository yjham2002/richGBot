package util;

import word.w2004.Document2004;
import word.w2004.elements.BreakLine;
import word.w2004.elements.PageBreak;
import word.w2004.elements.Paragraph;
import word.w2004.elements.ParagraphPiece;
import word.w2004.style.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by a on 2017-05-12.
 */
public class ConfirmReport extends Document2004{

    public static final String TEMPLATE = "위 본인은 직원으로서 사규를 준수하고 맡은 바 책임과 의무를 다하여 성실히 근무하여야 함에도 불구하고, %s(으)로 인해 회사의 관련 규정을 위반하였는 바, 이에 시말서를 제출하고 차후 본건을 계기로 과오의 재발이 없을 것임을 서약합니다.";

    private String name;
    private String reason;
    private String content;

    public ConfirmReport(String name, String reason, String content){
        super();
        this.name = name;
        this.reason = reason;
        this.content = content;
        write();
    }

    private void write(){
        Calendar c = Calendar.getInstance();
        String date = c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월" + c.get(Calendar.DAY_OF_MONTH) + "일";
        this.addEle(BreakLine.times(2).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with("시말서").withStyle().font(Font.CENTURY_GOTHIC).fontSize("18").bold().create()).create());
        this.addEle(BreakLine.times(3).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with("성명 : " + this.name).withStyle().font(Font.CENTURY_GOTHIC).fontSize("10").bold().create()).create());
        this.addEle(BreakLine.times(3).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with(String.format(TEMPLATE, this.reason)).withStyle().font(Font.CENTURY_GOTHIC).fontSize("10").create()).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with(this.content).withStyle().font(Font.CENTURY_GOTHIC).fontSize("10").create()).create());
        this.addEle(BreakLine.times(5).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with(date).withStyle().font(Font.CENTURY_GOTHIC).fontSize("10").bold().create()).create());
        this.addEle(BreakLine.times(2).create());
        this.addEle(Paragraph.withPieces(ParagraphPiece.with(this.name + " (   인   )").withStyle().font(Font.CENTURY_GOTHIC).fontSize("10").bold().create()).create());

    }

    public String writeFile(){

        Calendar c = Calendar.getInstance();
        String date = c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월" + c.get(Calendar.DAY_OF_MONTH) + "일" + ".doc";

        String desc = "Confirm_Report_" + this.name + "_" + date.replaceAll(" ", "");

        File fileObj = new File(desc);
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileObj);
        } catch (FileNotFoundException e) {
            System.out.println("파일이 사용 중이거나 접근할 권한이 없습니다.");
            return null;
        }
        String myWord = this.getContent();

        writer.println(myWord);
        writer.close();

        return desc;
    }

    public static void main(String... args){
        new ConfirmReport("전세호", "지각", "ㅇㅅㅇ").writeFile();
    }

}
