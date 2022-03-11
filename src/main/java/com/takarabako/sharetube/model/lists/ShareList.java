package com.takarabako.sharetube.model.lists;

import com.takarabako.sharetube.model.users.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "list")
public class ShareList {

  @Id
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "users_oauth2_id")
  private User author;

  private String thumbnail;

  private String title;

  private String description;

  @OneToMany
  @JoinColumn(name = "users_oauth2_id")
  private List<User> subscriber;

  private String channelIds;

  private int views;

  public ShareList(User author, String thumbnail, String title, String description, String channelIds) {
    this(UUID.randomUUID(),author,thumbnail,title,description,new ArrayList<>(), channelIds, 0);
  }

  public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) { this.description = description; }

  public void setChannelIds(String channelIds) {
    this.channelIds = channelIds;
  }

  public void addSubscriber(User user) {
    try {
      subscriber.add(user);
    } catch (Exception e) {
      throw new IllegalArgumentException("unexpected error occurred when add subscriber. cause : " + e.getMessage(), e.getCause());
    }
  }

  public void removeSubscriber(User user) {
    try {
      subscriber.remove(user);
    } catch (Exception e) {
      throw new IllegalArgumentException("unexpected error occurred when remove subscriber. cause : " + e.getMessage(), e.getCause());
    }
  }

  public void addViews() {
    views++;
  }

}
