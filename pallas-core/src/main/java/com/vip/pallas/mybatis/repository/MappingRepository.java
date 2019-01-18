/**
 * Copyright 2019 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.vip.pallas.mybatis.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.vip.pallas.mybatis.entity.Mapping;

@Repository
public interface MappingRepository {
    int deleteByPrimaryKey(Long id);

    int insert(Mapping record);
    
    int insertBatch(List<Mapping> list);

    int insertSelective(Mapping record);

    Mapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Mapping record);

    int updateByPrimaryKey(Mapping record);
    
    List<String> selectMultiFieldsByVersionId(Long versionId);
    
    List<Mapping> selectByVersionId(Long versionId);
    
    void deleteByVersionId(Long versionId);
}