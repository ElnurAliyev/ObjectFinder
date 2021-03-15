import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.awt.event.MouseListener;
import java.time.Clock;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.stream.Collectors;

public class Main extends Application {
    public Slider slider = new Slider(1, 10, 5);
    public static Label label = new Label("");

    public static Process p;
    public BarChart<String,Number> bc;
    public static javafx.scene.image.Image lastFilter = new javafx.scene.image.Image("file:images\\img" + 0 + ".png");;


    public static void main(String[] args)  {
        try{
            p = Runtime.getRuntime().exec("py main.py");

        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {


        Canvas canvas = new Canvas(450, 450);
        HBox hBoxMain = new HBox();
        VBox vBoxForImages = getHBox4(canvas);
        VBox vBox = new VBox();
        vBoxForImages.setMaxSize(328,656);
        vBox.setStyle("-fx-background-color: white;");
        vBox.getChildren().add(getHBox1(canvas));
        vBox.getChildren().add(getHBox2(canvas));
        vBox.getChildren().add(getHBox3(canvas));

        hBoxMain.getChildren().addAll(vBoxForImages,vBox);
        stage.setTitle("Slow, Draw!");
        Scene scene = new Scene(hBoxMain, 818, 750);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public VBox getHBox4(Canvas canvas) {
        ArrayList<HBox> lines = new ArrayList<HBox>();
        int curImage = 0;
        for(int j = 0 ; j< 8;j++){
            lines.add(new HBox());

            for(int i = 0; i< 4&& curImage<32;i++){
                javafx.scene.image.Image img = new javafx.scene.image.Image("file:images\\img"+curImage+".png");
                lines.get(j).getChildren().add(new ImageView(img));
                lines.get(j).setSpacing(2);
                curImage++;
            }
        }



        VBox hBox = new VBox();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    while (lastFilter == null) {
                        try {
                            lastFilter = new javafx.scene.image.Image("file:images\\img" + 0 + ".png");
                        } catch (Exception ec) {
                            System.out.println(ec);

                        }
                    }
                    javafx.scene.image.Image newFilter = null;

                    while(newFilter==null){
                        try {
                            newFilter = new javafx.scene.image.Image("file:images\\img" + 0 + ".png");
                        } catch (Exception ec) {
                            System.out.println(ec);
                        }
                    }

                    try{
                    if(lastFilter!=null && newFilter!=null && !equalImage(lastFilter,newFilter)) {
                        lastFilter = newFilter;
                        int curImagee = 0;
                        for (int j = 0; j < 8; j++) {

                            for (int i = 0; i < 4 && curImagee < 32; i++) {


                                javafx.scene.image.Image img = null;
                                while(img==null){
                                    if(new File("images\\img" + curImagee + ".png").exists())
                                        img = new javafx.scene.image.Image("file:images\\img" + curImagee + ".png");
                                }
                                lines.get(j).getChildren().set(i, new ImageView(img));
                                curImagee++;
                            }
                        }
                    }
                    }catch (Exception ec) {
                    System.out.println(ec);
                }


                });
            }
        }, 0, 100);


        hBox.setSpacing(2);
        hBox.getChildren().addAll(lines);
        hBox.setStyle("-fx-border-color: black;");
        return hBox;
    }

    public HBox getHBox3(Canvas canvas) throws Exception{
        ArrayList<String> names  = new ArrayList<String>();
        ArrayList<Double> probs  = new ArrayList<Double>();
        SortedMap<Double,String> map = new TreeMap<Double,String>(Collections.reverseOrder());

        File file = new File("probs.txt");
        Scanner sc = new Scanner(file);
        int cur = 0;
        while (sc.hasNext()) {
             names.add(sc.next());
             probs.add(Double.valueOf( sc.next() ) );
             map.put(probs.get(cur),names.get(cur++));
        }
        System.out.println(probs.toString());
        sc.close();

        System.out.println(names.toString());

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        bc = new BarChart<String,Number>(xAxis,yAxis);

        bc.setTitle("Probability");
        yAxis.setLabel("%");

        XYChart.Series series1 = new XYChart.Series();
        Set s = map.entrySet();

        // Using iterator in SortedMap
        Iterator z = s.iterator();


        for(int i = 0; i< 5&& z.hasNext();i++){
            Map.Entry m = (Map.Entry)z.next();
            series1.getData().add(new XYChart.Data((String)m.getValue(), (int)(100*(Double)m.getKey())));
        }


        bc.getData().addAll(series1);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 12, 0, 0));
        hBox.setSpacing(10);
        hBox.setMinSize(450,100);
        hBox.getChildren().add(bc);
        hBox.setStyle("-fx-border-color: black;");
        bc.setAnimated(false);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
//                        System.out.println("Chart Started");
                        Scanner scan = new Scanner(file);
                        names.clear();
                        probs.clear();
                        map.clear();
                        int temp = 0;
                        while (scan.hasNext()) {
                            names.add(scan.next());
                            probs.add(Double.valueOf(scan.next()));
                            map.put(probs.get(temp), names.get(temp++));
                        }
                        scan.close();

                        Set ss = map.entrySet();
                        Iterator zz = ss.iterator();
                        for (int i = 0; i < 5 && zz.hasNext(); i++) {
                            Map.Entry m = (Map.Entry) zz.next();
                            series1.getData().set(i, new XYChart.Data((String) m.getValue(), (int) (100 * (Double) m.getKey())));


                        }
                    } catch (Exception exxccc) {
                        System.out.println(exxccc.toString());
                    }
                });
            }
        }, 0, 500);



        return hBox;
    }

    public HBox getHBox1(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            graphicsContext.setLineWidth(slider.getValue());
            graphicsContext.beginPath();
            graphicsContext.moveTo(e.getX(), e.getY());
            graphicsContext.stroke();
            graphicsContext.closePath();
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            graphicsContext.lineTo(e.getX(), e.getY());
            graphicsContext.stroke();
        });
        HBox hBox = new HBox();
        hBox.setMaxSize(450,450);
        hBox.getChildren().add(canvas);
        hBox.setStyle("-fx-border-color: black;");
        return hBox;
    }

    public HBox getHBox2(Canvas canvas) {

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 12, 0, 0));
        hBox.setSpacing(10);

        Button erase = new Button("Erase");
        erase.setStyle("-fx-background-color: #ff0000; -fx-font-weight: bold; -fx-text-fill: white;");
        erase.setOnAction(e -> {
            if (erase.getText().equals("Erase")) {
                graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                slider.setValue(5);
                label.setText("");
                label.setFont(new Font(20));
            }
        });
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);

        Button guess = new Button("Guess");


        guess.setStyle("-fx-background-color: turquoise; -fx-font-weight: bold;");
        guess.setOnAction(e -> {
            if (guess.getText().equals("Guess")) {

                try {
                    WritableImage snapshot = canvas.snapshot(null, null);
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("a.png"));
                    BufferedImage toCrop = ImageIO.read(new File("a.png"));
                    int TopH = 0, BottomH = 0, TopW = 0, BottomW = 0;

                    for (int i = 0; i < toCrop.getHeight() && TopH == 0; i++) {
                        for (int j = 0; j < toCrop.getWidth(); j++) {
                            if (toCrop.getRGB(j, i) != -1) {
                                TopH = i;
                                break;
                            }
                        }
                    }

                    for (int i = toCrop.getHeight() - 1; i >= 0 && BottomH == 0; i--) {
                        for (int j = 0; j < toCrop.getWidth(); j++) {
                            if (toCrop.getRGB(j, i) != -1) {
                                BottomH = i;
                                break;
                            }
                        }
                    }

                    for (int j = 0; j < toCrop.getWidth() && TopW == 0; j++) {
                        for (int i = 0; i < toCrop.getHeight(); i++) {
                            if (toCrop.getRGB(j, i) != -1) {
                                TopW = j;
                                break;
                            }
                        }
                    }

                    for (int j = toCrop.getWidth() - 1; j >= 0 && BottomW == 0; j--) {
                        for (int i = 0; i < toCrop.getHeight(); i++) {
                            if (toCrop.getRGB(j, i) != -1) {
                                BottomW = j;
                                break;
                            }
                        }
                    }

//                    System.out.println(TopH);
//                    System.out.println(TopW);
//                    System.out.println(BottomH);
//                    System.out.println(BottomW);
                    int size = Math.max(BottomW - TopW, BottomH - TopH)+40;
                    int initW = TopW- (size-(BottomW - TopW))/2;
                    int initH = TopH - (size-(BottomH - TopH))/2;
                    int endW = size;
                    int endH = size;
                    if(initW+size>=canvas.getWidth())
                        endW = (int)(canvas.getWidth()-initW);
                    if(initH+size>=canvas.getHeight())
                        endH = (int)(canvas.getHeight()-initH);
                    toCrop = toCrop.getSubimage(Math.max(initW,0), Math.max(initH,0), endW, endH);
                    Image temp = toCrop.getScaledInstance(28, 28, Image.SCALE_AREA_AVERAGING);
                    toCrop = toBufferedImage(temp);

                    for (int i = 0; i < toCrop.getHeight(); i++) {
                        for (int j = 0; j < toCrop.getWidth(); j++) {
                            Color a = new Color(toCrop.getRGB(j, i));
                            Color updated;
                            if(a.equals(Color.white)){
                                updated = new Color(255 - a.getRed(), 255 - a.getGreen(), 255 - a.getBlue());
                            }
                            else
                                updated = new Color(Math.min(255,255 - a.getRed()+100), Math.min(255 - a.getGreen()+100,255), Math.min(255 - a.getBlue()+100,255));

                            toCrop.setRGB(j, i, updated.getRGB());
                        }
                    }

                    File outputfile = new File("image.png");
                    ImageIO.write(toCrop, "png", outputfile);


//                    Process p = Runtime.getRuntime().exec("C:\\Users\\Elnur\\AppData\\Local\\Programs\\Python\\Python37\\python main.py");
//                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//                    String line = in.readLine();
//                    while(line !=null){
//                        System.out.println(line);
//                        line = in.readLine();
//                    }
//                    in.close();



                    //System.out.println(p.isAlive());


                    //Creating an InputStream object
                    //p.toHandle();


                    File file = new File("result.txt");
                    Scanner sc = new Scanner(file);
                    String output = "";
                    while (sc.hasNextLine()) {
                        output = sc.nextLine();
                    }
                    sc.close();
                    //System.out.println(output);
                    label.setText("It is a(n): " + output);

                } catch (IOException exception) {
                    System.out.println(exception.toString());

                }
            }
        });

        //canvas.addEventFilter(MouseEvent.MOUSE_RELEASED,event -> {guess.fire();});
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        guess.fire();
//                        System.out.println("Guess Started");
//                        File file = new File("result.txt");
//                        Scanner sc = new Scanner(file);
//
//                        String output = "";
//                        while (sc.hasNextLine()) {
//                            output = sc.nextLine();
//                        }
//                        sc.close();
//                        //System.out.println(output);
//                        label.setText("It is a(n): " + output);
                    } catch (Exception exxccc) {
                        System.out.println(exxccc.toString());
                    }
                });
            }
        }, 0, 100);



        hBox.getChildren().add(erase);
        hBox.getChildren().add(slider);
        hBox.getChildren().add(guess);

        hBox.getChildren().add(label);
        label.setFont(new Font(20));
        return hBox;
    }




    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public boolean equalImage(javafx.scene.image.Image a , javafx.scene.image.Image b){
        if(a==null||b==null)
            return true;
        for (int i = 0; i < a.getWidth(); i++)
        {
            for (int j = 0; j < a.getHeight(); j++)
            {
                if(a.getPixelReader()==null || b.getPixelReader()==null)
                    return true;
                if(a.getPixelReader().getColor(i, j)==null || b.getPixelReader().getColor(i, j) ==null)
                    return true;
                if (!a.getPixelReader().getColor(i, j).equals( b.getPixelReader().getColor(i, j)))
                {
                    return false;
                }

            }
        }
        return true;
    }
}