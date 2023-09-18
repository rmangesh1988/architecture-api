package com.architecture.api.repository;

import com.architecture.api.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    @Modifying
    @Query("update Site s set s.version = :newVersion where s.version = :currentVersion")
    int updateSiteVersion(@Param("currentVersion") Integer currentVersion, @Param("newVersion") Integer newVersion);

}
