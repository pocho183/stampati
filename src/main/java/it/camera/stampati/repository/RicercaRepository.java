package it.camera.stampati.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.camera.stampati.domain.Stampato;
import it.camera.stampati.domain.StampatoId;

@Repository
public interface RicercaRepository extends JpaRepository<Stampato, StampatoId> {

	@Query("SELECT s FROM Stampato s WHERE (s.numeroAtto = %:text%) AND (s.id.legislatura LIKE %:leg%)")
	List<Stampato> searchStampati(@Param("leg") String leg, @Param("text") String text);
	
	Optional<Stampato> findByIdLegislaturaAndIdBarcode(String legislatura, String barcode);

}