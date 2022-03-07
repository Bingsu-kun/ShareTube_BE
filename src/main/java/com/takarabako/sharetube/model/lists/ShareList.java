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

  private String title;

  @OneToMany
  @JoinColumn(name = "users_oauth2_id")
  private List<User> subscriber;

  @ElementCollection
  @CollectionTable(
          name = "channels",
          joinColumns = @JoinColumn(name = "lists_id")
  )
  private List<String> channels;

  private int views;

  public ShareList(User author, String title, List<String> channels) {
    this(null,author,title,new ArrayList<User>(), channels, 0);
  }

  public boolean addSubscriber(User user) {
    try {
      subscriber.add(user);
      return true;
    } catch (Exception e) {
      throw new IllegalArgumentException("unexpected error occurred when add subscriber. cause : " + e.getMessage(), e.getCause());
    }
  }

  public boolean removeSubscriber(User user) {
    try {
      subscriber.remove(user);
      return true;
    } catch (Exception e) {
      throw new IllegalArgumentException("unexpected error occurred when remove subscriber. cause : " + e.getMessage(), e.getCause());
    }
  }

  public void addViews() {
    views++;
  }

}
