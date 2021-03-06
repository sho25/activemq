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
package|;
end_package

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
name|jmx
operator|.
name|ManagementContext
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
name|demo
operator|.
name|DefaultQueueSender
import|;
end_import

begin_comment
comment|/**  * A helper class which can be handy for running a broker in your IDE from the  * activemq-core module.  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Main
block|{
specifier|protected
specifier|static
name|boolean
name|createConsumers
decl_stmt|;
specifier|private
name|Main
parameter_list|()
block|{             }
comment|/**      * @param args      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
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
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// String brokerDir = "xbean:...;
comment|// System.setProperty("activemq.base", brokerDir);
comment|// BrokerService broker = BrokerFactory.createBroker(new URI(brokerDir + "/activemq.xml"));
comment|// for running on Java 5 without mx4j
name|ManagementContext
name|managementContext
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
decl_stmt|;
name|managementContext
operator|.
name|setFindTigerMbeanServer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|managementContext
operator|.
name|setUseMBeanServer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|managementContext
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// broker.setPlugins(new BrokerPlugin[] { new
comment|// ConnectionDotFilePlugin(), new UDPTraceBrokerPlugin() });
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"stomp://localhost:61613"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// lets publish some messages so that there is some stuff to browse
name|DefaultQueueSender
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"Prices.Equity.IBM"
block|}
argument_list|)
expr_stmt|;
name|DefaultQueueSender
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"Prices.Equity.MSFT"
block|}
argument_list|)
expr_stmt|;
comment|// lets create a dummy couple of consumers
if|if
condition|(
name|createConsumers
condition|)
block|{
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Orders.IBM"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Orders.MSFT"
argument_list|)
argument_list|,
literal|"price> 100"
argument_list|)
expr_stmt|;
name|Session
name|session2
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
name|session2
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Orders.MSFT"
argument_list|)
argument_list|,
literal|"price> 200"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Lets wait for the broker
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

