package com.ecochain.ecocommonsms.dao;

import com.ecochain.ecocommonsms.entity.SmsHistoryInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  SmsHistoryInfoDao extends IBaseDao<SmsHistoryInfo,Integer> {
    public List<SmsHistoryInfo> findByMobileOrderBySendTimeDesc(String mobile);

    @Query(value="select * from historyinfo s where s.senderid= :senderID order by s.sendtime desc   limit 5",nativeQuery = true)
    public List<SmsHistoryInfo> findBySenderIDOrderBySendTimeDesc(@Param("senderID") Integer senderID);
}
