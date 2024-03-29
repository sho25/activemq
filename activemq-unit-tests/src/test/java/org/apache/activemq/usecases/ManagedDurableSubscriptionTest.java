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
name|usecases
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
name|Session
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
name|BrokerFactory
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
name|leveldb
operator|.
name|LevelDBStore
import|;
end_import

begin_class
specifier|public
class|class
name|ManagedDurableSubscriptionTest
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
block|{
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|public
name|void
name|testJMXSubscriptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create durable subscription
name|Session
name|session
init|=
name|connection
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// restart the broker
name|stopBroker
argument_list|()
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
name|ObjectName
name|inactiveSubscriptionObjectName
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Object
name|inactive
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|inactiveSubscriptionObjectName
argument_list|,
literal|"Active"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Subscription is active."
argument_list|,
name|Boolean
operator|.
name|FALSE
operator|.
name|equals
argument_list|(
name|inactive
argument_list|)
argument_list|)
expr_stmt|;
comment|// activate
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|)
expr_stmt|;
name|ObjectName
name|activeSubscriptionObjectName
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Object
name|active
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|activeSubscriptionObjectName
argument_list|,
literal|"Active"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Subscription is INactive."
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|active
argument_list|)
argument_list|)
expr_stmt|;
comment|// deactivate
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
name|inactive
operator|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|inactiveSubscriptionObjectName
argument_list|,
literal|"Active"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Subscription is active."
argument_list|,
name|Boolean
operator|.
name|FALSE
operator|.
name|equals
argument_list|(
name|inactive
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://localhost)"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setKeepDurableSubsActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LevelDBStore
name|persistenceAdapter
init|=
operator|new
name|LevelDBStore
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"activemq-data/"
operator|+
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
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
literal|"vm://"
operator|+
name|getName
argument_list|()
operator|+
literal|"?waitForStart=5000&create=false"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
argument_list|()
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|rc
init|=
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

