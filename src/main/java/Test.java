
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.groups.GroupsGetLongPollServerQuery;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import core.Commander;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws NullPointerException, ClientException, ApiException, InterruptedException, IOException {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        Properties prop = new Properties();
        prop.load(new FileInputStream("vkconfig.properties"));

        int groupId = Integer.valueOf(prop.getProperty("groupId"));
        String access_token = prop.getProperty("accessToken");

        GroupActor actor = new GroupActor(groupId, access_token);

        vk.messages().send(actor).userId(255396611).message("ROFFFL");
        GroupsGetLongPollServerQuery response = vk.groups().getLongPollServer(actor);

        System.out.println("Server Started!");
        int ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true) {
            Thread.sleep(300);
            MessagesGetLongPollHistoryQuery eventsQuery = vk.messages().getLongPollHistory(actor);
            eventsQuery.ts(ts);
            ts =  vk.messages().getLongPollServer(actor).execute().getTs();

            List<Message> messages = eventsQuery.execute().getMessages().getMessages();

            if (!messages.isEmpty() && !messages.get(0).isOut()){
                System.out.println(messages.get(0));
                vk.messages().send(actor).peerId(messages.get(0).getUserId()).message(Commander.getResponse(messages.get(0).getBody())).execute();
            }

        }

    }
}