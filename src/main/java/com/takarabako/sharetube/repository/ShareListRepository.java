package com.takarabako.sharetube.repository;

import com.takarabako.sharetube.model.lists.ShareList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShareListRepository extends JpaRepository<ShareList, UUID> {
}
