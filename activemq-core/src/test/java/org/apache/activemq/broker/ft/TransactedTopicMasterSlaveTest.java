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
name|ft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|JmsTopicTransactionTest
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
name|test
operator|.
name|JmsResourceProvider
import|;
end_import

begin_comment
comment|/**  * Test failover for Queues  */
end_comment

begin_class
specifier|public
class|class
name|TransactedTopicMasterSlaveTest
extends|extends
name|JmsTopicTransactionTest
block|{
specifier|protected
name|BrokerService
name|slave
decl_stmt|;
specifier|protected
name|int
name|inflightMessageCount
decl_stmt|;
specifier|protected
name|int
name|failureCount
init|=
literal|50
decl_stmt|;
specifier|protected
name|String
name|uriString
init|=
literal|"failover://(tcp://localhost:62001,tcp://localhost:62002)?randomize=false"
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|failureCount
operator|=
name|super
operator|.
name|batchCount
operator|/
literal|2
expr_stmt|;
comment|// this will create the main (or master broker)
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|slave
operator|.
name|setBrokerName
argument_list|(
literal|"slave"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setMasterConnectorURI
argument_list|(
literal|"tcp://localhost:62001"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:62002"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for thing to connect
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|resourceProvider
operator|=
name|getJmsResourceProvider
argument_list|()
expr_stmt|;
name|topic
operator|=
name|resourceProvider
operator|.
name|isTopic
argument_list|()
expr_stmt|;
comment|// We will be using transacted sessions.
name|resourceProvider
operator|.
name|setTransacted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionFactory
operator|=
name|resourceProvider
operator|.
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|reconnect
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|slave
operator|.
name|stop
argument_list|()
expr_stmt|;
name|slave
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
throws|,
name|URISyntaxException
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:62001"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|JmsResourceProvider
name|getJmsResourceProvider
parameter_list|()
block|{
name|JmsResourceProvider
name|p
init|=
name|super
operator|.
name|getJmsResourceProvider
argument_list|()
decl_stmt|;
name|p
operator|.
name|setServerUri
argument_list|(
name|uriString
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uriString
argument_list|)
return|;
block|}
specifier|protected
name|void
name|messageSent
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|++
name|inflightMessageCount
operator|>=
name|failureCount
condition|)
block|{
name|inflightMessageCount
operator|=
literal|0
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

