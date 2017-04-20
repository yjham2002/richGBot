import com.google.cloud.vision.v1.EntityAnnotation;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import nlp.NaturalLanguageEngine;
import vision.ImageRecognizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by a on 2017-04-13.
 */
public class AppSlackMain {

    private static String slackAuth = "xoxb-168613915715-4vHmjm58Oo6bUqZhwsTAtZhL";

    public static void main(String[] args) {

        NaturalLanguageEngine nlpEngine = NaturalLanguageEngine.getInstance().setDebugMode(true);

        try {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackAuth);
            session.connect();

            SlackChannel channel_general = session.findChannelByName("general"); //make sure bot is a member of the channel.
            SlackMessagePostedListener listener = new SlackMessagePostedListener() {
                @Override
                public void onEvent(SlackMessagePosted slackMessagePosted, SlackSession slackSession) {
                    List<EntityAnnotation> entityList = new ArrayList<>();

                    if(slackMessagePosted.getSender().isBot()) return;
                    SlackFile file = slackMessagePosted.getSlackFile();
                    if(file != null) {
                        try {
                            URL url = new URL(file.getUrlPrivateDownload());

                            URLConnection uc = url.openConnection();
                            uc.setRequestProperty("Authorization", "Bearer " + slackAuth);

                            InputStream content = uc.getInputStream();

                            String fileName = Long.toString(Calendar.getInstance().getTimeInMillis());

                            BufferedImage img = ImageIO.read(content);
                            File local = new File(fileName);
                            ImageIO.write(img, file.getFiletype(), local);

                            entityList = ImageRecognizer.getEntitySet(fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    String message = slackMessagePosted.getMessageContent();
                    SlackUser slackUser = slackMessagePosted.getSender();
                    System.out.println("Slack Message ::: " + message);
                    List<String> rMsgs = nlpEngine.analyzeInstantly(message, true);

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
