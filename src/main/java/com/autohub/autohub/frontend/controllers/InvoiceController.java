package com.autohub.autohub.frontend.controllers;

import com.autohub.autohub.backend.models.Car;
import com.autohub.autohub.backend.models.Rental;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InvoiceController implements Initializable {

    @FXML
    private Label lblInvoiceNumber;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblPickupDate;
    @FXML
    private Label lblReturnDate;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblCustomerName;
    @FXML
    private Label lblCustomerEmail;
    @FXML
    private Label lblCustomerPhone;
    @FXML
    private Label lblCarName;
    @FXML
    private Label lblYear;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblFuel;
    @FXML
    private Label lblDailyRate;
    @FXML
    private Label lblNumDays;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblTax;
    @FXML
    private Label lblTotalAmount;
    @FXML
    private StackPane carImageContainer;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnBackToCars;
    @FXML
    private ScrollPane scrollPane;

    private Rental rental;
    private Car car;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPrint.setOnAction(event -> handlePrint());
        btnDownload.setOnAction(event -> handleDownload());
        btnBackToCars.setOnAction(event -> handleBackToCars());
    }

    public void setRental(Rental rental, Car car) {
        this.rental = rental;
        this.car = car;
        loadInvoiceData();
        loadCarImage(); // ← تحميل الصورة
    }

    public void setRental(Rental rental) {
        this.rental = rental;
        loadInvoiceData();
        loadCarImage();
    }

    private void loadCarImage() {
        try {
            String imageUrl = null;

            // جرب تجيب الصورة من Car أو Rental
            if (car != null && car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                imageUrl = car.getImageUrl();
            } else if (rental != null && rental.getCarImageUrl() != null && !rental.getCarImageUrl().isEmpty()) {
                imageUrl = rental.getCarImageUrl();
            }

            if (imageUrl != null) {
                // لو URL من الإنترنت
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    Image image = new Image(imageUrl, true); // async loading
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(70);
                    imageView.setFitHeight(70);
                    imageView.setPreserveRatio(true);
                    carImageContainer.getChildren().clear();
                    carImageContainer.getChildren().add(imageView);
                }
                // لو مسار ملف محلي
                else if (new File(imageUrl).exists()) {
                    Image image = new Image(new File(imageUrl).toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(70);
                    imageView.setFitHeight(70);
                    imageView.setPreserveRatio(true);
                    carImageContainer.getChildren().clear();
                    carImageContainer.getChildren().add(imageView);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load car image: " + e.getMessage());
            // خلي الـ placeholder الموجود في الـ FXML
        }
    }

    private void loadInvoiceData() {
        if (rental == null) return;

        lblInvoiceNumber.setText("Invoice: R" + rental.getRentalId());
        lblDate.setText("Date: " + (rental.getCreatedAt() != null ? rental.getCreatedAt() : LocalDate.now()));

        String statusText = "Confirmed";
        if ("active".equalsIgnoreCase(rental.getStatus())) {
            statusText = "Active";
        } else if ("completed".equalsIgnoreCase(rental.getStatus())) {
            statusText = "Completed";
        } else if ("cancelled".equalsIgnoreCase(rental.getStatus())) {
            statusText = "Cancelled";
        }
        lblStatus.setText("Status: " + statusText);

        lblPickupDate.setText(rental.getStartDate() != null ? rental.getStartDate().toString() : "N/A");
        lblReturnDate.setText(rental.getEndDate() != null ? rental.getEndDate().toString() : "N/A");

        long days = rental.getDuration();
        lblDuration.setText(days + " days");
        lblNumDays.setText(String.valueOf(days));

        lblCustomerName.setText(rental.getCustomerName() != null ? rental.getCustomerName() : "N/A");
        lblCustomerEmail.setText(rental.getCustomerEmail() != null ? rental.getCustomerEmail() : "N/A");
        lblCustomerPhone.setText("N/A");

        if (car != null) {
            String carFullName = (car.getBrand() != null ? car.getBrand() + " " : "") +
                    (car.getModel() != null ? car.getModel() : "");
            lblCarName.setText(carFullName.isEmpty() ? "N/A" : carFullName);
            lblYear.setText("Year: " + (car.getYear() != null ? car.getYear() : "N/A"));
            lblCategory.setText(car.getTransmission() != null ? car.getTransmission() : "N/A");
            lblFuel.setText(car.getFuelType() != null ? car.getFuelType() : "N/A");
        } else {
            String carFullName = (rental.getCarBrand() != null ? rental.getCarBrand() + " " : "") +
                    (rental.getCarName() != null ? rental.getCarName() : "N/A");
            lblCarName.setText(carFullName);
            lblYear.setText("Year: N/A");
            lblCategory.setText("N/A");
            lblFuel.setText("N/A");
        }

        double totalAmount = rental.getTotalAmount();
        double dailyRate = days > 0 ? totalAmount / days / 1.10 : 0;

        lblDailyRate.setText(String.format("%.2f", dailyRate));

        double subtotal = dailyRate * days;
        double tax = subtotal * 0.10;

        lblSubtotal.setText(String.format("%.2f", subtotal));
        lblTax.setText(String.format("%.2f", tax));
        lblTotalAmount.setText(String.format("%.2f", totalAmount));
    }

    @FXML
    private void handlePrint() {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(btnPrint.getScene().getWindow())) {
                boolean success = job.printPage(scrollPane);
                if (success) {
                    job.endJob();
                    showAlert(Alert.AlertType.INFORMATION, "Print Success", "Invoice printed successfully!");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Print Error", "Failed to print: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDownload() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Invoice as PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            String invoiceId = "R" + rental.getRentalId();
            fileChooser.setInitialFileName("Invoice_" + invoiceId + ".pdf");

            String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
            File downloadsDir = new File(downloadsPath);
            if (downloadsDir.exists()) {
                fileChooser.setInitialDirectory(downloadsDir);
            }

            File file = fileChooser.showSaveDialog(btnDownload.getScene().getWindow());
            if (file != null) {
                generatePdf(file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Download Success",
                        "Invoice saved successfully to:\n" + file.getAbsolutePath());

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Download Error", "Failed to save PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePdf(String filePath) throws IOException {
        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                float pageHeight = page.getMediaBox().getHeight(); // 842
                float pageWidth = page.getMediaBox().getWidth();   // 595
                float margin = 50;

                // ============ SUCCESS BANNER (من أعلى الصفحة) ============
                float bannerHeight = 120;
                contentStream.setNonStrokingColor(209 / 255f, 250 / 255f, 229 / 255f);
                contentStream.addRect(0, pageHeight - bannerHeight, pageWidth, bannerHeight);
                contentStream.fill();

                // عنوان "Booking Confirmed!" في النص
                contentStream.setNonStrokingColor(16 / 255f, 185 / 255f, 129 / 255f);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 28);
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Booking Confirmed!") / 1000 * 28;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, pageHeight - 55);
                contentStream.showText("Booking Confirmed!");
                contentStream.endText();

                // النص التوضيحي في النص
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                String subtitle = "Your rental has been successfully confirmed";
                float subtitleWidth = PDType1Font.HELVETICA.getStringWidth(subtitle) / 1000 * 12;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - subtitleWidth) / 2, pageHeight - 85);
                contentStream.showText(subtitle);
                contentStream.endText();

                contentStream.setNonStrokingColor(0, 0, 0);

                // بداية المحتوى
                float yPosition = pageHeight - bannerHeight - 30;

                // ============ HEADER ============
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("INVOICE");
                contentStream.endText();

                // ============ COMPANY INFO (على اليمين) ============
                float rightX = pageWidth - margin - 180;
                float companyY = yPosition + 5;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                contentStream.beginText();
                contentStream.newLineAtOffset(rightX, companyY);
                contentStream.showText("Car Rental System");
                contentStream.endText();
                companyY -= 13;

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                String[] companyInfo = {
                        "Premium Car Rentals",
                        "Business Street 123",
                        "New York, NY 10001",
                        "contact@carrental.com",
                        "123-4567-8901"
                };

                for (String line : companyInfo) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(rightX, companyY);
                    contentStream.showText(line);
                    contentStream.endText();
                    companyY -= 12;
                }

                contentStream.setNonStrokingColor(0, 0, 0);
                yPosition -= 25;

                // Invoice Details
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Invoice: R" + rental.getRentalId());
                contentStream.endText();
                yPosition -= 13;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date: " + (rental.getCreatedAt() != null ? rental.getCreatedAt() : LocalDate.now()));
                contentStream.endText();
                yPosition -= 13;

                contentStream.setNonStrokingColor(16 / 255f, 185 / 255f, 129 / 255f);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(lblStatus.getText());
                contentStream.endText();

                contentStream.setNonStrokingColor(0, 0, 0);
                yPosition -= 25;

                // ============ SEPARATOR ============
                contentStream.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(pageWidth - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;

                // ============ RENTAL PERIOD & BILL TO ============
                float leftColumnX = margin;
                float rightColumnX = pageWidth / 2 + 20;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.newLineAtOffset(leftColumnX, yPosition);
                contentStream.showText("Rental Period");
                contentStream.endText();

                float tempY = yPosition - 18;
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                String[][] rentalPeriod = {
                        {"Pickup:", lblPickupDate.getText()},
                        {"Return:", lblReturnDate.getText()},
                        {"Duration:", lblDuration.getText()}
                };

                for (String[] line : rentalPeriod) {
                    contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(leftColumnX, tempY);
                    contentStream.showText(line[0]);
                    contentStream.endText();

                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(leftColumnX + 55, tempY);
                    contentStream.showText(line[1]);
                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    tempY -= 14;
                }

                // Bill To
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.newLineAtOffset(rightColumnX, yPosition);
                contentStream.showText("Bill To");
                contentStream.endText();

                tempY = yPosition - 18;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                contentStream.beginText();
                contentStream.newLineAtOffset(rightColumnX, tempY);
                contentStream.showText(lblCustomerName.getText());
                contentStream.endText();
                tempY -= 15;

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                contentStream.beginText();
                contentStream.newLineAtOffset(rightColumnX, tempY);
                contentStream.showText(lblCustomerEmail.getText());
                contentStream.endText();

                yPosition -= 70;
                contentStream.setNonStrokingColor(0, 0, 0);

                // ============ SEPARATOR ============
                contentStream.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(pageWidth - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;

                // ============ VEHICLE INFORMATION ============
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Vehicle Information");
                contentStream.endText();
                yPosition -= 20;

                // الصورة
                float imageSize = 70;
                PDImageXObject pdImage = null;

                try {
                    String imageUrl = (car != null && car.getImageUrl() != null) ?
                            car.getImageUrl() : rental.getCarImageUrl();

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                            URL url = new URL(imageUrl);
                            BufferedImage bufferedImage = ImageIO.read(url);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "jpg", baos);
                            pdImage = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "carImage");
                        } else if (new File(imageUrl).exists()) {
                            pdImage = PDImageXObject.createFromFile(imageUrl, document);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Could not load image: " + e.getMessage());
                }

                if (pdImage != null) {
                    contentStream.drawImage(pdImage, margin, yPosition - imageSize, imageSize, imageSize);
                } else {
                    contentStream.setNonStrokingColor(229 / 255f, 231 / 255f, 235 / 255f);
                    contentStream.addRect(margin, yPosition - imageSize, imageSize, imageSize);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 15, yPosition - 42);
                    contentStream.showText("CAR");
                    contentStream.endText();
                }

                contentStream.setNonStrokingColor(0, 0, 0);

                // Car Details
                float carDetailsX = margin + imageSize + 15;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(carDetailsX, yPosition - 20);
                contentStream.showText(lblCarName.getText());
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                contentStream.beginText();
                contentStream.newLineAtOffset(carDetailsX, yPosition - 36);
                contentStream.showText(lblYear.getText() + "  |  " + lblCategory.getText() + "  |  " + lblFuel.getText());
                contentStream.endText();

                yPosition -= 85;
                contentStream.setNonStrokingColor(0, 0, 0);

                // ============ SEPARATOR ============
                contentStream.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(pageWidth - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;

                // ============ PAYMENT DETAILS ============
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Payment Details");
                contentStream.endText();
                yPosition -= 20;

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                String[][] paymentLines = {
                        {"Daily Rate", "$" + lblDailyRate.getText()},
                        {"Number of Days", lblNumDays.getText()},
                        {"Subtotal", "$" + lblSubtotal.getText()},
                        {"Tax (10%)", "$" + lblTax.getText()}
                };

                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                for (String[] line : paymentLines) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(line[0]);
                    contentStream.endText();

                    contentStream.setNonStrokingColor(0, 0, 0);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(pageWidth - margin - 80, yPosition);
                    contentStream.showText(line[1]);
                    contentStream.endText();
                    contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                    yPosition -= 15;
                }
                yPosition -= 10;

                // ============ SEPARATOR ============
                contentStream.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(pageWidth - margin, yPosition);
                contentStream.stroke();
                yPosition -= 20;

                // Total
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Total Amount");
                contentStream.endText();

                contentStream.setNonStrokingColor(16 / 255f, 185 / 255f, 129 / 255f);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(pageWidth - margin - 110, yPosition);
                contentStream.showText("$" + lblTotalAmount.getText());
                contentStream.endText();

                yPosition -= 50;
                contentStream.setNonStrokingColor(0, 0, 0);

                // ============ SEPARATOR ============
                contentStream.setStrokingColor(203 / 255f, 213 / 255f, 225 / 255f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(pageWidth - margin, yPosition);
                contentStream.stroke();
                yPosition -= 25;

                // ============ FOOTER ============
                contentStream.setFont(PDType1Font.HELVETICA, 11);
                contentStream.setNonStrokingColor(100 / 255f, 116 / 255f, 139 / 255f);
                String footerText1 = "Thank you for choosing our car rental service";
                float footer1Width = PDType1Font.HELVETICA.getStringWidth(footerText1) / 1000 * 11;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - footer1Width) / 2, yPosition);
                contentStream.showText(footerText1);
                contentStream.endText();
                yPosition -= 15;

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                contentStream.setNonStrokingColor(148 / 255f, 163 / 255f, 184 / 255f);
                String footerText2 = "Please keep this invoice for your records";
                float footer2Width = PDType1Font.HELVETICA.getStringWidth(footerText2) / 1000 * 9;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - footer2Width) / 2, yPosition);
                contentStream.showText(footerText2);
                contentStream.endText();

            } finally {
                contentStream.close();
            }

            document.save(filePath);

        } finally {
            document.close();
        }
    }


    @FXML
    private void handleBackToCars() {
        btnBackToCars.getScene().getWindow().hide();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
