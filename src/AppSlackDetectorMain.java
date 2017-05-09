import com.google.cloud.vision.v1.EntityAnnotation;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import nlp.NaturalLanguageEngine;
import vision.FaceDetector;
import vision.ImageRecognizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by a on 2017-04-20.
 */
public class AppSlackDetectorMain {
    private static String slackAuth = "xoxb-168613915715-ClOiYLxIzb1mtZwoNxjexiRa";
    private static final String helloString = "Richware NLP Engine has been started - Cached Successfully via [Slack]";

    public static void main(String[] args) {

        NaturalLanguageEngine nlpEngine = NaturalLanguageEngine.getInstance().setDebugMode(true);

        try {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackAuth);
            session.connect();

            SlackChannel channel_general = session.findChannelByName("general"); //make sure bot is a member of the channel.

            session.sendMessage(channel_general, helloString);

            SlackMessagePostedListener listener = new SlackMessagePostedListener() {
                @Override
                public void onEvent(SlackMessagePosted slackMessagePosted, SlackSession slackSession) {
                    List<EntityAnnotation> entityList = new ArrayList<>();

                    byte[] data = null;
                    String outName = "";
                    String fileName = "";

                    if(slackMessagePosted.getSender().isBot()) return;
                    SlackFile file = slackMessagePosted.getSlackFile();
                    if(file != null) {
                        try {
                            URL url = new URL(file.getUrlPrivateDownload());

                            URLConnection uc = url.openConnection();
                            uc.setRequestProperty("Authorization", "Bearer " + slackAuth);

                            InputStream content = uc.getInputStream();

                            fileName = Long.toString(Calendar.getInstance().getTimeInMillis());

                            BufferedImage img = ImageIO.read(content);
                            File local = new File(fileName);
                            ImageIO.write(img, file.getFiletype(), local);

                            outName = fileName + "_output.jpg";

                            entityList = ImageRecognizer.getEntitySet(fileName);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }

                    try {
                        FaceDetector.getDetectedPath(fileName, outName);
                        Path path = Paths.get(outName);
                        data = Files.readAllBytes(path);
                    }catch (IOException e){
                    }catch (GeneralSecurityException e){
                    }

                    String message = slackMessagePosted.getMessageContent();
                    SlackUser slackUser = slackMessagePosted.getSender();
                    System.out.println("Slack Message ::: " + message);
                    List<String> rMsgs = nlpEngine.analyzeInstantly(message, true);

                    try {
                        if(data != null) session.sendFile(slackMessagePosted.getChannel(), data, "Face_Detection");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(entityList.size() > 0) {
                        rMsgs.clear();
                        for(EntityAnnotation e : entityList) rMsgs.add("이 사진은 " + e.getDescription() + " 사진 인가요?");
                    }

                    for(String rMsg : rMsgs) {
                        session.sendMessage(slackMessagePosted.getChannel(), rMsg);
                        //session.sendMessageToUser(slackUser, rMsg, null);
                        //session.sendMessage(channel, "GBot has been invited to Richware");
                    }
                }
            };
            session.addMessagePostedListener(listener);

        }catch(IOException e){
            e.printStackTrace();
        }


    }
}
