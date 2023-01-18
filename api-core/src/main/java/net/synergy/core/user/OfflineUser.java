package net.synergy.core.user;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class OfflineUser {

  /**
   * @see UUID
   * @return the unique id of the user.
   */
  private final UUID UUID;

  /**
   * Name given upon join or retrieved from database.
   *
   * @return the user's name
   */
  private final String name;

}
