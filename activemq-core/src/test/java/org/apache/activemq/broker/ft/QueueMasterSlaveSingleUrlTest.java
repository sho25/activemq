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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|TransportConnector
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
name|amq
operator|.
name|AMQPersistenceAdapter
import|;
end_import

begin_class
specifier|public
class|class
name|QueueMasterSlaveSingleUrlTest
extends|extends
name|QueueMasterSlaveTest
block|{
specifier|private
specifier|final
name|String
name|brokerUrl
init|=
literal|"tcp://localhost:62001"
decl_stmt|;
specifier|private
specifier|final
name|String
name|singleUriString
init|=
literal|"failover://("
operator|+
name|brokerUrl
operator|+
literal|")?randomize=false"
decl_stmt|;
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
name|singleUriString
argument_list|)
return|;
block|}
specifier|protected
name|void
name|createMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|master
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"shared-master"
argument_list|)
expr_stmt|;
name|configureSharedPersistenceAdapter
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|master
operator|.
name|addConnector
argument_list|(
name|brokerUrl
argument_list|)
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|configureSharedPersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQPersistenceAdapter
name|adapter
init|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"shared"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createSlave
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
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
literal|"shared-slave"
argument_list|)
expr_stmt|;
name|configureSharedPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|// add transport as a service so that it is bound on start, after store started
specifier|final
name|TransportConnector
name|tConnector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|tConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
name|brokerUrl
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|tConnector
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|.
name|set
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|slaveStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

