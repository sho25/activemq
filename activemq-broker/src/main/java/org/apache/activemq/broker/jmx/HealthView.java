begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|scheduler
operator|.
name|JobSchedulerStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_class
specifier|public
class|class
name|HealthView
implements|implements
name|HealthViewMBean
block|{
name|ManagedRegionBroker
name|broker
decl_stmt|;
name|String
name|currentState
init|=
literal|"Good"
decl_stmt|;
specifier|public
name|HealthView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|health
parameter_list|()
throws|throws
name|Exception
block|{
name|OpenTypeSupport
operator|.
name|OpenTypeFactory
name|factory
init|=
name|OpenTypeSupport
operator|.
name|getFactory
argument_list|(
name|HealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|CompositeType
name|ct
init|=
name|factory
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
literal|"HealthStatus"
argument_list|,
literal|"HealthStatus"
argument_list|,
name|ct
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"healthId"
block|,
literal|"level"
block|,
literal|"message"
block|,
literal|"resource"
block|}
argument_list|)
decl_stmt|;
name|TabularDataSupport
name|rc
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HealthStatus
argument_list|>
name|list
init|=
name|healthList
argument_list|()
decl_stmt|;
for|for
control|(
name|HealthStatus
name|healthStatus
range|:
name|list
control|)
block|{
name|rc
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|factory
operator|.
name|getFields
argument_list|(
name|healthStatus
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|HealthStatus
argument_list|>
name|healthList
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|HealthStatus
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|HealthStatus
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ObjectName
argument_list|,
name|DestinationView
argument_list|>
name|queueViews
init|=
name|broker
operator|.
name|getQueueViews
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ObjectName
argument_list|,
name|DestinationView
argument_list|>
name|entry
range|:
name|queueViews
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DestinationView
name|queue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|getConsumerCount
argument_list|()
operator|==
literal|0
operator|&&
name|queue
operator|.
name|getProducerCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ObjectName
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"Queue "
operator|+
name|queue
operator|.
name|getName
argument_list|()
operator|+
literal|" has no consumers"
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.noConsumer"
argument_list|,
literal|"WARNING"
argument_list|,
name|message
argument_list|,
name|key
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Check persistence store directory limits          *          */
name|BrokerService
name|brokerService
init|=
name|broker
operator|.
name|getBrokerService
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerService
operator|!=
literal|null
operator|&&
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PersistenceAdapter
name|adapter
init|=
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
name|adapter
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|SystemUsage
name|usage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
operator|&&
name|usage
operator|!=
literal|null
condition|)
block|{
name|String
name|dirPath
init|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|dir
operator|!=
literal|null
operator|&&
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|dir
operator|=
name|dir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
name|long
name|storeSize
init|=
name|adapter
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|storeLimit
init|=
name|usage
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|long
name|dirFreeSpace
init|=
name|dir
operator|.
name|getUsableSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeSize
operator|!=
literal|0
condition|)
block|{
name|int
name|val
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|storeSize
operator|*
literal|100
operator|)
operator|/
name|storeLimit
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|>
literal|90
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.StoreLimit"
argument_list|,
literal|"WARNING"
argument_list|,
literal|"Message Store size is within "
operator|+
name|val
operator|+
literal|"% of its limit"
argument_list|,
name|adapter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|storeLimit
operator|-
name|storeSize
operator|)
operator|>
name|dirFreeSpace
condition|)
block|{
name|String
name|message
init|=
literal|"Store limit is "
operator|+
name|storeLimit
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|" mb, whilst the data directory: "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" only has "
operator|+
name|dirFreeSpace
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|" mb of usable space"
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.FreeDiskSpaceLeft"
argument_list|,
literal|"WARNING"
argument_list|,
name|message
argument_list|,
name|adapter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|File
name|tmpDir
init|=
name|brokerService
operator|.
name|getTmpDataDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|tmpDir
operator|!=
literal|null
condition|)
block|{
name|String
name|tmpDirPath
init|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|tmpDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
name|tmpDirPath
argument_list|)
expr_stmt|;
block|}
name|long
name|storeSize
init|=
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
decl_stmt|;
name|long
name|storeLimit
init|=
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
decl_stmt|;
while|while
condition|(
name|tmpDir
operator|!=
literal|null
operator|&&
operator|!
name|tmpDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|tmpDir
operator|=
name|tmpDir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
name|int
name|val
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|storeSize
operator|*
literal|100
operator|)
operator|/
name|storeLimit
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|>
literal|90
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.TempStoreLimit"
argument_list|,
literal|"WARNING"
argument_list|,
literal|"TempMessage Store size is within "
operator|+
name|val
operator|+
literal|"% of its limit"
argument_list|,
name|adapter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|brokerService
operator|!=
literal|null
operator|&&
name|brokerService
operator|.
name|getJobSchedulerStore
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|JobSchedulerStore
name|scheduler
init|=
name|brokerService
operator|.
name|getJobSchedulerStore
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
name|scheduler
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|SystemUsage
name|usage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
operator|&&
name|usage
operator|!=
literal|null
condition|)
block|{
name|String
name|dirPath
init|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|dir
operator|!=
literal|null
operator|&&
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|dir
operator|=
name|dir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
name|long
name|storeSize
init|=
name|scheduler
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|storeLimit
init|=
name|usage
operator|.
name|getJobSchedulerUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|long
name|dirFreeSpace
init|=
name|dir
operator|.
name|getUsableSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeSize
operator|!=
literal|0
condition|)
block|{
name|int
name|val
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|storeSize
operator|*
literal|100
operator|)
operator|/
name|storeLimit
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|>
literal|90
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.JobSchedulerLimit"
argument_list|,
literal|"WARNING"
argument_list|,
literal|"JobSchedulerMessage Store size is within "
operator|+
name|val
operator|+
literal|"% of its limit"
argument_list|,
name|scheduler
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|storeLimit
operator|-
name|storeSize
operator|)
operator|>
name|dirFreeSpace
condition|)
block|{
name|String
name|message
init|=
literal|"JobSchedulerStore limit is "
operator|+
name|storeLimit
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|" mb, whilst the data directory: "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" only has "
operator|+
name|dirFreeSpace
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|" mb of usable space"
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
operator|new
name|HealthStatus
argument_list|(
literal|"org.apache.activemq.FreeDiskSpaceLeft"
argument_list|,
literal|"WARNING"
argument_list|,
name|message
argument_list|,
name|scheduler
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|answer
operator|!=
literal|null
operator|&&
operator|!
name|answer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|currentState
operator|=
literal|"Feeling Ill {"
expr_stmt|;
for|for
control|(
name|HealthStatus
name|hs
range|:
name|answer
control|)
block|{
name|currentState
operator|+=
name|hs
operator|+
literal|" , "
expr_stmt|;
block|}
name|currentState
operator|+=
literal|" }"
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|currentState
operator|=
literal|"Good"
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * @return String representation of the current Broker state      */
annotation|@
name|Override
specifier|public
name|String
name|getCurrentStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentState
return|;
block|}
block|}
end_class

end_unit

