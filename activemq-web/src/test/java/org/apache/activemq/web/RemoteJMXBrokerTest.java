begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
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
name|assertEquals
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
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CountDownLatch
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
name|ExecutorService
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
name|Executors
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
name|remote
operator|.
name|JMXConnectorServer
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
name|broker
operator|.
name|jmx
operator|.
name|DestinationViewMBean
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
name|util
operator|.
name|TestUtils
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
name|web
operator|.
name|config
operator|.
name|SystemPropertiesConfiguration
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
name|Test
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  *  * You can use this class to connect up to a running web console and run some queries.  * Used to work through https://issues.apache.org/jira/browse/AMQ-4272 but would be useful  * in any scenario where you need access to the underlying broker in the web-console to hack  * at it  *  */
end_comment

begin_class
specifier|public
class|class
name|RemoteJMXBrokerTest
block|{
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:()/remoteBroker?useJmx=true"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setConnectorPort
argument_list|(
name|TestUtils
operator|.
name|findOpenPort
argument_list|()
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
name|String
name|jmxUri
init|=
name|getJmxUri
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"webconsole.jmx.url"
argument_list|,
name|jmxUri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
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
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test that we can query the remote broker...      * Specifically this tests that the domain and objectnames are correct (type and brokerName      * instead of Type and BrokerName, which they were)      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testConnectRemoteBrokerFacade
parameter_list|()
throws|throws
name|Exception
block|{
name|RemoteJMXBrokerFacade
name|brokerFacade
init|=
operator|new
name|RemoteJMXBrokerFacade
argument_list|()
decl_stmt|;
name|SystemPropertiesConfiguration
name|configuration
init|=
operator|new
name|SystemPropertiesConfiguration
argument_list|()
decl_stmt|;
name|brokerFacade
operator|.
name|setConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=remoteBroker"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|brokerFacade
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Number: "
operator|+
name|queryResult
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queryResult
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Before AMQ-5896 there was the possibility of an InstanceNotFoundException when      * brokerFacade.getQueue if a destination was deleted after the initial list was looked      * up but before iterating over the list to find the right destination by name.      *      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testGetDestinationRaceCondition
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|getQueuesLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|destDeletionLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Adding a pause so we can test the case where the destination is
comment|// deleted in between calling getQueues() and iterating over the list
comment|//and calling getName() on the DestinationViewMBean
comment|// See AMQ-5896
name|RemoteJMXBrokerFacade
name|brokerFacade
init|=
operator|new
name|RemoteJMXBrokerFacade
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|DestinationViewMBean
name|getDestinationByName
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|DestinationViewMBean
argument_list|>
name|collection
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
comment|//we are done getting the queue collection so let thread know
comment|//to remove destination
name|getQueuesLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|//wait until other thread is done removing destination
name|destDeletionLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
return|return
name|super
operator|.
name|getDestinationByName
argument_list|(
name|collection
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|SystemPropertiesConfiguration
name|configuration
init|=
operator|new
name|SystemPropertiesConfiguration
argument_list|()
decl_stmt|;
name|brokerFacade
operator|.
name|setConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
comment|//Create the destination
specifier|final
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue.test"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|getDestination
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|//after 1 second delete
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
name|service
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//wait for confirmation that the queue list was obtained
name|getQueuesLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|removeDestination
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|//let original thread know destination was deleted
name|destDeletionLatch
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
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
comment|//Assert that the destination is now null because it was deleted in another thread
comment|//during iteration
name|assertNull
argument_list|(
name|brokerFacade
operator|.
name|getQueue
argument_list|(
name|queue
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getJmxUri
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|field
init|=
name|ManagementContext
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"connectorServer"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JMXConnectorServer
name|server
init|=
operator|(
name|JMXConnectorServer
operator|)
name|field
operator|.
name|get
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|server
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

