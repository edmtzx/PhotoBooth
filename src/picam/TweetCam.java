/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picam;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import twitter4j.DirectMessage;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserMentionEntity;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author edgmarti
 */
public class TweetCam {

    public static String PICTUREFILENAME = "picamera";
    public static String JAVA_CAM_TWITTER_NAME = "JavaPiCam";
    public static String fileName;
    static Runtime rt = Runtime.getRuntime();
    static int increment;
    static StatusUpdate st;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Your Twitter App's Consumer Key
        final String consumerKey = "Here goes my key";

        //Your Twitter App's Consumer Secret
        final String consumerSecret = "myconsumerSecret";

        //Your Twitter Access Token
        final String accessToken = "myToken";

        //Your Twitter Access Token Secret
        final String accessTokenSecret = "myTokenSecret";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        ConfigurationBuilder cb2 = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        cb2.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
                .getInstance();
        TwitterFactory factory = new TwitterFactory(cb2.build());
        Twitter twitter = factory.getInstance();

        UserStreamListener listener = new UserStreamListener() {
            @Override
            public void onStatus(Status status) {
                if (status.getUserMentionEntities() != null) {
                    // the tweet has mention(s)
                    for (UserMentionEntity ume : status.getUserMentionEntities()) {
                        System.out.println("ume.getName:" + ume.getName() + "final:" + JAVA_CAM_TWITTER_NAME);
                        if (ume.getName().equals(JAVA_CAM_TWITTER_NAME)) {
                            //Here goes the picture snapshot
                            try {
                                System.out.println("Tweet received ---->");
                                fileName = "/root/img/" + PICTUREFILENAME + increment++ + ".jpg";
                                Process p = rt.exec("raspistill -t 8000 -o " + fileName
                                        + " -w 800 -h 600");
                                p.waitFor();
                                //Reply to user
                                st = new StatusUpdate("Hello " + status.getUser().getScreenName() + "!");
                                st.inReplyToStatusId(status.getId());
                                File imageFile = new File(fileName);
                                FileInputStream fi = new FileInputStream(imageFile);
                                st.setMedia(
                                        //title of media
                                        "Raspberry Pi Cam snapshot", fi);
                                Status status_update = twitter.updateStatus(st);

                                //response from twitter server
                                System.out.println("status.toString() = " + status_update.toString());
                                System.out.println("status.getInReplyToScreenName() = " + status_update.getInReplyToScreenName());
                                System.out.println("status.getSource() = " + status_update.getSource());
                                System.out.println("status.getText() = " + status_update.getText());

                                System.out.println("status.getURLEntities() = " + Arrays.toString(status_update.getURLEntities()));
                                System.out.println("status.getUserMentionEntities() = " + Arrays.toString(status_update.getUserMentionEntities()));
                            } catch (Exception e) {
                                System.out.println("Exception:" + e.getMessage());
                            }
                        }
                    }
                    System.out.println("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onDeletionNotice(long directMessageId, long userId) {
                System.out.println("Got a direct message deletion notice id:" + directMessageId);
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onFriendList(long[] friendIds) {
                System.out.println("onFriendList");
                for (long friendId : friendIds) {
                    System.out.print(" " + friendId);
                }
            }

            @Override
            public void onFavorite(User source, User target, Status favoritedStatus) {
                System.out.println("onFavorite source:@"
                        + source.getScreenName() + " target:@"
                        + target.getScreenName() + " @"
                        + favoritedStatus.getUser().getScreenName() + " - "
                        + favoritedStatus.getText());
            }

            @Override
            public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
                System.out.println("onUnFavorite source:@"
                        + source.getScreenName() + " target:@"
                        + target.getScreenName() + " @"
                        + unfavoritedStatus.getUser().getScreenName()
                        + " - " + unfavoritedStatus.getText());
            }

            @Override
            public void onFollow(User source, User followedUser) {
                System.out.println("onFollow source:@"
                        + source.getScreenName() + " target:@"
                        + followedUser.getScreenName());
            }

            @Override
            public void onDirectMessage(DirectMessage directMessage) {
                System.out.println("onDirectMessage text:"
                        + directMessage.getText());
            }

            @Override
            public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
                System.out.println("onUserListMemberAddition added member:@"
                        + addedMember.getScreenName()
                        + " listOwner:@" + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
                System.out.println("onUserListMemberDeleted deleted member:@"
                        + deletedMember.getScreenName()
                        + " listOwner:@" + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
                System.out.println("onUserListSubscribed subscriber:@"
                        + subscriber.getScreenName()
                        + " listOwner:@" + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
                System.out.println("onUserListUnsubscribed subscriber:@"
                        + subscriber.getScreenName()
                        + " listOwner:@" + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListCreation(User listOwner, UserList list) {
                System.out.println("onUserListCreated  listOwner:@"
                        + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListUpdate(User listOwner, UserList list) {
                System.out.println("onUserListUpdated  listOwner:@"
                        + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserListDeletion(User listOwner, UserList list) {
                System.out.println("onUserListDestroyed  listOwner:@"
                        + listOwner.getScreenName()
                        + " list:" + list.getName());
            }

            @Override
            public void onUserProfileUpdate(User updatedUser) {
                System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
            }

            @Override
            public void onBlock(User source, User blockedUser) {
                System.out.println("onBlock source:@" + source.getScreenName()
                        + " target:@" + blockedUser.getScreenName());
            }

            @Override
            public void onUnblock(User source, User unblockedUser) {
                System.out.println("onUnblock source:@" + source.getScreenName()
                        + " target:@" + unblockedUser.getScreenName());
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
                System.out.println("onException:" + ex.getMessage());
            }
        };

        twitterStream.addRateLimitStatusListener(new RateLimitStatusListener() {
            @Override
            public void onRateLimitStatus(RateLimitStatusEvent event) {
                System.out.println("Limit[" + event.getRateLimitStatus().getLimit() + "], Remaining[" + event.getRateLimitStatus().getRemaining() + "]");
                System.out.println("Limit[" + event.getRateLimitStatus().getLimit() + "], Remaining[" + event.getRateLimitStatus().getRemaining() + "]");
            }

            @Override
            public void onRateLimitReached(RateLimitStatusEvent event) {
                System.out.println("Limit[" + event.getRateLimitStatus().getLimit() + "], Remaining[" + event.getRateLimitStatus().getRemaining() + "]");
                System.out.println("Limit[" + event.getRateLimitStatus().getLimit() + "], Remaining[" + event.getRateLimitStatus().getRemaining() + "]");
            }
        });
        twitterStream.addListener(listener);
        twitterStream.user();
    }

}
