import com.itextpdf.text.*;
import timetable.Session;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;


public class MakePDF {
    //This method sets up headers for the timetable
    private static void makeHeader(PdfPTable table, Font font){
        PdfPCell titleCell = new PdfPCell();
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setColspan(6);
        titleCell.addElement(new Phrase("Timetable", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD))); // composite mode
        table.addCell(titleCell);

        // Column titles
        PdfPCell timeslotTitle = new PdfPCell(new Paragraph("Time", font));
        PdfPCell MondayTitle = new PdfPCell(new Paragraph("Monday", font));
        PdfPCell TuesdayTitle = new PdfPCell(new Paragraph("Tuesday", font));
        PdfPCell WednesdayTitle = new PdfPCell(new Paragraph("Wednesday", font));
        PdfPCell ThursdayTitle = new PdfPCell(new Paragraph("Thursday", font));
        PdfPCell FridayTitle = new PdfPCell(new Paragraph("Friday", font));

        table.addCell(timeslotTitle);
        table.addCell(MondayTitle);
        table.addCell(TuesdayTitle);
        table.addCell(WednesdayTitle);
        table.addCell(ThursdayTitle);
        table.addCell(FridayTitle);
    }

    public static String[] allValidKey(int x, int y){
        String strY = String.valueOf(y);
        if (strY.length() < 2) {
            strY = "0" + strY;
        }
        String singleHrKey = x + strY + (y + 1);
        String dualHrKey = x + strY + (y + 2);
        String tripleHrKey = x + strY + (y + 3);
        return new String[]{singleHrKey, dualHrKey, tripleHrKey};
    }

    private static void addCourses(TreeMap<String, Session> timetable, PdfPTable table, Font font){
        ArrayList<Integer> boxed = new ArrayList<>();
        PdfPCell emptyCell = new PdfPCell();

        for (int yCoords = 9; yCoords < 21; yCoords ++){
            // Every row has a time mark
            String timeslot = yCoords + ":00\n\n" + (yCoords + 1) + ":00";
            PdfPCell timeslotCell = new PdfPCell();
            timeslotCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            timeslotCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            timeslotCell.addElement(new Paragraph(timeslot, font)); // composite mode
            table.addCell(timeslotCell);

            for (int xCoords = 1; xCoords < 6; xCoords ++){
                // convert the position of the current cell to timetable key format
                String[] keys = allValidKey(xCoords, yCoords);

                // Case for when this time has activity representation in timeslot
                if (timetable.containsKey(keys[0])){
                    String content = timetable.get(keys[0]).courseCode + "\n" +
                            timetable.get(keys[0]).sessionCode + "\n" +
                            timetable.get(keys[0]).instructor + "\n";
                    PdfPCell contentCell = new PdfPCell();
                    contentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    contentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    contentCell.addElement(new Paragraph(content, font)); // composite mode
                    table.addCell(contentCell);
                } else if (timetable.containsKey(keys[1])) {
                    String content = timetable.get(keys[1]).courseCode + "\n" +
                            timetable.get(keys[1]).sessionCode + "\n" +
                            timetable.get(keys[1]).instructor + "\n";
                    PdfPCell contentCell = new PdfPCell();
                    contentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    contentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    contentCell.setRowspan(2);
                    boxed.add(xCoords);
                    contentCell.addElement(new Paragraph(content, font)); // composite mode
                    table.addCell(contentCell);
                } else if (timetable.containsKey(keys[2])) {
                    String content = timetable.get(keys[2]).courseCode + "\n" +
                            timetable.get(keys[2]).sessionCode + "\n" +
                            timetable.get(keys[2]).instructor + "\n";
                    PdfPCell contentCell = new PdfPCell();
                    contentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    contentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    contentCell.setRowspan(3);
                    boxed.add(xCoords);
                    boxed.add(xCoords);
                    contentCell.addElement(new Paragraph(content, font)); // composite mode
                    table.addCell(contentCell);
                } else {
                    if (boxed.contains(xCoords)) {
                        boxed.remove(Integer.valueOf(xCoords));
                    } else {
                        table.addCell(emptyCell);
                    }
                }
            }
        }
    }



    public static void makePDF(TreeMap<String, Session> timetable, Font font){
        Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                    new FileOutputStream("FallWinterTimetable.pdf"));

            document.open();

            PdfPTable table = new PdfPTable(new float[] {1, 2, 2, 2, 2, 2}); // 6 columns.

            // set how much of the page the whole table takes horizontally
            table.setWidthPercentage(90);

            makeHeader(table, font);

            addCourses(timetable, table, font);

            document.add(table);

            document.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Integer[] sessionTime = {41820};
        Session CSC207 = new Session("Paul", "CSC207H1F", "LEC0401", sessionTime);
        Session COG250 = new Session("John", "COG250H1Y", "LEC0101", sessionTime);
        TreeMap<String, Session> sampleTimetable = new TreeMap<>();
        sampleTimetable.put("40912", CSC207);
        sampleTimetable.put("50912", CSC207);
        sampleTimetable.put("41314", COG250);
        sampleTimetable.put("41416", COG250);
        sampleTimetable.put("31416", COG250);
        makePDF(sampleTimetable, font);
    }
}