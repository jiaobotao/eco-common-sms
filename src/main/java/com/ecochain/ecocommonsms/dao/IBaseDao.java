package com.ecochain.ecocommonsms.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IBaseDao <T, ID extends Serializable> extends JpaRepository<T, ID> {
    //以下自定义接口方法
}
