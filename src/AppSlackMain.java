import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import nlp.NaturalLanguageEngine;

import java.io.IOException;
import java.util.List;

/**
 * Created by a on 2017-04-13.
 */
public class AppSlackMain {

    public static void main(String[] args) {

        NaturalLanguageEngine nlpEngine = NaturalLanguageEngine.getInstance().setDebugMode(true);

        try {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-168613915715-lZ3sqZQmoNlbdwp3kw4yRC5o");
            session.connect();

            SlackChannel channel_general = session.findChannelByName("general"); //make sure bot is a member of the channel.
            SlackMessagePostedListener listener = new SlackMessagePostedListener() {
                @Override
                public void onEvent(SlackMessagePosted slackMessagePosted, SlackSession slackSession) {
                    if(slackMessagePosted.getSender().isBot()) return;
                    String message = slackMessagePosted.getMessageContent();
                    SlackUser slackUser = slackMessagePosted.getSender();
                    System.out.println("Slack Message ::: " + message);
                    List<String> rMsgs = nlpEngine.analyzeInstantly(message, true);
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
