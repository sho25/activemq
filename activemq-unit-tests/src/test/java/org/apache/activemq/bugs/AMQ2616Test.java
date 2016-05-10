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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|region
operator|.
name|policy
operator|.
name|FilePendingQueueMessageStoragePolicy
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|ActiveMQQueue
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
name|KahaDBPersistenceAdapter
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
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ2616Test
block|{
annotation|@
name|Rule
specifier|public
name|TestName
name|test
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER
init|=
literal|2000
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|final
name|String
name|ACTIVEMQ_BROKER_BIND
init|=
literal|"tcp://0.0.0.0:0"
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
specifier|public
name|void
name|testQueueResourcesReleased
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|Connection
name|tempConnection
init|=
name|fac
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|tempConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|tempSession
init|=
name|tempConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|tempQueue
init|=
name|tempSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|Connection
name|testConnection
init|=
name|fac
operator|.
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|long
name|startUsage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
decl_stmt|;
name|Session
name|testSession
init|=
name|testConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|testProducer
init|=
name|testSession
operator|.
name|createProducer
argument_list|(
name|tempQueue
argument_list|)
decl_stmt|;
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|4
index|]
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
name|NUMBER
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|msg
init|=
name|testSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|testProducer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|long
name|endUsage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|startUsage
operator|==
name|endUsage
argument_list|)
expr_stmt|;
name|tempConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Usage should return to original"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|==
name|startUsage
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start an embedded broker up.
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|KahaDBPersistenceAdapter
name|adaptor
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|adaptor
operator|.
name|setEnableJournalDiskSyncs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"target/AMQ2616Test"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|adaptor
operator|.
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|adaptor
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|pe
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|pe
operator|.
name|setMemoryLimit
argument_list|(
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|FilePendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|pe
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|20
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|200
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|ACTIVEMQ_BROKER_BIND
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
operator|new
name|ActiveMQQueue
argument_list|(
name|test
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

