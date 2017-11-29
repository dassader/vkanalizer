import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class RunMeBackup {

    public static final int APP_ID = 6276147;
    public static final int COUNT = 200;
    public static final int SLEEP = 1000;

    public static void main(String[] args) throws ClientException, ApiException, IOException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);

        UserActor actor = new UserActor(, "");

        JsonWriter jsonWriter = new JsonWriter(new FileWriter(new File("messages.bak")));

        Gson gson = new GsonBuilder().create();

        jsonWriter.beginArray();

        int offsetDialog = 0;
        while (true) {
            Thread.sleep(SLEEP);
            List<Dialog> items = vk.messages()
                    .getDialogs(actor)
                    .count(COUNT)
                    .offset(offsetDialog)
                    .execute().getItems();

            System.out.println("Get dialogs: "+items.size());

            for (Dialog dialog : items) {
                System.out.println("Target dialog: "+dialog);
                int offsetHistory = 0;
                while (true) {
                    Thread.sleep(SLEEP-600);
                    List<Message> messages = vk.messages()
                            .getHistory(actor)
                            .userId(dialog.getMessage().getUserId())
                            .count(COUNT)
                            .offset(offsetHistory)
                            .execute().getItems();

                    Integer messageId = 0;
                    if(!messages.isEmpty()) {
                        messageId = messages.get(0).getId();
                    }

                    System.out.println("Get messages: "+messages.size()+" first id: "+ messageId);

                    for (Message message : messages) {
                        String jsonValue = gson.toJson(message);
                        jsonWriter.jsonValue(jsonValue);
                    }

                    if(messages.isEmpty() || messages.size() < COUNT) {
                        break;
                    }

                    offsetHistory += COUNT;
                }
            }

            offsetDialog += COUNT;

            if(items.isEmpty() || items.size() < COUNT) {
                break;
            }
        }

        jsonWriter.endArray();

        jsonWriter.flush();
        jsonWriter.close();
    }
}
