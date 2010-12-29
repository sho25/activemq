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
name|advisory
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|advisory
operator|.
name|AdvisorySupport
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
name|ActiveMQMessage
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
name|BrokerInfo
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
name|MessageConsumer
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_class
specifier|public
class|class
name|AdvisoryNetworkBridgeTest
extends|extends
name|TestCase
block|{
name|BrokerService
name|broker1
decl_stmt|;
name|BrokerService
name|broker2
decl_stmt|;
specifier|public
name|void
name|testAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|broker1
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:org/apache/activemq/network/reconnect-broker1.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|broker1
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker1
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|getNetworkBridgeAdvisoryTopic
argument_list|()
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|broker2
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:org/apache/activemq/network/reconnect-broker2.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|broker2
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ActiveMQMessage
name|advisory
init|=
operator|(
name|ActiveMQMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|advisory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|advisory
operator|.
name|getDataStructure
argument_list|()
operator|instanceof
name|BrokerInfo
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|advisory
operator|.
name|getBooleanProperty
argument_list|(
literal|"started"
argument_list|)
argument_list|)
expr_stmt|;
name|broker2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|advisory
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|advisory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|advisory
operator|.
name|getDataStructure
argument_list|()
operator|instanceof
name|BrokerInfo
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|advisory
operator|.
name|getBooleanProperty
argument_list|(
literal|"started"
argument_list|)
argument_list|)
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
name|broker1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker1
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker2
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

