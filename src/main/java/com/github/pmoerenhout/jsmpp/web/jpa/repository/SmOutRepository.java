package com.github.pmoerenhout.jsmpp.web.jpa.repository;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.pmoerenhout.jsmpp.web.jpa.model.SmOut;

@Repository
public interface SmOutRepository extends JpaRepository<SmOut, Long> {

  SmOut findOneByMessageIdOrderByTimestampDesc(@Param("messageId") String messageId);

  SmOut findOneByConnectionIdInAndMessageIdAndSourceAndDestination(@Param("connectionId") List<String> connectionIds, @Param("messageId") String messageId,
                                                                   @Param("source") String source, @Param("destination") String destination);

//  List<SmOut> findAllByOrderByTimestamp();
//
//  List<SmOut> findFirstByTimestamp(Instant timestamp, Sort sort);
//
//  List<SmOut> findByMessageIdIsNull();

  @Query("select s from SmOut s where messageId is null order by timestamp asc")
  Stream<SmOut> findOutstanding();

  @Query("select s from SmOut s where messageId = :messageId")
  Stream<SmOut> findByMessageIdAndStream(@Param("messageId") String messageId);

  Stream<SmOut> findAllByMessageIdIsNull();

//  Stream<SmOut> findFirstByMessageIdIsNullAndSubmitTimestampIsNullOrderByTimestampAsc();
//
//  Stream<SmOut> findByMessageIdIsNullAndSubmitTimestampIsNullOrderByTimestampAsc(final Pageable pageable);

  //Stream<SmOut> findFirst5ByMessageIdIsNullByOrderByTimestampAscAndStream();

  List<SmOut> findByDestinationOrderByIdAsc(@Param("destination") String destination, final Pageable pageable);

  List<SmOut> findByDestinationOrderByTimestampAsc(@Param("destination") String destination, final Pageable pageable);

//  @Query("select s from SmOut s where s.dr.messageId is not null")
//  List<SmOut> findAllSmDelivered();
}