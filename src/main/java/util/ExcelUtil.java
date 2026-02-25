package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;

public class ExcelUtil {

    public static void exportTableToExcel(JTable table, String defaultFileName) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));

            int option = fileChooser.showSaveDialog(null);
            if (option != JFileChooser.APPROVE_OPTION) return;

            File file = fileChooser.getSelectedFile();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            TableModel model = table.getModel();

            // Header
            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < model.getColumnCount(); c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(model.getColumnName(c));
            }

            // Data
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object value = model.getValueAt(r, c);
                    cell.setCellValue(value == null ? "" : value.toString());
                }
            }

            // Auto size
            for (int c = 0; c < model.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
            }

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            JOptionPane.showMessageDialog(null, "Export success!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() == null ? e.toString() : e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}