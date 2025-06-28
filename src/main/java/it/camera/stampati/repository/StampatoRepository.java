package it.camera.stampati.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.domain.StampatoId;
import jakarta.persistence.TemporalType;

@Repository
public interface StampatoRepository extends JpaRepository<Stampato, StampatoId>, CustomRepository<Stampato, StampatoId> {

	@Override
	boolean existsById(StampatoId id);

    List<Stampato> findById_Legislatura(String legislatura);

    Optional<Stampato> findByIdLegislaturaAndIdBarcode(String legislatura, String barcode);

    @Query("SELECT s FROM Stampato s WHERE s.id.legislatura = :legislatura AND s.dataDeleted IS NULL")
    List<Stampato> findByLegislaturaAndNotDeleted(@Param("legislatura") String legislatura);

    @Query("SELECT s FROM Stampato s WHERE s.id.legislatura = :legislatura AND s.dataDeleted IS NOT NULL")
    List<Stampato> findByLegislaturaAndDeleted(@Param("legislatura") String legislatura);

    // Da rivedere e testare
    @Query(value = """
    	    SELECT * FROM stampato ORDER BY
    	        CAST(SUBSTRING(barcode FROM 1 FOR 2) AS INTEGER) DESC,
    	        CAST(REGEXP_SUBSTR(barcode, '[0-9]+$', 1, 1) AS INTEGER) DESC LIMIT 1
    	""", nativeQuery = true)
    Optional<Stampato> findLastInserted();
    
	@Query("SELECT s FROM Stampato s WHERE s.id.legislatura = :legislatura AND s.numeroAtto = :atto AND (:navette = null or s.navette = :navette)")
	public List<Stampato> findStampatiByNumeroAttoAndNavette(@Param("legislatura") String legislatura, @Param ("atto") String atto, @Param ("navette") Character navette);
	
	@Query("SELECT s FROM Stampato s WHERE (s.pdfPresente = false OR s.pdfPresente IS NULL) AND s.id.legislatura = :legislatura ORDER BY s.createdAt DESC")
	public List<Stampato> findLastFourStampatiData(@Param("legislatura") String legislatura);

	@Query("SELECT s FROM Stampato s WHERE (s.pdfPresente = false OR s.pdfPresente IS NULL) AND s.dataDeleted IS NULL AND s.id.legislatura = :legislatura AND s.createdAt BETWEEN :date1 AND :date2 ORDER BY s.numeroAtto desc")
	public List<Stampato> findStampatiData(@Param("legislatura") String legislatura, @Param("date1") @Temporal(TemporalType.DATE) Date date1, @Param("date2") @Temporal(TemporalType.DATE) Date date2);

	@Query("SELECT s from Stampato s WHERE s.id.legislatura = :leg and s.numeroAtto = :atto and (:navette = null or s.navette = :navette)")
	public List<Stampato> findStampatiByIdLegislaturaAndNumeroAttoAndNavette(@Param ("leg") String leg, @Param ("atto") String atto, @Param ("navette") Character navette);
	
}

