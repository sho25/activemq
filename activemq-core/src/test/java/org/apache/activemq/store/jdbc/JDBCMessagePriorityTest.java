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
name|store
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|command
operator|.
name|ActiveMQTopic
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
name|MessagePriorityTest
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
import|;
end_import

begin_class
specifier|public
class|class
name|JDBCMessagePriorityTest
extends|extends
name|MessagePriorityTest
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JDBCMessagePriorityTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|Exception
block|{
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|EmbeddedDataSource
name|dataSource
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setDatabaseName
argument_list|(
literal|"derbyDb"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setShutdownDatabase
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|jdbc
operator|.
name|setCleanupPeriod
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
return|return
name|jdbc
return|;
block|}
comment|// this cannot be a general test as kahaDB just has support for 3 priority levels
specifier|public
name|void
name|testDurableSubsReconnectWithFourLevels
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|sess
operator|.
name|createTopic
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|subName
init|=
literal|"priorityDisconnect"
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
decl_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|int
name|MED_PRI
init|=
name|LOW_PRI
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
name|MED_HIGH_PRI
init|=
name|HIGH_PRI
operator|-
literal|1
decl_stmt|;
name|ProducerThread
name|lowPri
init|=
operator|new
name|ProducerThread
argument_list|(
name|topic
argument_list|,
name|MSG_NUM
argument_list|,
name|LOW_PRI
argument_list|)
decl_stmt|;
name|ProducerThread
name|medPri
init|=
operator|new
name|ProducerThread
argument_list|(
name|topic
argument_list|,
name|MSG_NUM
argument_list|,
name|MED_PRI
argument_list|)
decl_stmt|;
name|ProducerThread
name|medHighPri
init|=
operator|new
name|ProducerThread
argument_list|(
name|topic
argument_list|,
name|MSG_NUM
argument_list|,
name|MED_HIGH_PRI
argument_list|)
decl_stmt|;
name|ProducerThread
name|highPri
init|=
operator|new
name|ProducerThread
argument_list|(
name|topic
argument_list|,
name|MSG_NUM
argument_list|,
name|HIGH_PRI
argument_list|)
decl_stmt|;
name|lowPri
operator|.
name|start
argument_list|()
expr_stmt|;
name|highPri
operator|.
name|start
argument_list|()
expr_stmt|;
name|medPri
operator|.
name|start
argument_list|()
expr_stmt|;
name|medHighPri
operator|.
name|start
argument_list|()
expr_stmt|;
name|lowPri
operator|.
name|join
argument_list|()
expr_stmt|;
name|highPri
operator|.
name|join
argument_list|()
expr_stmt|;
name|medPri
operator|.
name|join
argument_list|()
expr_stmt|;
name|medHighPri
operator|.
name|join
argument_list|()
expr_stmt|;
specifier|final
name|int
name|closeFrequency
init|=
name|MSG_NUM
decl_stmt|;
specifier|final
name|int
index|[]
name|priorities
init|=
operator|new
name|int
index|[]
block|{
name|HIGH_PRI
block|,
name|MED_HIGH_PRI
block|,
name|MED_PRI
block|,
name|LOW_PRI
block|}
decl_stmt|;
name|sub
operator|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MSG_NUM
operator|*
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|sub
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received i="
operator|+
name|i
operator|+
literal|", m="
operator|+
operator|(
name|msg
operator|!=
literal|null
condition|?
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|", priority: "
operator|+
name|msg
operator|.
name|getJMSPriority
argument_list|()
else|:
literal|null
operator|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Message "
operator|+
name|i
operator|+
literal|" was null"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message "
operator|+
name|i
operator|+
literal|" has wrong priority"
argument_list|,
name|priorities
index|[
name|i
operator|/
name|MSG_NUM
index|]
argument_list|,
name|msg
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|closeFrequency
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing durable sub.. on: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
name|sub
operator|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"closing on done!"
argument_list|)
expr_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestConcurrentDurableSubsReconnectWithXLevels
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prioritizeMessages"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentDurableSubsReconnectWithXLevels
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|topic
init|=
operator|(
name|ActiveMQTopic
operator|)
name|sess
operator|.
name|createTopic
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|subName
init|=
literal|"priorityDisconnect"
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
decl_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|int
name|maxPriority
init|=
literal|5
decl_stmt|;
specifier|final
name|AtomicInteger
index|[]
name|messageCounts
init|=
operator|new
name|AtomicInteger
index|[
name|maxPriority
index|]
decl_stmt|;
name|Vector
argument_list|<
name|ProducerThread
argument_list|>
name|producers
init|=
operator|new
name|Vector
argument_list|<
name|ProducerThread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|priority
init|=
literal|0
init|;
name|priority
operator|<
name|maxPriority
condition|;
name|priority
operator|++
control|)
block|{
name|producers
operator|.
name|add
argument_list|(
operator|new
name|ProducerThread
argument_list|(
name|topic
argument_list|,
name|MSG_NUM
argument_list|,
name|priority
argument_list|)
argument_list|)
expr_stmt|;
name|messageCounts
index|[
name|priority
index|]
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ProducerThread
name|producer
range|:
name|producers
control|)
block|{
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|closeFrequency
init|=
name|MSG_NUM
operator|/
literal|2
decl_stmt|;
name|sub
operator|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MSG_NUM
operator|*
name|maxPriority
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|sub
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received i="
operator|+
name|i
operator|+
literal|", m="
operator|+
operator|(
name|msg
operator|!=
literal|null
condition|?
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|", priority: "
operator|+
name|msg
operator|.
name|getJMSPriority
argument_list|()
else|:
literal|null
operator|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Message "
operator|+
name|i
operator|+
literal|" was null"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|messageCounts
index|[
name|msg
operator|.
name|getJMSPriority
argument_list|()
index|]
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|closeFrequency
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing durable sub.. on: "
operator|+
name|i
operator|+
literal|", counts: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|messageCounts
argument_list|)
argument_list|)
expr_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
name|sub
operator|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"closing on done!"
argument_list|)
expr_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|ProducerThread
name|producer
range|:
name|producers
control|)
block|{
name|producer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JDBCMessagePriorityTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

