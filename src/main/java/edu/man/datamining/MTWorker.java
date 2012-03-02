package edu.man.datamining;

import edu.man.datamining.MetaTag;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import org.json.JSONObject;

import java.io.IOException;

public class MTWorker extends JedisPubSub {
  private Jedis jedis;
  private Jedis publisher;
  private MetaTag mt;

  public void onMessage(String channel, String message) {
    System.out.println("Got: "+message);
    try {
      JSONObject msg = new JSONObject(message);
      JSONObject resp = new JSONObject().put("socket_key", msg.get("socket_key"))
                                        .put("data", (JSONObject) mt.getSemTypes((String) msg.get("phrase"), false, 0));
      publisher.publish("extract-terms-ret", resp.toString());
    } catch (Exception e) {
      System.err.println("Unable to get sem types: ");
      System.err.println(e.getMessage());
      mt.disconnect();
      mt = getMT();
      this.onMessage(channel, message);
    }
  }

  public void onPMessage(String pattern, String channel, String message) {}
  public void onPSubscribe(String pattern, int subscribedChannels) {}
  public void onPUnsubscribe(String pattern, int subscribedChannels) {}
  public void onSubscribe(String channel, int subscribedChannels) {
    System.out.println("Subscribed to channel: "+channel);
    System.out.println("No channels: "+subscribedChannels);

  }
  public void onUnsubscribe(String channel, int subscribedChannels) {
    jedis.quit();
  }

  private MetaTag getMT() {
    try { return new MetaTag("localhost", 8888); }
    catch (IOException e) {
      System.out.println("Error trying to load filtered semtypes: ");
      System.out.println(e.getMessage());
      return null;
    }
  }

  public MTWorker() {
    jedis = new Jedis("localhost");
    jedis.auth("jredis");
    publisher = new Jedis("localhost");
    publisher.auth("jredis");
    mt = getMT();
    jedis.subscribe(this, "extract-terms");
  }

  public static void main(String[] args) {
    MTWebWorker worker = new MTWorker();
  }
}
