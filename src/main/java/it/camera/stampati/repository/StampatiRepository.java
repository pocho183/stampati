package it.camera.stampati.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.camera.stampati.domain.Stampato;

import java.util.List;

@Repository
public interface StampatiRepository extends JpaRepository<Stampato, String> {

	List<Stampato> findByLegislaturaId(String legislaturaId);
	
    @Query("SELECT s FROM Stampato s WHERE s.legislaturaId = :legislaturaId AND s.dataDeleted IS NULL")
    List<Stampato> findByLegislaturaIdAndNotDeleted(@Param("legislaturaId") String legislaturaId);
    
    @Query("SELECT s FROM Stampato s WHERE s.legislaturaId = :legislaturaId AND s.dataDeleted IS NOT NULL")
    List<Stampato> findByLegislaturaIdAndDeleted(@Param("legislaturaId") String legislaturaId);

}