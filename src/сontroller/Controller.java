package сontroller;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import exceptions.EmptyException;
import exceptions.IncorrectData;
import exceptions.MyMessageException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Table;
import math.*;
import org.apache.commons.lang.math.NumberUtils;
import parser.JaxbParser;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Controller {
    @FXML
    private TableView tableA;
    @FXML
    private TableView tableB;
    @FXML
    private TableView tableC;
    @FXML
    private ComboBox comboBoxExtr;
    @FXML
    private TextField textFieldVariables;
    @FXML
    private TextField textFieldRestrictions;
    @FXML
    private Label labelVariables;
    @FXML
    private Label labelRestrictions;
    @FXML
    private Label labelExtr;
    @FXML
    private Label firstComponentLabel;
    @FXML
    private Label secondComponentLabel;
    @FXML
    private Tab enterTab;
    @FXML
    private Tab solutionTab;
    @FXML
    private Tab checkTab;
    @FXML
    private Tab findTab;
    @FXML
    private Tab graphicTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private TabPane tabPaneGraphic;
    @FXML
    private Tab buildGraphicTabPane;
    @FXML
    private Tab showEquationTabPane;
    @FXML
    private TextArea textArea;
    @FXML
    private TextArea textAreaCheck;
    @FXML
    private TextArea equationTextArea;
    @FXML
    private LineChart<Number, Number> AlfaLineChart;
    @FXML
    private LineChart<Number, Number> BettaLineChart;
    @FXML
    private ComboBox comboBoxDeltaB1, comboBoxDeltaB1_m1;
    @FXML
    private ComboBox comboBoxDeltaB2;
    @FXML
    private Canvas graph;
    @FXML
    private AnchorPane paneGraph;
    @FXML
    private Button buttonDraw, buttonDraw_m1, buttonShowEquation_m1, buttonShowEquation;

    private boolean openCheckAndRestricTab = false;
    private ArrayList<String> arrayErrors;
    private ArrayList<Double> iterAlfaList, iterBettaList;
    private static Stage mainStage;
    private static ArrayList<TableColumn> arrayTableAColumn, arrayTableBColumn, arrayTableCColumn;
    private ObservableList data;
    private boolean extr, check = false, drawclick, solved, valid, report;
    private FileChooser fileChooser;
    private File file;
    private FileChooser.ExtensionFilter extFilter;
    private MMethod method;
    private Validation val;
    private Graphics gr;
    private XYChart.Series<Number, Number> iterAlfa, iterBetta;
    private double Width, Height, scale = 40, maxX, maxY, minX, minY;
    private StringBuilder answToEq = new StringBuilder();

    @FXML
    private void initialize() {
        if (check == false) {
            comboBoxExtr.getItems().add("max");
            comboBoxExtr.getItems().add("min");
            initLoader();
            tableA.setVisible(false);
            tableB.setVisible(false);
            tableC.setVisible(false);
            solved = false;
            valid = false;
            report = false;
        }
        else {
            initListeners();
            solved = false;
            valid = false;
            report = false;
        }
    }

    private void initLoader() {
        setMainStage(mainStage);
        solutionTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (solutionTab.isSelected() && !solved) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Необходимо ввести начальные данные\nи нажать на кнопку \"Решить\"");
                    alert.showAndWait();
                    tabPane.getSelectionModel().select(enterTab);
                }
            }
        });
        checkTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (checkTab.isSelected() && !valid) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if(!solved){
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Необходимо ввести начальные данные\nи нажать на кнопку \"Решить\"");
                        alert.showAndWait();
                        tabPane.getSelectionModel().select(enterTab);
                    }
                    else {
                        if(!openCheckAndRestricTab) {
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Необходимо проверить достоверность нажав \nна кнопку \"Проверить достоверность решения \"");
                            alert.showAndWait();
                            tabPane.getSelectionModel().select(solutionTab);
                        }
                    }
                }
            }
        });
        findTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (findTab.isSelected() && !valid) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if(!solved){
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Необходимо ввести начальные данные\nи нажать на кнопку \"Решить\"");
                        alert.showAndWait();
                        tabPane.getSelectionModel().select(enterTab);
                    }
                    else {
                        if(!openCheckAndRestricTab) {
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Необходимо проверить достоверность нажав \nна кнопку \"Проверить достоверность решения \"");
                            alert.showAndWait();
                            tabPane.getSelectionModel().select(solutionTab);
                        }
                    }
                }
            }
        });
        graphicTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (graphicTab.isSelected() && !solved) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Необходимо ввести начальные данные\nи нажать на кнопку \"Решить\"");
                    alert.showAndWait();
                    tabPane.getSelectionModel().select(enterTab);
                }
            }
        });
        showEquationTabPane.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (showEquationTabPane.isSelected() && !drawclick) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Необходимо сперва\nпостроить график!\nнажав на кнопку \"Построить\"");
                    alert.showAndWait();
                    tabPaneGraphic.getSelectionModel().select(buildGraphicTabPane);
                }
            }
        });
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void menuActions(ActionEvent actionEvent) throws JAXBException {
        Object source = actionEvent.getSource();
        if (!(source instanceof MenuItem)) {
            return;
        }
        MenuItem clickedItem = (MenuItem) source;
        switch (clickedItem.getId()) {
            case "openMenuItem":
                JaxbParser jaxbParser = new JaxbParser();
                fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
                fileChooser.setTitle("Открытие XML документа");//Заголовок диалога
                extFilter =  new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");//Расширение
                fileChooser.getExtensionFilters().add(extFilter);
                file = fileChooser.showOpenDialog(mainStage);//Указываем текущую сцену
                if (file != null) {
                    arrayTableAColumn = new ArrayList<>();
                    arrayTableBColumn = new ArrayList<>();
                    arrayTableCColumn = new ArrayList<>();
                    MMethod mMethod = (MMethod) jaxbParser.getObject(file,MMethod.class);
                    check = true;
                    tableA.getColumns().clear();
                    tableA.getItems().clear();
                    tableA.setVisible(true);
                    tableB.getColumns().clear();
                    tableB.getItems().clear();
                    tableB.setVisible(true);
                    tableC.getColumns().clear();
                    tableC.getItems().clear();
                    tableC.setVisible(true);
                    Table.createTable(tableA, mMethod.getA()[0].length, mMethod.getA().length, "A");
                    Table.createTable(tableB, 1, mMethod.getB().length, "B");
                    Table.createTable(tableC, mMethod.getC().length, 1, "C");
                    Table.setTable(tableA, mMethod.getA());
                    Table.setTable(tableB, mMethod.getB());
                    Table.setTable(tableC, mMethod.getC());
                    if(mMethod.getExtr()){
                        comboBoxExtr.getSelectionModel().select("min");
                    }else{
                        comboBoxExtr.getSelectionModel().select("max");
                    }
                    textFieldRestrictions.setText(String.valueOf(mMethod.getA().length));
                    textFieldVariables.setText(String.valueOf(mMethod.getA()[0].length));
                    tabPane.getSelectionModel().select(enterTab);
                    textArea.clear();
                    textAreaCheck.clear();
                    initialize();
                }
                break;
            case "saveMenuItem":
                try {
                    if (tableA.getItems().isEmpty() || tableB.getItems().isEmpty() || tableC.getItems().isEmpty()) {
                        throw new MyMessageException("Постройте для начала таблицы!");
                    }
                    else if ( comboBoxExtr.getSelectionModel().isEmpty()||
                            EmptyException.emptyTable(tableA) || EmptyException.emptyTable(tableB) || EmptyException.emptyTable(tableC)) {
                        arrayErrors = new ArrayList<>();
                        if (comboBoxExtr.getSelectionModel().isEmpty()) {
                            arrayErrors.add(labelExtr.getText());
                        }
                        if (EmptyException.emptyTable(tableA)) {
                            arrayErrors.add("таблица А");
                        }
                        if (EmptyException.emptyTable(tableB)) {
                            arrayErrors.add("таблица В");
                        }
                        if (EmptyException.emptyTable(tableC)) {
                            arrayErrors.add("вектор С");
                        }
                        throw new EmptyException(arrayErrors);
                    }
                    else if (IncorrectData.incorrectTable(tableA) || IncorrectData.incorrectTable(tableB) || IncorrectData.incorrectTable(tableC)) {
                        arrayErrors = new ArrayList<>();
                        if (IncorrectData.incorrectTable(tableA)) {
                            arrayErrors.add("Матрица А");
                        }
                        if (IncorrectData.incorrectTable(tableC)) {
                            arrayErrors.add("Вектор ограничений");
                        }
                        if (IncorrectData.incorrectTable(tableC)) {
                            arrayErrors.add("Целевая функция");
                        }
                        throw new IncorrectData(arrayErrors);
                    }
                    fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
                    fileChooser.setTitle("Сохранение XML документа");//Заголовок диалога
                    extFilter =  new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");//Расширение
                    fileChooser.getExtensionFilters().add(extFilter);
                    file = fileChooser.showSaveDialog(mainStage);//Указываем текущую сцену CodeNote.mainStage
                    if (file != null) { //Save
                        MMethod myMethod = new MMethod(Table.getTableC(tableC, tableC.getColumns().size()),
                                Table.getTableA(tableA, tableA.getColumns().size(), tableA.getItems().size()),
                                Table.getTableB(tableB, tableB.getItems().size()), extr);

                        jaxbParser = new JaxbParser();
                        jaxbParser.saveObject(file,myMethod);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Отчет о сохранении");
                        alert.setHeaderText("Сохранение выполнено успешно!");
                        alert.setContentText("Файл \"" + file.getName() + "\"  успешно сохранен по следующему " +
                                "адресу:\n\"" + file.getAbsolutePath()+"\"");
                        alert.showAndWait();
                    }
                }
                catch (MyMessageException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessage());
                    alert.showAndWait();
                }
                catch (IncorrectData ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageTables());
                    alert.showAndWait();
                }
                catch (EmptyException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageTablesAndFields());
                    alert.showAndWait();
                }
                break;
            case "exitMenuItem":
                Platform.exit();
                break;
            case "aboutMenuItem":
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("О программе");
                alert.setHeaderText(null);
                alert.setHeaderText("Программная реализация для решения задач линейного программирования. \n" +
                        "Нахождение оптимального плана с помощью М-метода (первый алгоритм).");
                alert.setContentText("Программная реализация создана в рамках курсовой работы по \n" +
                        "предмету \"Математические методы исследования операций\"" +
                        "\nстудентами группы КН-34б.\n Члены бригады:\n" +
                        "   - Кондратьев Виталий (бригадир).\n" +
                        "   - Ворона Борис.\n" +
                        "   - Кущ Алина.");
                alert.setResizable(false);
                alert.showAndWait();
                break;
            case "reportMenuItem":
                try {
                    if(!report){
                        throw new MyMessageException("Необходимо выполнить решение задачи для создания отчета!");
                    }
                    fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
                    fileChooser.setTitle("Сохранение отчета");//Заголовок диалога
                    extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.PDF", "*.pdf");//Расширение
                    fileChooser.getExtensionFilters().add(extFilter);
                    file = fileChooser.showSaveDialog(mainStage);//Указываем текущую сцену CodeNote.mainStage
                    if (file != null) { //Save
                        file.getParentFile().mkdirs();
                        Image a = null, b = null, c =null;
                        File graphAlpha = null, graphBetta= null, graphStability = null;
                        if(method.isSolve()) {
                            graphAlpha = new File("Alfa.png");
                            graphBetta = new File("Betta.png");
                            graphStability = new File("Stability.png");
                            WritableImage snapShotAlpha = AlfaLineChart.snapshot(null, null);
                            ImageIO.write(SwingFXUtils.fromFXImage(snapShotAlpha, null), "png", graphAlpha);
                            a = Image.getInstance("Alfa.png");
                            WritableImage snapShotBeta = BettaLineChart.snapshot(null, null);
                            ImageIO.write(SwingFXUtils.fromFXImage(snapShotBeta, null), "png", graphBetta);
                            b = Image.getInstance("Betta.png");
                            WritableImage snapShotStability = graph.snapshot(null, null);
                            ImageIO.write(SwingFXUtils.fromFXImage(snapShotStability, null), "png", graphStability);
                            c = Image.getInstance("Stability.png");
                        }
                        OutputStream files = new FileOutputStream(file);
                        Document document = new Document();
                        PdfWriter writer = PdfWriter.getInstance(document,files);
                        BaseFont times = BaseFont.createFont("c:/windows/fonts/times.ttf","cp1251",BaseFont.EMBEDDED);
                        document.open();
                        StringBuilder sb = new StringBuilder();
                        sb.append(method.getZvit1());
                        if(c!=null) {
                            c.setAlignment(Element.ALIGN_MIDDLE);
                        }
                        if(a!=null)
                            a.setAlignment(Element.ALIGN_MIDDLE);

                        if(b!=null)
                            b.setAlignment(Element.ALIGN_MIDDLE);

                        Paragraph first = new Paragraph("Условия исходной задачи\n", new Font(times,24));
                        first.setAlignment(Element.ALIGN_CENTER);
                        Paragraph second = null, third = null;
                        if(method.isSolve()) {
                            third = new Paragraph("\nИсследование устойчивости\n\n", new Font(times, 24));
                            third.setAlignment(Element.ALIGN_CENTER);
                        }
                        document.add(first);
                        document.add(new Paragraph(sb.toString(), new Font(times,10)));
                        sb.setLength(0);
                        if(val != null) {
                            second = new Paragraph("\nПроверка достоверности\n", new Font(times, 24));
                            second.setAlignment(Element.ALIGN_CENTER);
                            sb.append(val.getValidStr());
                            document.add(second);
                            document.add(new Paragraph(sb.toString()+"\n\n", new Font(times, 10)));
                        }
                        if(third != null && gr != null) {
                            third = new Paragraph("\nИсследование устойчивости\n\n", new Font(times, 24));
                            third.setAlignment(Element.ALIGN_CENTER);
                            c.scaleAbsolute(500f, 250f);
                            sb.setLength(0);
                            sb.append(gr.getUst());
                            document.add(third);
                            document.add(new Paragraph("\n\n", new Font(times, 14)));
                            document.add(c);
                            document.add(new Paragraph("\n\n", new Font(times, 14)));
                            document.add(new Paragraph(sb.toString(), new Font(times, 10)));
                        }
                        if(a != null) {
                            Paragraph graphLinear = new Paragraph("\n\nГрафики изменения линейной формы по итерациям", new Font(times, 24));
                            graphLinear.setAlignment(Element.ALIGN_CENTER);
                            a.scaleAbsolute(500f, 250f);
                            b.scaleAbsolute(500f, 250f);
                            document.add(graphLinear);
                            document.add(a);
                            document.add(b);
                        }
                        if(graphAlpha!= null) {
                            graphAlpha.delete();
                            graphBetta.delete();
                            graphStability.delete();
                        }
                        document.close();
                        Alert saveAlert = new Alert(Alert.AlertType.INFORMATION);
                        saveAlert.setTitle("Отчет о сохранении");
                        saveAlert.setHeaderText("Отчет сохранен успешно!");
                        saveAlert.setContentText("Отчет с названием " + file.getName() + " был успешно сформирован и сохранен по следующему пути:\n"
                                + file.getAbsolutePath());
                        saveAlert.showAndWait();
                    }
                }
                catch (MyMessageException ex) {
                    Alert repAlert = new Alert(Alert.AlertType.INFORMATION);
                    repAlert.setTitle("Ошибка");
                    repAlert.setHeaderText("Ошибка сохранения отчета");
                    repAlert.setContentText(ex.getMessage());
                    repAlert.showAndWait();
                }
                catch (Exception ex) {
                    Alert repErrorAlert = new Alert(Alert.AlertType.ERROR);
                    repErrorAlert.setHeaderText("Ошибка сохранения отчета");
                    repErrorAlert.setContentText("Произошла ошибка при сохранении отчета, возможно данный файл открыт в другой программе," +
                            " для того чтобы сохранить отчет в данный файл, закройте его во всех остальных программах!");
                    repErrorAlert.showAndWait();
                }
                break;
        }
    }

    public void buttonAction(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        switch (clickedButton.getId()) {
            case "buttonBuild":
                try {
                    if (textFieldRestrictions.getText().isEmpty() ||
                            textFieldVariables.getText().isEmpty()) {
                        arrayErrors = new ArrayList<>();
                        if (textFieldRestrictions.getText().isEmpty()) {
                            arrayErrors.add(labelRestrictions.getText());
                        }
                        if (textFieldVariables.getText().isEmpty()) {
                            arrayErrors.add(labelVariables.getText());
                        }
                        if (comboBoxExtr.getSelectionModel().isEmpty()) {
                            arrayErrors.add(labelExtr.getText());
                        }
                        throw new EmptyException(arrayErrors);
                    } else if (!NumberUtils.isNumber(textFieldRestrictions.getText()) ||
                            !NumberUtils.isNumber(textFieldVariables.getText())) {
                        arrayErrors = new ArrayList<>();
                        if (!NumberUtils.isNumber(textFieldRestrictions.getText())) {
                            arrayErrors.add(labelRestrictions.getText());
                        }
                        if (!NumberUtils.isNumber(textFieldVariables.getText())) {
                            arrayErrors.add(labelVariables.getText());
                        }
                        throw new IncorrectData(arrayErrors);
                    }
                    else if(Integer.parseInt(textFieldVariables.getText()) < Integer.parseInt(textFieldRestrictions.getText())){
                        throw new MyMessageException("Количество ограничений должно быть меньше\nлибо равно количеству переменных");
                    }
                    else if(Integer.parseInt(textFieldVariables.getText()) < 1 || Integer.parseInt(textFieldRestrictions.getText()) < 1){
                        throw new MyMessageException("Количество ограничений и переменных\nдолжно быть больше 0");
                    }
                    arrayTableAColumn = new ArrayList<>();
                    arrayTableBColumn = new ArrayList<>();
                    arrayTableCColumn = new ArrayList<>();
                    tableA.getColumns().clear();
                    tableA.getItems().clear();
                    tableA.setVisible(true);
                    tableB.getColumns().clear();
                    tableB.getItems().clear();
                    tableB.setVisible(true);
                    tableC.getColumns().clear();
                    tableC.getItems().clear();
                    tableC.setVisible(true);
                    check = true;
                    Table.createTable(tableA, Integer.parseInt(textFieldVariables.getText()), Integer.parseInt(textFieldRestrictions.getText()), "A");
                    Table.createTable(tableB, 1, Integer.parseInt(textFieldRestrictions.getText()), "B");
                    Table.createTable(tableC, Integer.parseInt(textFieldVariables.getText()), 1, "C");
                    initialize();
                } catch (MyMessageException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessage());
                    alert.showAndWait();
                } catch (EmptyException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageFields());
                    alert.showAndWait();
                } catch (IncorrectData ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageFields());
                    alert.showAndWait();
                }
                break;
        case "buttonSolve" :
            try {
                if (tableA.getItems().isEmpty() || tableB.getItems().isEmpty() || tableC.getItems().isEmpty()) {
                    throw new MyMessageException("Постройте для начала таблицы!");
                } else if (EmptyException.emptyTable(tableA) || EmptyException.emptyTable(tableB) || EmptyException.emptyTable(tableC)) {
                    arrayErrors = new ArrayList<>();
                    if (EmptyException.emptyTable(tableA)) {
                        arrayErrors.add("Матрица А");
                    }
                    if (EmptyException.emptyTable(tableB)) {
                        arrayErrors.add("Вектор ограничений");
                    }
                    if (EmptyException.emptyTable(tableC)) {
                        arrayErrors.add("Целевая функция");
                    }
                    throw new EmptyException(arrayErrors);
                } else if (IncorrectData.incorrectTable(tableA) || IncorrectData.incorrectTable(tableB) || IncorrectData.incorrectTable(tableC)) {
                    arrayErrors = new ArrayList<>();
                    if (IncorrectData.incorrectTable(tableA)) {
                        arrayErrors.add("Матрица А");
                    }
                    if (IncorrectData.incorrectTable(tableC)) {
                        arrayErrors.add("Вектор ограничений");
                    }
                    if (IncorrectData.incorrectTable(tableC)) {
                        arrayErrors.add("Целевая функция");
                    }
                    throw new IncorrectData(arrayErrors);
                }
                if (comboBoxExtr.getSelectionModel().getSelectedItem() == "max"){
                    extr = false;}
                else {extr = true;}
                method = new MMethod(Table.getTableC(tableC, tableC.getColumns().size()),
                        Table.getTableA(tableA, tableA.getColumns().size(), tableA.getItems().size()),
                        Table.getTableB(tableB, tableB.getItems().size()), extr);
                method.run();
                textArea.clear();
                openCheckAndRestricTab = false;
                valid = false;
                if(textAreaCheck.getText()!=null){
                    textAreaCheck.clear();
                }
                if(textFieldRestrictions.getText() != null){
                    textFieldRestrictions.clear();
                }
                if(textFieldVariables.getText() != null){
                    textFieldVariables.clear();
                }
                if(equationTextArea.getText() != null){
                    equationTextArea.clear();
                }
                if(AlfaLineChart.getData() != null) {
                    AlfaLineChart.getData().clear();
                    BettaLineChart.getData().clear();
                }
                drawclick = false;
                paneGraph.getChildren().clear();
                solved = true;
                report = true;
                for(int i=0;i<method.getAnswer().size();i++) {
                    textArea.setText(textArea.getText()+method.getAnswer().get(i).toString());
                }
                tabPane.getSelectionModel().select(solutionTab);
                if(!method.isSolve())
                    break;
                iterAlfa = new XYChart.Series();
                iterBetta = new XYChart.Series();
                iterAlfaList = method.getIteratAlfa();
                iterBettaList = method.getIteratBetta();
                for (int i = 0; i < iterAlfaList.size(); i++) {
                    iterAlfa.getData().add(new XYChart.Data(i, iterAlfaList.get(i).doubleValue()));
                }
                for (int i = 0; i < iterBettaList.size(); i++) {
                    iterBetta.getData().add(new XYChart.Data(i, iterBettaList.get(i).doubleValue()));
                }
                AlfaLineChart.getData().addAll(iterAlfa);
                BettaLineChart.getData().addAll(iterBetta);
                AlfaLineChart.setLegendVisible(false);
                BettaLineChart.setLegendVisible(false);
                AlfaLineChart.setStyle("CHART_COLOR_1: red;");
                BettaLineChart.setStyle("CHART_COLOR_1: blue;");
                for(final XYChart.Data<Number, Number> data : iterAlfa.getData()){
                    data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Tooltip.install(data.getNode(), new Tooltip("Итерация : " + data.getXValue() + "\nЗначение : " + String.valueOf(data.getYValue())));
                        }
                    });
                }
                for(final XYChart.Data<Number, Number> data : iterBetta.getData()){
                    data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Tooltip.install(data.getNode(), new Tooltip("Итерация : " + data.getXValue() + "\nЗначение : " + String.valueOf(data.getYValue())));
                        }
                    });
                }
            } catch (EmptyException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText(ex.getMessageTables());
                alert.showAndWait();
            } catch (IncorrectData ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText(ex.getMessageTables());
                alert.showAndWait();
            } catch (MyMessageException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ошибка");
                alert.setHeaderText(ex.getMessage());
                alert.showAndWait();
            }
            break;
            case "buttonCheck":
                if(method.getB().length == 1){
                    comboBoxDeltaB1_m1.setDisable(false);
                    buttonDraw_m1.setDisable(false);
                    buttonShowEquation_m1.setDisable(false);

                    comboBoxDeltaB1.setDisable(true);
                    comboBoxDeltaB2.setDisable(true);
                    buttonDraw.setDisable(true);
                    buttonShowEquation.setDisable(true);
                }else{
                    comboBoxDeltaB1.setDisable(false);
                    comboBoxDeltaB2.setDisable(false);
                    buttonDraw.setDisable(false);
                    buttonShowEquation.setDisable(false);

                    comboBoxDeltaB1_m1.setDisable(true);
                    buttonDraw_m1.setDisable(true);
                    buttonShowEquation_m1.setDisable(true);
                }
                if(method.isSolve()) {
                    initDeltaComboBoxes();
                    val = new Validation();
                    val.OpornoCheck();
                    val.DopustimostCheck();
                    val.OptimalityCheck();
                    textAreaCheck.clear();
                    textAreaCheck.setText(val.getListCheck().toString());
                    textAreaCheck.setEditable(false);
                    valid = true;
                    openCheckAndRestricTab = true;
                    tabPane.getSelectionModel().select(checkTab);
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Ошибка при попытке проверки достоверности решения");
                    alert.setContentText("Невозможно проверить достоверность результатов, исходная задача не имеет решения");
                    alert.showAndWait();
                }
                break;
            case "buttonDraw":
                try {
                    if (comboBoxDeltaB1.getSelectionModel().isEmpty() || comboBoxDeltaB2.getSelectionModel().isEmpty()) {
                        arrayErrors = new ArrayList<>();
                        if (comboBoxDeltaB1.getSelectionModel().isEmpty()) {
                            arrayErrors.add(firstComponentLabel.getText());
                        }
                        if (comboBoxDeltaB2.getSelectionModel().isEmpty()) {
                            arrayErrors.add(secondComponentLabel.getText());
                        }
                        throw new EmptyException(arrayErrors);
                    } else if (Integer.parseInt(comboBoxDeltaB1.getSelectionModel().getSelectedItem().toString()) == Integer.parseInt(comboBoxDeltaB2.getSelectionModel().getSelectedItem().toString())) {
                        arrayErrors = new ArrayList<>();
                        arrayErrors.add(firstComponentLabel.getText() + " и " + secondComponentLabel.getText() + " не должны совпадать ");
                        throw new IncorrectData(arrayErrors);
                    }
                    paneGraph.getChildren().remove(graph);
                    graph = new Canvas(Width, Height);
                    gr = new Graphics(Integer.parseInt(comboBoxDeltaB1.getSelectionModel().getSelectedItem().toString()),Integer.parseInt(comboBoxDeltaB2.getSelectionModel().getSelectedItem().toString()));
                    gr.DoIt();
                    calcDataToCanvas();
                    Width = paneGraph.getWidth();
                    Height = paneGraph.getHeight();
                    graph.setWidth(Width);
                    graph.setHeight(Height);
                    GraphicsContext gc = graph.getGraphicsContext2D();
                    drawShapes(gc, Integer.parseInt(comboBoxDeltaB1.getSelectionModel().getSelectedItem().toString()),Integer.parseInt(comboBoxDeltaB2.getSelectionModel().getSelectedItem().toString()));
                    paneGraph.getChildren().add(graph);
                    paneGraph.getScene().widthProperty().addListener(new ChangeListener<Number>() {
                        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                            paneGraph.getChildren().remove(graph);
                            Width = newSceneWidth.doubleValue();
                            graph = new Canvas(Width, Height);
                            GraphicsContext gc = graph.getGraphicsContext2D();
                            drawShapes(gc, Integer.parseInt(comboBoxDeltaB1.getSelectionModel().getSelectedItem().toString()),Integer.parseInt(comboBoxDeltaB2.getSelectionModel().getSelectedItem().toString()));
                            paneGraph.getChildren().add(graph);
                        }
                    });
                    paneGraph.getScene().heightProperty().addListener(new ChangeListener<Number>() {
                        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                            paneGraph.getChildren().remove(graph);
                            Height = newSceneHeight.doubleValue();
                            graph = new Canvas(Width, Height);
                            GraphicsContext gc = graph.getGraphicsContext2D();
                            drawShapes(gc, Integer.parseInt(comboBoxDeltaB1.getSelectionModel().getSelectedItem().toString()),Integer.parseInt(comboBoxDeltaB2.getSelectionModel().getSelectedItem().toString()));
                            paneGraph.getChildren().add(graph);
                        }
                    });
                    drawclick = true;
                }
                catch (EmptyException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageFields());
                    alert.showAndWait();
                } catch (IncorrectData ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessageFields());
                    alert.showAndWait();
                }
                break;
            case "buttonShowEquation":
                try {
                    if(!drawclick){
                        throw new MyMessageException("Необходимо сперва\nпостроить график!");
                    }
                    equationTextArea.setText(gr.getUst().toString());
                    tabPaneGraphic.getSelectionModel().select(showEquationTabPane);
                    report = true;
                }
                catch (MyMessageException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessage());
                    alert.showAndWait();
                }
                break;
            case "buttonDraw_m1":
                    paneGraph.getChildren().remove(graph);
                    graph = new Canvas(Width, Height);
                    Width = paneGraph.getWidth();
                    Height = paneGraph.getHeight();
                    graph.setWidth(Width);
                    graph.setHeight(Height);
                    GraphicsContext gc = graph.getGraphicsContext2D();
                    drawShapesM1(gc, Integer.parseInt(comboBoxDeltaB1_m1.getSelectionModel().getSelectedItem().toString()));
                    paneGraph.getChildren().add(graph);
                    paneGraph.getScene().widthProperty().addListener(new ChangeListener<Number>() {
                        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                            paneGraph.getChildren().remove(graph);
                            Width = newSceneWidth.doubleValue();
                            graph = new Canvas(Width, Height);
                            GraphicsContext gc = graph.getGraphicsContext2D();
                            drawShapesM1(gc, Integer.parseInt(comboBoxDeltaB1_m1.getSelectionModel().getSelectedItem().toString()));
                            paneGraph.getChildren().add(graph);
                        }
                    });
                    paneGraph.getScene().heightProperty().addListener(new ChangeListener<Number>() {
                        @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                            paneGraph.getChildren().remove(graph);
                            Height = newSceneHeight.doubleValue();
                            graph = new Canvas(Width, Height);
                            GraphicsContext gc = graph.getGraphicsContext2D();
                            drawShapesM1(gc, Integer.parseInt(comboBoxDeltaB1_m1.getSelectionModel().getSelectedItem().toString()));
                            paneGraph.getChildren().add(graph);
                        }
                    });
                    drawclick = true;

                break;
            case "buttonShowEquation_m1":
                try {
                    if(!drawclick){
                        throw new MyMessageException("Необходимо сперва\nпостроить график!");
                    }
                    equationTextArea.setText(answToEq.toString());
                    tabPaneGraphic.getSelectionModel().select(showEquationTabPane);
                    report = true;
                }
                catch (MyMessageException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(ex.getMessage());
                    alert.showAndWait();
                }
                break;
        }
    }

    private void initDeltaComboBoxes() {
        if(method.getB().length==1){
            comboBoxDeltaB1_m1.getItems().clear();
            comboBoxDeltaB1_m1.getItems().add(1);
            return;
        }
        comboBoxDeltaB1.getItems().clear();
        comboBoxDeltaB2.getItems().clear();
        for (int i = 0; i < method.getB().length; i++) {
            comboBoxDeltaB1.getItems().add(i+1);
            comboBoxDeltaB2.getItems().add(i+1);
        }
    }

    private void initListeners() {
        for (int i = 0; i < tableA.getColumns().size(); i++) {
            arrayTableAColumn.get(i).setCellFactory(TextFieldTableCell.forTableColumn());
            arrayTableAColumn.get(i).setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<ObservableList, String> t) {
                            data = (ObservableList) tableA.getItems().get(t.getTablePosition().getRow());
                            String value = data.get(t.getTablePosition().getColumn()).toString();
                            data.set(t.getTablePosition().getColumn(), t.getNewValue());
                        }
                    }
            );

            arrayTableCColumn.get(i).setCellFactory(TextFieldTableCell.forTableColumn());
            arrayTableCColumn.get(i).setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<ObservableList, String> t) {
                            data = (ObservableList) tableC.getItems().get(t.getTablePosition().getRow());
                            String value = data.get(t.getTablePosition().getColumn()).toString();
                            data.set(t.getTablePosition().getColumn(), t.getNewValue());
                        }
                    }
            );
        }
        arrayTableBColumn.get(0).setCellFactory(TextFieldTableCell.forTableColumn());
        arrayTableBColumn.get(0).setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ObservableList, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ObservableList, String> t) {
                        data = (ObservableList) tableB.getItems().get(t.getTablePosition().getRow());
                        String value = data.get(t.getTablePosition().getColumn()).toString();
                        data.set(t.getTablePosition().getColumn(), t.getNewValue());
                    }
                }
        );
    }

    private void drawShapes(GraphicsContext gc, int indexB1, int indexB2) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1.0);//толщина линий
        double stepX = Double.valueOf(maxX / ((Width/2)/scale)).intValue();
        double stepY = Double.valueOf(maxY / ((Height/2)/scale)).intValue();
        for(int i=0;i<gr.getDdb().length;i++){
            if(i<gr.getDdb().length-2) {
                double startX = minX;
                double endX = maxX;
                double startY = (gr.getDdb()[i][2].subtract(gr.getDdb()[i][0]
                        .multiply(new BigFraction(startX)))).divide(gr.getDdb()[i][1]).doubleValue();
                double endY = (gr.getDdb()[i][2].subtract(gr.getDdb()[i][0]
                        .multiply(new BigFraction(endX)))).divide(gr.getDdb()[i][1]).doubleValue();
                gc.strokeLine(Width/2 +startX/stepX*scale,Height/2 -startY/stepY*scale, Width/2 +endX/stepX*scale, Height/2 -endY/stepY*scale);
            }
            else{
                gc.setStroke(Color.RED);
                if(gr.getDdb()[i][0].doubleValue() == 0){
                    double startX = minX;
                    double endX = maxX;
                    double startY = gr.getDdb()[i][2].doubleValue();
                    double endY = gr.getDdb()[i][2].doubleValue();
                    gc.strokeLine(Width/2 +startX/stepX*scale, Height/2 -startY/stepY*scale, Width/2 +endX/stepX*scale, Height/2 -endY/stepY*scale);
                }else{
                    double startX = gr.getDdb()[i][2].doubleValue();
                    double endX = gr.getDdb()[i][2].doubleValue();
                    double startY = minY;
                    double endY = maxY;
                    gc.strokeLine(Width/2 +startX/stepX*scale, Height/2 -startY/stepY*scale, Width/2 +endX/stepX*scale, Height/2 -endY/stepY*scale);
                }
            }
        }
        gc.setFill(Color.CYAN);
        double[] x = new double[gr.getListPoint().size()],y = new double[gr.getListPoint().size()];
        for (Point point : gr.getListPoint()){
            x[gr.getListPoint().indexOf(point)] = Width/2 + (point.getX().doubleValue()/stepX)*scale;
            y[gr.getListPoint().indexOf(point)] = Height/2 - (point.getY().doubleValue()/stepY)*scale;
        }
        gc.fillPolygon(x, y, x.length);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.2);//толщина линий
        for (double i=Height/2;i<Height;i+=scale){
            gc.strokeLine(0, i, Width-10, i);//линии X Down
        }

        for (double i=Height/2;i>10;i-=scale){
            gc.strokeLine(0, i, Width-10, i);//линии X Up
        }

        for (double i=Width/2;i<Width-10;i+=scale){
            gc.strokeLine(i,Height,i,10);//линии Y ->
        }

        for (double i=Width/2;i>0;i-=scale){
            gc.strokeLine(i,Height,i,10);//линии Y <-
        }

        gc.setLineWidth(1);//толщина Осей
        drawArrow(gc, 0, Height/2, Width-10, Height/2);//ОсьХ startX, startY, endX, endY
        gc.fillText("Δb"+indexB2,((Width-10)/2)-25,-((Height-11)/2)+20);
        gc.fillText("Δb"+indexB1,Width-30, 20);
        gc.setFont(new javafx.scene.text.Font("Arial",8)); //Шрифт и размер. Нужна какая-та зависимость размера шрифта от масщтаба scale
        int step, k=0;
        if(40%scale==0) {
            step = Double.valueOf(40 / scale).intValue();
        }else{
            step = Double.valueOf(40 / scale).intValue()+1;
        }
        System.out.println(step);
        int z=0;
        for (double i=(Width/2);i<Width-scale;i+=scale,k++,z++){// По оси х 0 1 2 3
            if((z+1)%(step)==0||z==0) {
                gc.fillText("" + z*Double.valueOf(maxX / ((Width/2-scale)/scale)).intValue(), i, 10);
            }
        }
        k=1;
        z=1;
        for (double i=(Width/2)-scale-5;i>0;i-=scale,k++,z++){// По оси х -1 -2 -3
            if(z%(step)==0)
                gc.fillText("-"+z*Double.valueOf(minX / ((Width/2-scale)/scale)).intValue(),i,10);
        }
        z=1;
        k=1;
        for (double i=scale+10;i<Height/2;i+=scale,k++,z++){// По оси у -1 -2 -3
            if(z%(step)==0)
                gc.fillText("-"+z*Double.valueOf(minY / ((Height/2-scale)/scale)).intValue(),Width/2,i);
        }
        k=1;
        z=1;
        for (double i=-scale+3;i>-(Height/2)+scale+10;i-=scale,k++,z++){// По оси у 1 2 3
            if(z%(step)==0)
                gc.fillText(""+z*Double.valueOf(maxY / ((Height/2-scale)/scale)).intValue(),Width/2,i);
        }
        drawArrow(gc, Width / 2, Height, Width / 2, 10);//ОсьY
    }

    void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) { //Метод отрисовки осей
        gc.setFill(Color.BLACK);
        double ARR_SIZE = 5;
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
                4);
    }

    private void calcDataToCanvas(){
        maxX = gr.getMaxX().doubleValue();
        minX = gr.getMaxX().doubleValue()*(-1);
        minY = gr.getMaxY().doubleValue()*(-1);
        maxY = gr.getMaxY().doubleValue();
    }

    public static ArrayList<TableColumn> getArrayTableAColumn() {
        return arrayTableAColumn;
    }

    public static void setArrayTableAColumn(TableColumn<ObservableList<String>, String> column) {
        arrayTableAColumn.add(column);
    }

    public static ArrayList<TableColumn> getArrayTableBColumn() {
        return arrayTableBColumn;
    }

    public static void setArrayTableBColumn(TableColumn<ObservableList<String>, String> column) {
        arrayTableBColumn.add(column);
    }

    public static ArrayList<TableColumn> getArrayTableCColumn() {
        return arrayTableCColumn;
    }

    public static void setArrayTableCColumn(TableColumn<ObservableList<String>, String> column) {
        arrayTableCColumn.add(column);
    }

    private void drawShapesM1(GraphicsContext gc, int indexB1) {
        answToEq = new StringBuilder();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1.0);//толщина линий
        BigFraction[][] inverse = Validation.getObrAFs();
        BigFraction startX = method.getxBest()[method.getBestFs()[0] - 1]
                .divide(inverse[0][0]).multiply(new BigFraction(-1));
        maxX = ((Width / 2) / scale)*startX.abs().doubleValue();
        System.out.println(maxX);
        minX = maxX*(-1);
        double stepX = maxX / ((Width/2)/scale);
        double xStart = Width/2 +1+ (startX.doubleValue()/stepX)*scale;
        double xEnd = 0;
        answToEq.append("DΔb ={ " + inverse[0][0].doubleValue() +
                "*Δb1 ≧ "+method.getxBest()[method.getBestFs()[0]-1].doubleValue()*(-1) +"}");
        if(inverse[0][0].doubleValue()*minX >= method.getxBest()[method.getBestFs()[0]-1].doubleValue()*(-1)){
            xEnd = Width/2 + (minX/stepX)*scale;
        }else{
            xEnd = Width/2-15 + (maxX/stepX)*scale;
        }
        gc.setLineWidth(4.2);//толщина линий
        gc.strokeLine(xStart,  Height/2, xEnd,  Height/2);
        answToEq.append("}\n\nDb = {" + "Δb1 ≧ " + (method.getB()[0]).doubleValue()*(-1)+" }\n" );
        answToEq.append("\n\nОбласть устойчивости:\n Db∩DΔb = {"+" Δb1 ≧ " + (method.getB()[0]).doubleValue()*(-1)+" }\n");
        answToEq.append("}\n\nНа данной прямой найденный план сохраняет свою оптимальность.");
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.2);//толщина линий
        for (double i=Height/2;i<Height;i+=scale){
            gc.strokeLine(0, i, Width-10, i);//линии X Down
        }

        for (double i=Height/2;i>10;i-=scale){
            gc.strokeLine(0, i, Width-10, i);//линии X Up
        }

        for (double i=Width/2;i<Width-10;i+=scale){
            gc.strokeLine(i,Height,i,10);//линии Y ->
        }

        for (double i=Width/2;i>0;i-=scale){
            gc.strokeLine(i,Height,i,10);//линии Y <-
        }

        gc.setLineWidth(1);//толщина Осей
        drawArrow(gc, 0, Height/2, Width-10, Height/2);
        gc.fillText("Δb"+indexB1,Width-30, 20);
        gc.setFont(new javafx.scene.text.Font("Arial",8)); //Шрифт и размер. Нужна какая-та зависимость размера шрифта от масщтаба scale
        double step = startX.abs().doubleValue();
        int z=0;
        for (double i=Width/2;i<Width-10;i+=scale, z++){
                gc.fillText("" + new BigDecimal(z*step).setScale(2, BigDecimal.ROUND_FLOOR), i, 10);
        }
        z=1;
        for (double i=Width/2-scale;i>0;i-=scale, z++){
                gc.fillText("-"+ new BigDecimal(z*step).setScale(2, BigDecimal.ROUND_FLOOR), i, 10);
        }
    }
}