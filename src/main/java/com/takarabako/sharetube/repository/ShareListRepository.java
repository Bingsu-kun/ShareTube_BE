package com.takarabako.sharetube.repository;

import com.takarabako.sharetube.model.lists.ShareList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShareListRepository extends JpaRepository<ShareList, UUID> {
}
