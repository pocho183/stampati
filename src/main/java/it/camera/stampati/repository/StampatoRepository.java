package it.camera.stampati.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.domain.StampatoId;

@Repository
public interface StampatoRepository extends JpaRepository<Stampato, StampatoId> {
	
	boolean existsById(StampatoId id);

    List<Stampato> findById_Legislatura(String legislatura);
    
    Optional<Stampato> findByIdLegislaturaAndIdBarcode(String legislatura, String barcode);

    @Query("SELECT s FROM Stampato s WHERE s.id.legislatura = :legislatura AND s.dataDeleted IS NULL")
    List<Stampato> findByLegislaturaAndNotDeleted(@Param("legislatura") String legislatura);

    @Query("SELECT s FROM Stampato s WHERE s.id.legislatura = :legislatura AND s.dataDeleted IS NOT NULL")
    List<Stampato> findByLegislaturaAndDeleted(@Param("legislatura") String legislatura);

    @Query(value = """
    	    SELECT * FROM stampato ORDER BY 
    	        CAST(SUBSTRING(barcode FROM 1 FOR 2) AS INTEGER) DESC, 
    	        CAST(REGEXP_SUBSTR(barcode, '[0-9]+$', 1, 1) AS INTEGER) DESC LIMIT 1
    	""", nativeQuery = true)
    Optional<Stampato> findLastInserted();

}

