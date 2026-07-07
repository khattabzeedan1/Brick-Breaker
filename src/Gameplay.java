import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean start = false;
    private int score = 0;
    private int totalBricks = 48;

    private int delay = 10;
    private int playerX = 310;
    private int ballposX = 310;
    private int ballposY = 350;
    private int ballXdir = 3;
    private int ballYdir = -6;
    private int minutes = 0;
    private int seconds = 0;
    private long startTime = System.currentTimeMillis();
    private Timer timer;
    private MapGenerator mapObj;


    public Gameplay() {
        super();//هنا يتم استدعاء الpaint بعد عدة خطوات
        mapObj = new MapGenerator(3, 7);//هنا يتم تهيئة مصفوفة 2D Array بحيث يتم اعطاء كل طوبة طول وعرض على حسب عدد الطوب ويتم تخزين قيمة one داخل كل حقل لإعلامنا بان هذه الطوبة ستظهر على الشاشة وعند ميثود ال paint يتم رسم الحقل الذي يحتوي على قيمة 1 فقط
        addKeyListener(this);//في هذه الميثود يتم عمل keyPressed بين الاقواس بالعادة لكن هنا نفس الشيئ تقريبا فقط اننا قمنا بعمل implements for KeyListener في داخل الكلاس , لانه هو انترفيس او ابستراكت كلاس
        setFocusable(true); //لانه هون عملنا extend for Jpanel وليس JFrame فيجب عليك تفعيل الفوكس للكيبورد عشان يصير يستقبل البتاع
        timer = new Timer(10, this);//قصة this هون نفس قصة keyListener بس هون عملنا overried/implements لميثود ال actionPerformed التي ضمن انترفيس ال ActionListener
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(new Color(0, 103, 153));
        g.fillRect(0, 0, 692, 592);

        // drawing mapObj
        mapObj.draw((Graphics2D) g);


        // the scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("score : " + score, 570, 30);

        // the paddle
        g.setColor(Color.BLACK);
        g.fillRect(playerX, 550, 100, 8);

        // the ball
        g.setColor(Color.white);
        g.fillOval(ballposX, ballposY, 20, 20);

        // when you won the game
        if (totalBricks == 0) {
            start = false;
            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won ", 260, 300);
        }

        // when you lose the game
        if (ballposY > 570) {
            start = false;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores : " + score, 190, 300);
            g.drawString(" Click Enter to play again ", 190, 340);
        }
        g.drawString(minutes + " : " + (seconds < 10 ? "0" + seconds : seconds), 100, 30);
    }

    public void keyPressed(KeyEvent e) {//اول ما يتم الضفط عالبتاع
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 580) {//هنا اشارة اكبر لان الزيادات الي بتصير على المضرب ممكن ما توصل لمرحلة انها تساوي 600 ممكن تفشق وتصير تساوي اكبر من ال 600
                playerX = 580;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!start) {
                start = true;
                ballposX = 310;
                ballposY = 350;
                ballXdir = 4;
                ballYdir = -3;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                mapObj = new MapGenerator(3, 7);
                startTime = System.currentTimeMillis();
                repaint();
            }
        }
    }

    public void keyReleased(KeyEvent e) {// بعد الضغط عند الافلات
    }

    public void keyTyped(KeyEvent e) {// للاحرف ما بتهمنا
    }

    public void moveRight() {
        start = true;
        playerX += 40;
    }


    public void moveLeft() {
        start = true;
        playerX -= 40;
    }

    public void actionPerformed(ActionEvent e) {
        //في حال المستخدم ضغط على انتر او على سهم اليمين او اليسار سيتم اعطاء start قيمة true لذلك سيشتغل هون
        if (start) {
            long elapsed = System.currentTimeMillis() - startTime;//هون بنقص الوقت يلي ضغطت في انتر من الوقت الحالي
            seconds = (int) (elapsed / 1000);//بقسم على 1000 عشان اشوف كم ثانية بس لو كانت مثلا ال seconds = 80 مش رح نكتبها هيك بس بعد سطرين بنطول باقي القسمة على 60
            minutes = seconds / 60;//بقسم الثواني على 60 لاشوف كم دقيقة
            seconds %= 60;//عشان اطبع الثواني التقليدية

            //هون بالشرط بسال اذا المضلع الخاص بالكرة تقاطع مع المضلع الخاص بالمضرب
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                int paddleCenter = playerX + 50;//نحدد منتصف عرض المضرب
                int ballCenter = ballposX + 10;//نحدد منتصف عرض الكرة
                int diff = ballCenter - paddleCenter;//الفرق بين مركز الكرة ومركز المضرب , اذا كانت جاي الكرة من "يمين المضرب" بتكون قيمه الdiff موجبة اما اذا من يساره بتكن سالبة
                ballXdir = diff / 10;//فهون منحدد المقدار الذي يجب ان يتزايد لتسر الكرة بالمسار الفيزيائي الصحيح ... وطبعا المسار ليس واقعي ميه بالميه فلهيك احنا منحاول نقدر تقدير وطبعا هاي احدالاسباب انا قسمنا على 10 لانه قيمة ال diff كبيرة نسبيا وتجعل الكرة تسير بشكل عشوائي
                ballYdir = -ballYdir;// اذا الكرة نازله نزول بتكون قيمة y تزداد ولعكس المسار نغير المقدار المضاف لسالب ليبدأ موقع الكرة بالنقصان وكل ما اتجهنا الى اعلى قلت y (لان النقة (0,0)تقع في الزاوية العلوية اليسرى للشاشة)
            }

            for (int i = 0; i < mapObj.map.length; i++) {
                for (int j = 0; j < mapObj.map[0].length; j++) {
                    if (mapObj.map[i][j] == 1) {// سيتم فرض وجود لكل طوبة من الطوب
                        int brickWidth = mapObj.brickWidth;
                        int brickHeight = mapObj.brickHeight;
                        int brickX = j * brickWidth + 80;//لايجاد قيمة x للزاوية اليسرى العلوية لكل طوبة , حيث 80 هي البعد بين الحافة الجانبية لل frame والمصفوفةال 2DArray
                        int brickY = i * brickHeight + 50;//لايجاد قيمة y للزاوية اليسرى العلوية لكل طوبة, حيث 50 هي البعد بين الحافة العلوية لل frame والمصفوفةال 2DArray

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);//تكوين محيط لكل طوبة
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);//تكوين محيط للكرة

                        if (ballRect.intersects(rect)) {//هل محيط الكرة خبط بمحيط بالطوبة؟
                            mapObj.setBrickValue(0, i, j);//تخزين قيمة 0 في كل حقل تم ملامسة الكرة بمحيطه الواقعي
                            score += 5;
                            totalBricks--;//بنقص عدد الطوب عشان اشوف متى حيساوي صفر ويفوز اخينا

                            // لما الطابة تضرب الطوبة من احد الجنبين بدي اعكس اتجاه x فقط
                            if (ballposX + 20 <= rect.x || ballposX >= rect.x + rect.width) {
                                ballXdir = -ballXdir;
                            }
                            //لما الطابة تضرب الطوبة من فوق او من تحت
                            else {
                                ballYdir = -ballYdir;
                            }

                            break;//مش كثير بتاثر بس هاي باختصار عشان لما محيط الكرة يخبط بمحيط الطوبة رح تختفي الطوبة صح فلهيك إذاً انا وصلت لعند الطوبة يلي خبطتها الكرة فما في داعي اكمل فحص في نفس ال 10 ميلي ثانية انت فاهم علي ليش اشرح اكثر
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            //خليني انوه على شغله هون مش if else لانو اذا الكرة بنفس اللحظة كانت تساوي الصفر للxوy بدي اعكس اتجاه x واتحاه y ... يعني باختصار اذا لمست احدى الزاويتين العلويتين
            if (ballposX <= 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY <= 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX >= 670) {
                ballXdir = -ballXdir;
            }

            repaint();
        }
    }
}