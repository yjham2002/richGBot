import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import relations.Linker;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by a on 2017-04-13.
 */
public class AppSlackMain {

    public static void main(String[] args) {

        Komoran komoran = new Komoran("C:\\Users\\a\\IdeaProjects\\richGBot\\models");
        //komoran.setUserDic("C:\\Users\\a\\IdeaProjects\\GBot\\user_data\\NIADic.user");
        Linker linker = new Linker();

        try {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-168613915715-JI51o6LNBHxwJUkcYrqcHa3r");
            session.connect();

            SlackChannel channel_general = session.findChannelByName("general"); //make sure bot is a member of the channel.
            SlackMessagePostedListener listener = new SlackMessagePostedListener() {
                @Override
                public void onEvent(SlackMessagePosted slackMessagePosted, SlackSession slackSession) {
                    if(slackMessagePosted.getSender().isBot()) return;
                    String message = slackMessagePosted.getMessageContent();
                    SlackUser slackUser = slackMessagePosted.getSender();

                    System.out.println("Slack Message ::: " + message);
                    List<List<Pair<String, String>>> result = komoran.analyze(message);
                    linker.setMorphemes(result, message);
                    List<String> rMsgs = linker.interaction();
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
