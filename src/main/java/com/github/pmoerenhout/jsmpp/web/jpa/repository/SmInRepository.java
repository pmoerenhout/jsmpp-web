package com.github.pmoerenhout.jsmpp.web.jpa.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.pmoerenhout.jsmpp.web.jpa.model.SmIn;

@Repository
public interface SmInRepository extends JpaRepository<SmIn, Long> {

  List<SmIn> findBySmscTimestampAndSourceAndDestinationAndShortMessage(
      @Param("smscTimestamp") ZonedDateTime smscTimestamp,
      @Param("source") String source,
      @Param("destination") String destination,
      @Param("shortMessage") byte[] shortMessage);

  SmIn findOneByMessageIdOrderByTimestampDesc(@Param("messageId") String messageId);

//  List<SmIn> findAllByOrderByTimestamp();
//
//  List<SmIn> findFirstByTimestamp(Instant timestamp, Sort sort);
//
//  List<SmIn> findByMessageIdIsNull();

  @Query("select s from SmIn s where messageId is null order by timestamp asc")
  Stream<SmIn> findOutstanding();

  @Query("select s from SmIn s where messageId = :messageId")
  Stream<SmIn> findByMessageIdAndStream(@Param("messageId") String messageId);

  Stream<SmIn> findAllByMessageIdIsNull();

//  Stream<SmIn> findFirstByMessageIdIsNullAndSubmitTimestampIsNullOrderByTimestampAsc();
//
//  Stream<SmIn> findByMessageIdIsNullAndSubmitTimestampIsNullOrderByTimestampAsc(final Pageable pageable);

  //Stream<SmIn> findFirst5ByMessageIdIsNullByOrderByTimestampAscAndStream();

  List<SmIn> findByDestinationOrderByIdAsc(@Param("destination") String destination, final Pageable pageable);

  List<SmIn> findByDestinationOrderByTimestampAsc(@Param("destination") String destination, final Pageable pageable);

//  @Query("select s from SmIn s where s.dr.messageId is not null")
//  List<SmIn> findAllSmDelivered();
}