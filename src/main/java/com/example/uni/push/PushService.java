package com.example.uni.push;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {

    private final PushSubscriptionRepository repo;

    @Value("${vapid.public}")
    String vapidPublic;

    @Value("${vapid.private}")
    String vapidPrivate;

    public void save(Long userId, String endpoint, String p256dh, String auth) {
        var entity = repo.findByUserId(userId).orElseGet(() -> PushSubscriptionEntity.builder().userId(userId).build());
        entity.setEndpoint(endpoint);
        entity.setP256dh(p256dh);
        entity.setAuth(auth);
        repo.save(entity);
    }

    public void sendToUser(long userId, String title, String body, String url) throws Exception {
        var subOpt = repo.findByUserId(userId);
        if (subOpt.isEmpty()) return;

        var sub = subOpt.get();
        var subscription = new nl.martijndwars.webpush.Subscription(
                sub.getEndpoint(),
                new nl.martijndwars.webpush.Subscription.Keys(sub.getP256dh(), sub.getAuth())
        );

        var pushService = new nl.martijndwars.webpush.PushService();
        pushService.setPublicKey(vapidPublic);
        pushService.setPrivateKey(vapidPrivate);
        pushService.setSubject("mailto:you@example.com");

        var json = """
          {"title": %s, "body": %s, "url": %s}
        """.formatted(toJson(title), toJson(body), toJson(url));

        var notification = new nl.martijndwars.webpush.Notification(subscription, json);
        pushService.send(notification);
    }

    private String toJson(String s) {
        return "\"" + s.replace("\"","\\\"") + "\"";
    }
}
