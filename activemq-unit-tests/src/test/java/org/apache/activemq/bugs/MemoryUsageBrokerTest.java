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
name|bugs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|BrokerTestSupport
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|kahadb
operator|.
name|KahaDBStore
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
name|util
operator|.
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
specifier|public
class|class
name|MemoryUsageBrokerTest
extends|extends
name|BrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MemoryUsageBrokerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|PolicyEntry
name|getDefaultPolicy
parameter_list|()
block|{
name|PolicyEntry
name|policy
init|=
name|super
operator|.
name|getDefaultPolicy
argument_list|()
decl_stmt|;
comment|// Disable PFC and assign a large memory limit that's larger than the default broker memory limit for queues
name|policy
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setMemoryLimit
argument_list|(
literal|128
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|kaha
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createJmsConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|void
name|testMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|createJmsConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue.a.b"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|bm
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bm
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|bm
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|memoryUsagePercent
init|=
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" messages have been sent; broker memory usage "
operator|+
name|memoryUsagePercent
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Used more than available broker memory"
argument_list|,
name|memoryUsagePercent
operator|<=
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

